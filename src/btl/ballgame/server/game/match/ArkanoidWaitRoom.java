package btl.ballgame.server.game.match;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import btl.ballgame.protocol.packets.out.PacketPlayOutRoomJoinError;
import btl.ballgame.protocol.packets.out.PacketPlayOutRoomUpdate;
import btl.ballgame.protocol.packets.out.PacketPlayOutRoomUpdate.*;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import btl.ballgame.shared.libs.Constants.TeamColor;

/**
 * Represents a lobby/waiting room for Arkanoid matches.
 */
public class ArkanoidWaitRoom {
	private final String roomId;
	private final String roomName;
	private final ArkaPlayer owner;
	private final boolean isPrivate;
	private final MatchSettings settings;
	private boolean started = false;

	private final Map<TeamColor, List<ArkaPlayer>> teams = new ConcurrentHashMap<>();
	private final Map<ArkaPlayer, Boolean> readyStatus = new ConcurrentHashMap<>();
	
	public ArkanoidWaitRoom(String roomId, String roomName, 
		ArkaPlayer owner, boolean isPrivate, 
		MatchSettings settings
	) {
		this.owner = owner;
		this.roomName = roomName;
		this.isPrivate = isPrivate;
		this.roomId = roomId;
		this.settings = settings;
		
		teams.put(TeamColor.RED, new ArrayList<>());
		teams.put(TeamColor.BLUE, new ArrayList<>());
		
		addPlayer(owner);
	}
	
	public String getRoomId() {
		return roomId;
	}

	public ArkaPlayer getOwner() {
		return owner;
	}

	public MatchSettings getSettings() {
		return settings;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public boolean hasStarted() {
		return started;
	}
	
	public String getRoomName() {
		return roomName;
	}

	/** Add player, auto-balance into a team */
	public boolean addPlayer(ArkaPlayer player) {
		if (started || getAllPlayers().size() >= getMaxPlayersPerTeam() * 2) {
			player.playerConnection.sendPacket(new PacketPlayOutRoomJoinError(
				started ? "This room has started!" : "This room is full!"
			));
			return false;
		}
		// auto-balance, choose the team with fewer players
		TeamColor target = (teams.get(TeamColor.RED).size() <= teams.get(TeamColor.BLUE).size()) 
			? TeamColor.RED
			: TeamColor.BLUE
		;
		// respect max players per team
		if (teams.get(target).size() >= getMaxPlayersPerTeam()) {
			target = (target == TeamColor.RED) ? TeamColor.BLUE : TeamColor.RED;
			if (teams.get(target).size() >= getMaxPlayersPerTeam()) {
				player.playerConnection.sendPacket(new PacketPlayOutRoomJoinError(
					"Cannot join this room, both teams are full!"
				));
				return false; // both teams full
			}
		}
		teams.get(target).add(player);
		readyStatus.put(player, false);
		player.joinWaitingRoom(this);
		this.broadcastRoomUpdate();
		return true;
	}

	/** Remove player from room */
	public boolean removePlayer(ArkaPlayer player) {
		if (teams.get(TeamColor.RED).remove(player) || teams.get(TeamColor.BLUE).remove(player)) {
			player.leaveWaitingRoom();
			readyStatus.remove(player);
			return true;
		}
		return false;
	}

	/** Swap team if space is available */
	public boolean swapTeam(ArkaPlayer player, TeamColor target) {
		TeamColor current = getTeamOf(player);
		if (current == target) return false;
		if (teams.get(target).size() >= getMaxPlayersPerTeam()) return false;
		
		teams.get(current).remove(player);
		teams.get(target).add(player);
		broadcastRoomUpdate();
		return true;
	}

	/** Mark player as ready/unready */
	public void setReady(ArkaPlayer player, boolean ready) {
		if (readyStatus.containsKey(player)) {
			readyStatus.put(player, ready);
			broadcastRoomUpdate();
		}
	}

	/** Get all players in the room */
	public List<ArkaPlayer> getAllPlayers() {
		List<ArkaPlayer> all = new ArrayList<>();
		teams.values().forEach(all::addAll);
		return all;
	}

	/** Get a player’s team */
	public TeamColor getTeamOf(ArkaPlayer player) {
		for (Map.Entry<TeamColor, List<ArkaPlayer>> entry : teams.entrySet()) {
			if (entry.getValue().contains(player)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/** Check if all players are ready */
	public boolean allReady() {
		return !readyStatus.isEmpty() && readyStatus.values().stream().allMatch(Boolean::booleanValue);
	}

	/** Check if conditions are met to start the match */
	public boolean canStart() {
		if (started || !allReady()) {
			return false;
		}
		
		// check 1v1 min. requirements
		if (settings.getGamemode() == ArkanoidMode.ONE_VERSUS_ONE) {
			// one per team must be ready
			return teams.get(TeamColor.RED).size() == 1 
				&& teams.get(TeamColor.BLUE).size() == 1 
				&& allReady()
			;
		} else { // TWO_VERSUS_TWO
			// at least 1 player per team
			return teams.values().stream().filter(l -> !l.isEmpty()).count() == 2 
				&& allReady()
				&& getAllPlayers().size() >= 2
			;
		}
	}
	
	public int getMaxPlayersPerTeam() {
	    return settings.getGamemode() == ArkanoidMode.ONE_VERSUS_ONE ? 1 : 2;
	}
	
	/** Start the match and hand over to ArkanoidMatch */
	public ArkanoidMatch startMatch() {
		if (!canStart()) {
			return null;
		}
		this.started = true;
		ArkanoidMatch match = new ArkanoidMatch(settings);
		match.assignTeam(TeamColor.RED, new ArrayList<>(teams.get(TeamColor.RED)));
		match.assignTeam(TeamColor.BLUE, new ArrayList<>(teams.get(TeamColor.BLUE)));
		match.start();
		return match;
	}

	/** Broadcast update packet to all players in this room */
	private void broadcastRoomUpdate() {
	    // build the packet from current room state
	    var teamsData = new RoomTeamEntry[TeamColor.values().length];
	    for (TeamColor color : TeamColor.values()) {
	        PacketPlayOutRoomUpdate.RoomTeamEntry teamEntry = new PacketPlayOutRoomUpdate.RoomTeamEntry();
	        teamEntry.teamColor = (byte) color.ordinal();
	        // collect all players in this team
	        List<ArkaPlayer> teamPlayers = teams.get(color);
	        teamEntry.players = teamPlayers.stream().map(p -> {
	        	// build each individual waiting players
	            var playerEntry = new RoomPlayerEntry();
	            playerEntry.uuid = p.getUniqueId();
	            playerEntry.name = p.getName();
	            playerEntry.ready = readyStatus.get(p);
	            return playerEntry;
	        }).toArray(RoomPlayerEntry[]::new);
	        teamsData[color.ordinal()] = teamEntry;
	    }
	    // create the packet
	    PacketPlayOutRoomUpdate packet = new PacketPlayOutRoomUpdate(teamsData, roomId);
	    for (ArkaPlayer player : getAllPlayers()) {
	    	player.playerConnection.sendPacket(packet);
	    }
	}
}
