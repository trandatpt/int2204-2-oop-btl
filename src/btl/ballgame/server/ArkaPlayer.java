package btl.ballgame.server;

import java.util.UUID;

import btl.ballgame.server.net.PlayerConnection;

public class ArkaPlayer {
	public final PlayerConnection playerConnection;
	private final UUID uuid;
	private final String userName;
	
	protected ArkaPlayer(String userName, PlayerConnection conn) {
		this.playerConnection = conn;
		this.userName = userName;
		this.uuid = UUID.nameUUIDFromBytes(("ArkaPlayer:" + this.userName.toLowerCase()).getBytes());
	}
	
	public void onPlayerConnectionClose() {
		ArkanoidServer.getServer().getPlayerManager().removePlayer(this.getUniqueId());
		System.out.println(userName + " left");
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
}
