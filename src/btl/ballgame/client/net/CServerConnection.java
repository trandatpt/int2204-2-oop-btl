package btl.ballgame.client.net;

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

import btl.ballgame.client.ArkanoidClient;
import btl.ballgame.protocol.ConnectionCtx;
import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.IPacketPlayIn;
import btl.ballgame.protocol.packets.in.PacketPlayInDisconnect;

/**
 * Represents an active network connection from the client to the game server.
 * Handles both sending packets to the server and receiving packets from the server.
 * <p>
 * Each connection spawns two background threads:
 * <ul>
 *     <li>Packet Listener Thread: Continuously reads incoming packets from the server.</li>
 *     <li>Packet Dispatcher Thread: Sends queued outgoing packets to the server.</li>
 * </ul>
 * <p>
 * Manages the underlying socket and I/O streams, queues outgoing packets for batch sending,
 * and handles graceful or forced disconnection. Also manages network exceptions, closing
 * the connection automatically when unrecoverable errors occur.
 */
public class CServerConnection implements ConnectionCtx {
	private Socket socket;
	private DataInputStream receiveStream;
	private DataOutputStream sendStream;
	
	private LinkedBlockingQueue<NetworkPacket> dispatchQueue = new LinkedBlockingQueue<>();
	
	private boolean closed = false;
	
	private Thread packetListenerThread;
	private Thread packetDispatcherThread;
	
	public final ArkanoidClient client;
	
	@SuppressWarnings("unchecked")    
	/**
     * Creates a new {@link CServerConnection} representing the client's active connection to a server.
     * Initializes input/output streams and spawns the packet listener and dispatcher threads.
     *
     * @param socket the socket connected to the server
     * @param client the {@link ArkanoidClient} instance providing packet handling and codec utilities
     * @throws IOException if an I/O error occurs while creating the input/output streams
     */
	public CServerConnection(Socket socket, ArkanoidClient client) throws IOException {
		this.socket = socket;
		this.client = client;
		
		this.sendStream = new DataOutputStream(socket.getOutputStream());
		this.receiveStream = new DataInputStream(socket.getInputStream());
		
		// PWP specifications: magic bytes (0x544824) -> Protocol version -> magic footer (0x24E12)
//		sendStream.writeInt(0x544824);
//		sendStream.write(ProtoUtils.PROTOCOL_VERSION);
//		sendStream.writeInt(0x24E12);
//		sendStream.flush();
		
		// packet listening thread
		this.packetListenerThread = new Thread(() -> {
			Thread.currentThread().setName("CServerConnection: Packet Listener Thread");
			while (!closed) {
				try {
					// read the next packet from the server stream
					NetworkPacket packet = client.codec().readPacket(receiveStream);
					// find the handle of this packet (client bound) and then invoke it
					PacketHandler<?, ?> handler = client.getRegistry().getHandle(packet.getClass());
					((PacketHandler<NetworkPacket, CServerConnection>) handler).handle(packet, this);
				} catch (Exception e) {
					handleConnectionException(e);
				}
			}
		});
		
		// packet dispatching thread
		this.packetDispatcherThread = new Thread(() -> {
			Thread.currentThread().setName("CServerConnection: Packet Dispatcher Thread");
			while (true) {
				try {
					NetworkPacket first = dispatchQueue.take(); // this blocks until there's something to pick up
					List<NetworkPacket> batch = new ArrayList<>();
					batch.add(first);
					dispatchQueue.drainTo(batch); // grab any other queued packets immediately
					synchronized (sendStream) {
						for (NetworkPacket p : batch) {
							client.codec().writePacket(sendStream, p);
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
	 * Queues a sequence of packets for sending to the server.
	 * 
	 * @param packets the sequence of packets to send
	 * @throws IllegalArgumentException if the packet is not an instance of
	 *                                  {@link NetworkPacket}
	 */
	public void sendPackets(IPacketPlayIn... packets) {
		for (IPacketPlayIn packetPlayIn : packets) {
			this.sendPacket(packetPlayIn);
		}
	}
	
    /**
     * Queues a packet to be sent to the server.
     *
     * @param packet the outgoing packet to send
     * @throws IllegalArgumentException if the packet is not an instance of {@link NetworkPacket}
     */
	public void sendPacket(IPacketPlayIn packet) {
		if (!(packet instanceof NetworkPacket)) {
			throw new IllegalArgumentException("Cannot dispatch " + packet.getClass().getName() + "! Malformed blueprint.");
		}
		dispatchQueue.add((NetworkPacket) packet);
	}
	
    /**
     * Gracefully closes the connection to the server
     *
     * @param reason the reason for disconnecting
     */
	public void closeWithNotify(String reason) {
		if (closed) return;
		// dispatch a disconnect immediately, this bypasses the queue
		synchronized (sendStream) {
			try {
				client.codec().writePacket(sendStream, new PacketPlayInDisconnect());
				sendStream.flush();
			} catch (IOException e) {}
		}
		closeConnection();
		System.out.println("dc'ed: " + reason);
	}
	
	/**
	 * Immediately closes the connection to the server without sending a
	 * notification. Closes the socket and marks the connection as closed. Stops the
	 * dispatcher thread.
	 */
	public void closeConnection() {
		if (closed) return;
		this.closed = true;
		// only do this if the server socket is still open
		if (!socket.isClosed()) {
			try { 
				receiveStream.close();
				sendStream.close();
				socket.close(); 
			} catch (IOException ignored) {}
		}
		// stop the dispatcher thread
		this.packetDispatcherThread.interrupt();
	}
	
	/**
	 * Handles a graceful disconnect initiated by the server or the client. Simply
	 * closes the connection and cleans up resources.
	 */
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
