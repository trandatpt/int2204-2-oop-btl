package btl.ballgame.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import btl.ballgame.server.net.PlayerConnection;

public class PlayerManager {
	private Map<UUID, ArkaPlayer> trackingPlayers = Collections.synchronizedMap(new LinkedHashMap<>());
	
	public ArkaPlayer addPlayer(PlayerConnection connection, String userName) {
		ArkaPlayer player = new ArkaPlayer(userName, connection);
		if (getPlayer(player.getUniqueId()) != null) {
			throw new IllegalStateException("You are already logged in!");
		}
		
		// register the player and attach connection
		trackingPlayers.put(player.getUniqueId(), player);
		connection.attachTo(player);
		return player;
	}
	
	public void removePlayer(UUID uniqueId) {
		ArkaPlayer player = getPlayer(uniqueId);
		if (player == null) return;
		trackingPlayers.remove(uniqueId);
	}
	
	public Collection<ArkaPlayer> getOnlinePlayers() {
		return trackingPlayers.values();
	}
	
	public ArkaPlayer getPlayer(UUID uniqueId) {
		return trackingPlayers.get(uniqueId);
	}
	
	public ArkaPlayer getPlayer(String name) {
		for (ArkaPlayer player : getOnlinePlayers()) {
			if (!player.getName().equalsIgnoreCase(name)) continue;
			return player;
		}
		return null;
	}
}
