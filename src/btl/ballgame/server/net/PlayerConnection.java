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
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import btl.ballgame.protocol.ConnectionCtx;
import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.IPacketPlayOut;
import btl.ballgame.protocol.packets.out.PacketPlayOutCloseSocket;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityPosition;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.shared.libs.DataWatcher;
import btl.ballgame.shared.libs.Location;
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
	private static final ScheduledExecutorService GLOBAL_SCHEDULER = Executors.newSingleThreadScheduledExecutor();
	
	private Socket clientSocket;
	private DataInputStream receiveStream;
	private DataOutputStream sendStream;
	
	private ConcurrentLinkedDeque<NetworkPacket> dispatchQueue = new ConcurrentLinkedDeque<>();
	
	private boolean closed = false;
	
	private ArkanoidServer server;
	private ArkaPlayer owner;
	
	private Thread packetListenerThread;
	private Thread packetDispatcherThread;
	
	private final Semaphore flushSignal = new Semaphore(0); // basically a notifier
	
	// special flag
	private boolean validClient = false; // every connection is garbage until proven otherwise
	
	/**
	 * Creates a new {@link PlayerConnection} for the given client socket and
	 * server. Initializes streams and spawns packet listener and dispatcher
	 * threads.
	 * 
	 * @apiNote This connection uses a PASSIVE DISPATCHER model!<br>
	 * Outbound packets (to the client) are not pushed automatically.
	 * To trigger transmission, {@link PlayerConnection#notifyDispatcher()} 
	 * must be called!
	 * 
	 * @param server the game server managing this connection
	 * @param socket the client socket representing the connection
	 * @throws IOException if an I/O error occurs when creating the input/output
	 *                     streams
	 */
	@SuppressWarnings("unchecked")    
	public PlayerConnection(ArkanoidServer server, Socket socket) throws IOException {
		this.server = server;
		this.clientSocket = socket;
		
		this.sendStream = new DataOutputStream(socket.getOutputStream());
		this.receiveStream = new DataInputStream(socket.getInputStream());
		
		// PWP specifications (SERVER): RECEIVE magic bytes (0x544824) / SEND magic bytes (0x24E12)
		if (receiveStream.readInt() == 0x544824) {
			sendStream.writeInt(0x24E12);
		} else {
			socket.close();
			throw new IOException("PWP Protocol: Invalid Handshake!");
		}
		
		// packet listening thread
		this.packetListenerThread = new Thread(() -> {
			Thread.currentThread().setName("PlayerConnection: Packet Listener Thread");
			while (!closed) {
				try {
					Thread.sleep(50);
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
					flushSignal.acquire(); // block until flushSignal release
					Thread.sleep(50);
					synchronized (sendStream) {
						if (this.dispatchQueue.isEmpty()) continue;
						for (NetworkPacket p : dispatchQueue) {
							server.codec().writePacket(sendStream, p);
						}
						sendStream.flush();
						dispatchQueue.clear();
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
		
		// allow this connection 1 second to prove that it is not garbage
		GLOBAL_SCHEDULER.schedule(() -> {
			if (!this.isValidClient()) {
				this.closeConnection();
			}
		}, 1, TimeUnit.SECONDS);
	}
	
	/**
	 * @return true if this connection has successfully completed 
	 *  the client-hello handshake.
	 */
	public boolean isValidClient() {
		return this.validClient;
	}
	
	/**
	 * Mark this connection as a "valid" client.
	 * 
	 * Basically this connection has completed a client-hello handshake.
	 */
	public void completeHandshake() {
		this.validClient = true; // set this to true so it wont be killed
	}
	
    /**
     * Signals the internal packet dispatcher thread to wake up and flush
     * all pending outbound packets currently queued for this connection.
     */
	public void notifyDispatcher() {
	    flushSignal.release();
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
	 * @return true if there is a {@link ArkaPlayer} owning this connection,
	 * false otherwise
	 */
	public boolean hasPlayer() {
		return getPlayer() != null;
	}
	
	/**
	 * Queues a sequence of packets for sending to the client.
	 * 
	 * @param packets the sequence of packets to send
	 * @throws IllegalArgumentException if the packet is not an instance of
	 *                                  {@link NetworkPacket}
	 */
	public void sendPackets(IPacketPlayOut... packets) {
		for (IPacketPlayOut packetPlayOut : packets) {
			this.sendPacket(packetPlayOut);
		}
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
	
	public void closeForViolation() {
		this.closeWithNotify("Invalid packet order!");
	}
	
	/**
	 * Closes the connection gracefully with an optional reason. 
	 * This should notifies the client before closing.
	 * 
	 * @param reason the reason for closing the connection
	 */
	public void closeWithNotify(String reason) {
		this.dispatchLastPacketAndClose(new PacketPlayOutCloseSocket(reason));
	}
	
	/**
	 * Closes the connection gracefully with an last packet.
	 * 
	 * @param lastPacket the last packet to send before closing
	 */
	public void dispatchLastPacketAndClose(IPacketPlayOut lastPacket) {
		if (closed) return;
		// dispatch the last packet immediately, this bypasses the queue
		synchronized (sendStream) {
			try {
				server.codec().writePacket(sendStream, (NetworkPacket) lastPacket);
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
		// debug info
		System.out.println("[Network] User with IP: " + clientSocket.getInetAddress().toString() + " disconnected!");
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
			closeWithNotify("Network error: " + e.toString());
			return;
		}
		closeWithNotify(e.toString());
	}
}
