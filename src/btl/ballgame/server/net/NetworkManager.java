package btl.ballgame.server.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import btl.ballgame.protocol.packets.out.IPacketPlayOut;

public class NetworkManager {
	private final List<PlayerConnection> connections = Collections.synchronizedList(new ArrayList<>());
	
	public void track(PlayerConnection conn) {
		connections.add(conn);
	}
	
	public void untrack(PlayerConnection conn) {
		connections.remove(conn);
	}
	
	public void broadcast(IPacketPlayOut packet) {
		for (PlayerConnection conn : connections) {
			conn.sendPacket(packet);
		}
	}
	
	public List<PlayerConnection> getConnections() {
		return Collections.unmodifiableList(connections);
	}
}
