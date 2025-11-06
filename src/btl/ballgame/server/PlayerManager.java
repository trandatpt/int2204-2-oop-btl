package btl.ballgame.server;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import btl.ballgame.server.data.PlayerData;
import btl.ballgame.server.net.PlayerConnection;

public class PlayerManager {
	private Map<UUID, ArkaPlayer> trackingPlayers = Collections.synchronizedMap(new LinkedHashMap<>());
	
	public ArkaPlayer addPlayer(PlayerConnection connection, PlayerData data) {
		ArkaPlayer player = new ArkaPlayer(data, connection);
		if (getPlayer(player.getUniqueId()) != null) {
			throw new IllegalStateException("You are already logged in!");
		}
		
		// register the player and attach connection
		trackingPlayers.put(player.getUniqueId(), player);
		connection.attachTo(player);
		player.setOnline(true);
		System.out.println("[PLAYERMAN] Player " + player.getName() + " (" + player.getUniqueId() + ") joined the server.");
		return player;
	}
	
	public void removePlayer(UUID uniqueId) {
		ArkaPlayer player = getPlayer(uniqueId);
		if (player == null) return;
		player.setOnline(false);
		trackingPlayers.remove(uniqueId);
		if (player.getCurrentGame() != null) {
			player.getCurrentGame().onPlayerLeft(player);
		}
		if (player.getCurrentWaitingRoom() != null) {
			player.getCurrentWaitingRoom().removePlayer(player);
		}
		System.out.println("[PLAYERMAN] Player " + player.getName() + " (" + player.getUniqueId() + ") left the server.");
	}
	
	public Collection<ArkaPlayer> getOnlinePlayers() {
		return trackingPlayers.values();
	}
	
	public int total() {
		return this.trackingPlayers.size();
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
