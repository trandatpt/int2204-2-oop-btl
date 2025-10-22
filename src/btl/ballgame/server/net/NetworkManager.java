package btl.ballgame.server.net;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import btl.ballgame.protocol.packets.out.IPacketPlayOut;

public class NetworkManager {
	private final Set<PlayerConnection> connections = Collections.synchronizedSet(new LinkedHashSet<>());	
	
	public void notifyAllDispatcher() {
		for (PlayerConnection conn : connections) {
			conn.notifyDispatcher();
		}
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
