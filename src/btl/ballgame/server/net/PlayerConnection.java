package btl.ballgame.server.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

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
	private Socket clientSocket;
	private DataInputStream receiveStream;
	private DataOutputStream sendStream;
	
	private LinkedBlockingQueue<NetworkPacket> dispatchQueue = new LinkedBlockingQueue<>();
	
	private boolean closed = false;
	
	private ArkanoidServer server;
	private ArkaPlayer owner;
	
	private Thread packetListenerThread;
	private Thread packetDispatcherThread;
	
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
		
		// packet listening thread
		this.packetListenerThread = new Thread(() -> {
			Thread.currentThread().setName("PlayerConnection: Packet Listener Thread");
			while (!closed) {
				try {
					// read the next packet from the stream
					NetworkPacket packet = server.codec().readPacket(receiveStream);
					// find the handle of the packet and then invoke it
					PacketHandler<?, ?> handler = server.getRegistry().getHandle(packet.getClass());
					((PacketHandler<NetworkPacket, PlayerConnection>) handler).handle(packet, this);
				} catch (Exception e) {
					handleConnectionException(e);
				}
			}
		});
		
		// packet dispatching thread
		this.packetDispatcherThread = new Thread(() -> {
			Thread.currentThread().setName("PlayerConnection: Packet Dispatcher Thread");
			while (true) {
				try {
					NetworkPacket first = dispatchQueue.take(); // this blocks until there's something to pick up
					List<NetworkPacket> batch = new ArrayList<>();
					batch.add(first);
					dispatchQueue.drainTo(batch); // grab any other queued packets immediately
					synchronized (sendStream) {
						for (NetworkPacket p : batch) {
							server.codec().writePacket(sendStream, p);
						}
						sendStream.flush();
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				} catch (Exception e) {
					handleConnectionException(e);
				}
			}
		});
		
		this.packetDispatcherThread.start();
		this.packetListenerThread.start();
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
	 * @return the {@link ArkaPlayer} owning this connection, or {@code null} if not
	 *         attached
	 */
	public ArkaPlayer getPlayer() {
		return this.owner;
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
		dispatchQueue.add((NetworkPacket) packet);
	}
	
	/**
	 * Closes the connection gracefully with an optional reason. 
	 * This should notifies the client before closing.
	 * 
	 * @param reason the reason for closing the connection
	 */
	public void closeWithNotify(String reason) {
		if (closed) return;
		// dispatch a disconnect reason immediately, this bypasses the queue
		synchronized (sendStream) {
			try {
				server.codec().writePacket(sendStream, new PacketPlayOutCloseSocket(reason));
				sendStream.flush();
			} catch (IOException e) {}
		}
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
			try { 
				receiveStream.close();
				sendStream.close();
				clientSocket.close(); 
			} catch (IOException ignored) {}
		}
		// stop the dispatcher thread
		this.packetDispatcherThread.interrupt();
		// untrack this instance
		server.getNetworkManager().untrack(this);
		// notify the internal server impl to handle quit event
		if (owner == null) return;
		owner.onPlayerConnectionClose();
		attachTo(null);
	}
	
	public void handleGracefulDisconnect() {
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
		closeWithNotify(e.getClass().getName() + ": " + e.getMessage());
	}
}
