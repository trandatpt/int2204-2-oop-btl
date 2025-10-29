package btl.ballgame.server.net;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import btl.ballgame.protocol.packets.out.IPacketPlayOut;
import btl.ballgame.protocol.packets.out.PacketPlayOutPing;

public class NetworkManager {
	private static final PacketPlayOutPing PING_PACKET = new PacketPlayOutPing();
	private final Set<PlayerConnection> connections = Collections.synchronizedSet(new LinkedHashSet<>());	
	
	public void notifyAllDispatcher() {
		synchronized (connections) {
			for (PlayerConnection conn : connections) {
				conn.notifyDispatcher();
			}
		}
	}
	
	public void disconnectAll(String reason) {
		synchronized (connections) {
			for (PlayerConnection conn : connections) {
				conn.closeWithNotify(reason);
			}
		}
	}
	
	public void pingAllClients() {
		broadcast(PING_PACKET);
	}
	
	public void track(PlayerConnection conn) {
		connections.add(conn);
	}
	
	public void untrack(PlayerConnection conn) {
		connections.remove(conn);
	}
	
	public void broadcast(IPacketPlayOut packet) {
		synchronized (connections) {
			for (PlayerConnection conn : connections) {
				conn.sendPacket(packet);
			}
		}
	}
	
	public Collection<PlayerConnection> getConnections() {
		return Collections.unmodifiableSet(connections);
	}
}
