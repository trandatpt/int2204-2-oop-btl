package btl.ballgame.server.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import btl.ballgame.protocol.ConnectionCtx;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.IPacketPlayOut;
import btl.ballgame.protocol.packets.out.PacketPlayOutSocketClose;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.game.Player;
import btl.ballgame.shared.UnknownPacketException;

public class PlayerConnection implements ConnectionCtx {
	public static final int CLIENT_READ_INTERVAL = 10;
	public static final int PACKET_DISPATCH_INTERVAL = 5;
	
	private Socket clientSocket;
	private DataInputStream receiveStream;
	private DataOutputStream sendStream;
	
	private Queue<NetworkPacket> dispatchQueue = new ConcurrentLinkedQueue<>();
	
	private boolean closed = false;
	
	private ArkanoidServer server;
	private Player owner;
	
	@SuppressWarnings("unchecked")
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
	
	private synchronized void flush() throws IOException {
		if (dispatchQueue.isEmpty()) return;
		NetworkPacket packet;
		while ((packet = dispatchQueue.poll()) != null) {
			server.codec().writePacket(sendStream, packet);
		}
		sendStream.flush();
	}
	
	public void sendPacket(IPacketPlayOut packet) {
		if (!(packet instanceof NetworkPacket)) {
			throw new IllegalArgumentException("Cannot dispatch " + packet.getClass().getName() + "! Malformed blueprint.");
		}
		this.sendPacket((NetworkPacket) packet, false);
	}
	
	public void sendPacket(NetworkPacket packet, boolean instant) {
		if (closed) return;
		dispatchQueue.add(packet);
		if (instant) {
			try {
				flush();
			} catch (Exception e) {
				handleConnectionException(e);
			}
		}	
	}
	
	public void close(String reason) {
		sendPacket(new PacketPlayOutSocketClose(reason), true);
		this.closed = true;
		try {
			if (!clientSocket.isClosed()) {
				this.clientSocket.close();
			}
		} catch (IOException e) {}
	}
	
	public void attachTo(Player p) {
		this.owner = p;
	}
	
	public Player getPlayer() {
		return this.owner;
	}
	
	private void handleConnectionException(Throwable e) {
		if (e instanceof UnknownPacketException) {
			close(e.getMessage());
			return;
		}
		if (e instanceof IOException) {
			close("Broken pipe");
			return;
		}
		// generic
		e.printStackTrace();
	}
}
