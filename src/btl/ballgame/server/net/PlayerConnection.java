package btl.ballgame.server.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import btl.ballgame.protocol.ConnectionCtx;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.IPacketPlayOut;
import btl.ballgame.protocol.packets.out.PacketPlayOutCloseSocket;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.ArkaPlayer;

/**
 * Represents a connection between the server and a single player client.
 * Handles both receiving packets from the client and sending packets to the client.
 * <p>
 * Each connection spawns two background threads:
 * <ul>
 *     <li>Packet Listener Thread: Reads incoming packets from the client.</li>
 *     <li>Packet Dispatcher Thread: Sends queued outgoing packets to the client.</li>
 * </ul>
 */
public class PlayerConnection implements ConnectionCtx {
    /** Interval (in milliseconds) to wait between reading packets from the client. */
	public static final int CLIENT_READ_INTERVAL = 10;
    /** Interval (in milliseconds) to wait between dispatching outgoing packets. */
	public static final int PACKET_DISPATCH_INTERVAL = 5;
	
	private Socket clientSocket;
	private DataInputStream receiveStream;
	private DataOutputStream sendStream;
	
	private Queue<NetworkPacket> dispatchQueue = new ConcurrentLinkedQueue<>();
	
	private boolean closed = false;
	
	private ArkanoidServer server;
	private ArkaPlayer owner;
	
	@SuppressWarnings("unchecked")    
	/**
	 * Creates a new {@link PlayerConnection} for the given client socket and
	 * server. Initializes streams and spawns packet listener and dispatcher
	 * threads.
	 * 
	 * @param server the game server managing this connection
	 * @param socket the client socket representing the connection
	 * @throws IOException if an I/O error occurs when creating the input/output
	 *                     streams
	 */
	public PlayerConnection(ArkanoidServer server, Socket socket) throws IOException {
		this.server = server;
		this.clientSocket = socket;
		this.sendStream = new DataOutputStream(socket.getOutputStream());
		this.receiveStream = new DataInputStream(socket.getInputStream());
		
		new Thread(() -> {
			Thread.currentThread().setName("PlayerConnection: Packet Listener Thread");
			while (!closed) {
				try {
					Thread.sleep(CLIENT_READ_INTERVAL);
					NetworkPacket packet = server.codec().readPacket(receiveStream);
					PacketHandler<?, ?> handler = server.getRegistry().getHandle(packet.getClass());
					((PacketHandler<NetworkPacket, PlayerConnection>) handler).handle(packet, this);
				} catch (Exception e) {
					handleConnectionException(e);
				}
			}
		}).start();
		
		new Thread(() -> {
			Thread.currentThread().setName("PlayerConnection: Packet Dispatcher Thread");
			while (!closed) {
				try {
					Thread.sleep(PACKET_DISPATCH_INTERVAL);
					flush();
				} catch (Exception e) {
					handleConnectionException(e);
				}
			}
		}).start();
	}
	
    /**
     * Associates this connection with a Arkanoid player.
     * 
     * @param player the {@link ArkaPlayer} that owns this connection
     */
	public void attachTo(ArkaPlayer p) {
		this.owner = p;
	}
	
    /**
     * Returns the player associated with this connection.
     * 
     * @return the {@link ArkaPlayer} owning this connection, or {@code null} if not attached
     */
	public ArkaPlayer getPlayer() {
		return this.owner;
	}
	
	/**
	 * Sends all queued packets to the client. Synchronized to ensure thread-safe
	 * flushing.
	 * 
	 * @throws IOException if an I/O error occurs while sending packets
	 */
	private synchronized void flush() throws IOException {
		if (closed || dispatchQueue.isEmpty()) return;
		NetworkPacket packet;
		while ((packet = dispatchQueue.poll()) != null) {
			server.codec().writePacket(sendStream, packet);
		}
		sendStream.flush();
	}
	
	/**
	 * Queues a packet for sending to the client.
	 * 
	 * @param packet the packet to send
	 * @throws IllegalArgumentException if the packet is not an instance of
	 *                                  {@link NetworkPacket}
	 */
	public void sendPacket(IPacketPlayOut packet) {
		if (!(packet instanceof NetworkPacket)) {
			throw new IllegalArgumentException("Cannot dispatch " + packet.getClass().getName() + "! Malformed blueprint.");
		}
		this.sendPacket((NetworkPacket) packet, false, false);
	}
	
	/**
	 * Queues a {@link NetworkPacket} for sending. Optionally sends the packet
	 * immediately if {@code instant} is true.
	 * 
	 * @param packet  the packet to send
	 * @param instant whether to flush the packet immediately
	 * @param ignoreFlushExceptions whether to fail silently (only if instant is true) or not
	 */
	protected void sendPacket(NetworkPacket packet, boolean instant, boolean ignoreFlushExceptions) {
		if (closed) return;
		dispatchQueue.add(packet);
		if (instant) {
			try {
				flush();
			} catch (Exception e) {
				if (!ignoreFlushExceptions) handleConnectionException(e);
			}
		}
	}
	
	/**
	 * Closes the connection gracefully with an optional reason. 
	 * This should notifies the client before closing.
	 * 
	 * @param reason the reason for closing the connection
	 */
	public void closeWithNotify(String reason) {
		if (closed) return;
		sendPacket(new PacketPlayOutCloseSocket(reason), true, true);
		closeConnection();
	}
	
	/**
	 * Immediately destroys this player connection without notifying the client.
	 * Closes the underlying socket, marks the connection as closed, and notifies
	 * the associated player (if any) that the connection has been terminated.
	 */
	public void closeConnection() {
		if (closed) return;
		this.closed = true;
		// only do this if the client socket is still open
		if (!clientSocket.isClosed()) {
			try { clientSocket.close(); } catch (IOException ignored) {}
		}
		// notify the internal server impl to handle quit event
		if (owner == null) return;
		owner.onPlayerConnectionClose();
		attachTo(null);
	}
	
	private void handleGracefulDisconnect() {
		closeConnection();
	}
	
	/**
	 * Handles exceptions occurring in packet listener or dispatcher threads.
	 * Automatically closes or destroys the connection depending on the exception
	 * type.
	 * 
	 * @param e the exception that occurred
	 */
	private void handleConnectionException(Throwable e) {
		if (closed) return;
		if (e instanceof EOFException) {
			handleGracefulDisconnect(); // the client probably died, so /shrug
			return;
		}
		if (e instanceof SocketException) {
			closeWithNotify("Connection reset by peer");
			return;
		}
		if (e instanceof SocketTimeoutException) {
			closeWithNotify("Timed out");
			return;
		}
		if (e instanceof IOException) {
			closeWithNotify("Network error");
			return;
		}
		closeWithNotify(e.getClass().getPackageName() + "." + e.getClass().getName() + ": " + e.getMessage());
	}
}
