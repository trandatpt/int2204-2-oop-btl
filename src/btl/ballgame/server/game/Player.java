package btl.ballgame.server.game;

import java.util.UUID;

import btl.ballgame.server.net.PlayerConnection;

public class Player {
	public final PlayerConnection playerConnection;
	private final UUID uuid;
	private final String userName;
	
	public Player(String userName, PlayerConnection conn) {
		this.playerConnection = conn;
		this.userName = userName;
		this.uuid = UUID.nameUUIDFromBytes(("ArkaPlayer:" + this.userName.toLowerCase()).getBytes());
		this.playerConnection.attachTo(this);
	}
	
	public UUID getUniqueId() {
		return this.uuid;
	}
	
	public void kick(String reason) {
		this.disconnect("Kicked from server: " + reason);
	}
	
	public void disconnect(String reason) {
		this.playerConnection.close(reason);
	}
}
