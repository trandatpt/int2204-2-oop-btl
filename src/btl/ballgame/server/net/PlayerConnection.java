package btl.ballgame.server.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

import btl.ballgame.protocol.packets.ConnectionCtx;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.server.ArkanoidServer;

public class PlayerConnection implements ConnectionCtx {
	public static final int CLIENT_READ_INTERVAL = 10;
	
	private ObjectInputStream receiveStream;
	private ObjectOutputStream sendStream;
	
	private Queue<NetworkPacket> sendQueue = new ArrayDeque<>();
	
	private boolean closed = false;
	
	public PlayerConnection(ArkanoidServer server, InputStream iStream, OutputStream oStream) throws IOException {
		this.sendStream = new ObjectOutputStream(oStream);
		this.receiveStream = new ObjectInputStream(iStream);
		
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(CLIENT_READ_INTERVAL);
					if (closed) {
						break;
					}
					NetworkPacket packet = NetworkPacket.readNextPacket(receiveStream);
					server.getRegistry().getHandle(packet.getClass())
						.handle(packet, this)
					;
				} catch (InterruptedException e) {}
			}
		}).start();
	}
	
	public synchronized void sendPacket(NetworkPacket packet) {
		packet.write(this.sendStream);
	}
}
