package btl.ballgame.server;

import java.util.UUID;

import btl.ballgame.server.data.PlayerData;
import btl.ballgame.server.game.match.ArkanoidMatch;
import btl.ballgame.server.net.PlayerConnection;

public class ArkaPlayer {
	public final PlayerConnection playerConnection;
	private final UUID uuid;
	private final String userName;
	private final PlayerData data;
	
	private ArkanoidMatch currentGame = null;
	
	protected ArkaPlayer(PlayerData data, PlayerConnection conn) {
		this.playerConnection = conn;
		this.data = data;
		this.userName = this.data.getName();
		this.uuid = getUUIDFromName(userName);
	}
	
	public void onPlayerConnectionClose() {
		ArkanoidServer.getServer().getPlayerManager().removePlayer(this.getUniqueId());
		System.out.println(userName + " left");
	}
	
	public ArkanoidMatch getCurrentGame() {
		return currentGame;
	}
	
	public void joinGame(ArkanoidMatch currentGame) {
		this.currentGame = currentGame;
	}
	
	public UUID getUniqueId() {
		return this.uuid;
	}
	
	public String getName() {
		return this.userName;
	}
	
	public void kick(String reason) {
		this.disconnect("Kicked from server: " + reason);
	}
	
	public void disconnect(String reason) {
		this.playerConnection.closeWithNotify(reason);
	}
	
	public PlayerData getData() {
		return data;
	}
	
	public static UUID getUUIDFromName(String userName) {
		return UUID.nameUUIDFromBytes(("ArkaPlayer:" + userName.toLowerCase()).getBytes());
	}
}
