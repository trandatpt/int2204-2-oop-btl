package btl.ballgame.server;

import java.util.UUID;

import btl.ballgame.protocol.packets.out.PacketPlayOutTitle;
import btl.ballgame.server.data.PlayerData;
import btl.ballgame.server.game.match.ArkanoidMatch;
import btl.ballgame.server.game.match.ArkanoidWaitRoom;
import btl.ballgame.server.net.PlayerConnection;
import btl.ballgame.shared.libs.Constants.EnumTitle;

public class ArkaPlayer {
	public final PlayerConnection playerConnection;
	private final UUID uuid;
	private final String userName;
	private final PlayerData data;
	private boolean online = true;
	
	private ArkanoidWaitRoom currentWaitingRoom = null;
	private ArkanoidMatch currentGame = null;
	
	protected ArkaPlayer(PlayerData data, PlayerConnection conn) {
		this.playerConnection = conn;
		this.data = data;
		this.userName = this.data.getName();
		this.uuid = getUUIDFromName(userName);
	}
	
	public ArkanoidMatch getCurrentGame() {
		return currentGame;
	}
	
	public ArkanoidWaitRoom getCurrentWaitingRoom() {
		return currentWaitingRoom;
	}
	
	public void joinWaitingRoom(ArkanoidWaitRoom room) {
		if (currentWaitingRoom != null) {
			leaveWaitingRoom(); // leave previous room if any
		}
		this.currentWaitingRoom = room;
		this.currentGame = null; // cannot be in game while in waiting room
	}
	
	// Leave the current waiting room
	public void leaveWaitingRoom() {
	    if (currentWaitingRoom != null) {
	        currentWaitingRoom.removePlayer(this);
	        currentWaitingRoom = null;
	    }
	}
	
	public void joinGame(ArkanoidMatch currentGame) {
		this.leaveWaitingRoom();
		this.currentGame = currentGame;
	}
	
	public void leaveGame() {
		this.currentGame = null;
	}
	
	public boolean isBusy() {
	    return getCurrentWaitingRoom() != null || getCurrentGame() != null;
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
	
	public void setOnline(boolean online) {
		this.online = online;
	}
	
	public boolean isOnline() {
		return online;
	}
	
	/**
	 * Displays a simple title or subtitle to this player with default timings.
	 * (fadeIn=10, stay=30 (1s), fadeOut=10)
	 */
	public void sendFormattedTitle(EnumTitle type, 
		String message, int color, 
		int size, int fadeIn, int stay,
		int fadeOut
	) {
		if (message == null || message.isEmpty()) {
			return;
		}
		boolean bold = false, italic = false, underline = false;
		boolean parsing = true;
		while (parsing) {
			parsing = false;
			if (message.startsWith("<b>")) {
				bold = true;
				message = message.substring(3); // trim the parsed data
				parsing = true;
			}
			if (message.startsWith("<i>")) {
				italic = true;
				message = message.substring(3);
				parsing = true;
			}
			if (message.startsWith("<u>")) {
				underline = true;
				message = message.substring(3);
				parsing = true;
			}
		}
		sendTitle(type, message, color, size, bold, italic, underline, fadeIn, stay, fadeOut);
	}
	
	/**
	 * Displays a simple title or subtitle to this player with default timings.
	 * (fadeIn=10, stay=30 (1s), fadeOut=10)
	 */
	public void sendTitle(EnumTitle type, String message, int color, int size) {
		sendTitle(type, message, color, size, 10, 30, 10);
	}

	/**
	 * Displays a title or subtitle with custom fade timings.
	 */
	public void sendTitle(EnumTitle type, String message, int color, int size, int fadeInTicks, int stayTicks, int fadeOutTicks) {
		sendTitle(type, message, color, size, false, false, false, fadeInTicks, stayTicks, fadeOutTicks);
	}
	
	/**
	 * Displays a styled title or subtitle (full control).
	 */
	public void sendTitle(EnumTitle type, String message, 
		int color, int size, 
		boolean bold, boolean italic, boolean underline, 
		int fadeInTicks, int stayTicks, int fadeOutTicks
	) {
		playerConnection.sendPacket(new PacketPlayOutTitle(
			type, message, color, size, bold, italic, underline,
			fadeInTicks, stayTicks, fadeOutTicks
		));
	}
}
