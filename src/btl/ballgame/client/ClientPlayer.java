package btl.ballgame.client;

import java.util.UUID;

public class ClientPlayer {
	private final String userName;
	private final UUID userUUID;
	
	public ClientPlayer(String username, UUID uuid) {
		this.userName = username;
		this.userUUID = uuid;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public UUID getUniqueId() {
		return userUUID;
	}
}
