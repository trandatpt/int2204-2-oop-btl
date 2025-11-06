package btl.ballgame.server.game.match;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import btl.ballgame.protocol.packets.out.PacketPlayOutListPublicRooms;
import btl.ballgame.protocol.packets.out.PacketPlayOutListPublicRooms.RoomInfo;
import btl.ballgame.protocol.packets.out.PacketPlayOutRoomDisband;
import btl.ballgame.protocol.packets.out.PacketPlayOutRoomJoinError;
import btl.ballgame.server.ArkaPlayer;

public class MatchManager {
    private final Map<String, ArkanoidWaitRoom> waitingRooms = new ConcurrentHashMap<>();
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
    
    /** Generates a unique 8-character secret code */
	private String generateUniqueCode() {
		String code;
		do {
			var srnd = new SecureRandom();
			var sb = new StringBuilder(8);
			for (int i = 0; i < 8; i++) {
				int index = srnd.nextInt(CHARSET.length());
				sb.append(CHARSET.charAt(index));
			}
			code = sb.toString();
		} while (waitingRooms.containsKey(code)); // ensure it is unique
		return code;
	}
    
    /** Create a new room */
    public ArkanoidWaitRoom createRoom(ArkaPlayer owner, String name, boolean isPrivate, MatchSettings settings) {
    	ArkanoidWaitRoom room = new ArkanoidWaitRoom(generateUniqueCode(), name, owner, isPrivate, settings);
        waitingRooms.put(room.getRoomId(), room);
    	System.out.println("[MATCHMAN] Created waiting room for " + owner.getName() + " with name \"" + name + "\" (" + (isPrivate ? "private" : "public") + ")");
        return room;
    }

    /** Player joins an existing room */
    public boolean joinRoom(ArkaPlayer player, String roomId) {
        ArkanoidWaitRoom room = waitingRooms.get(roomId);
        if (room == null) {
    		player.playerConnection.sendPacket(new PacketPlayOutRoomJoinError(
    			"The room you tried to join doesn't exist!"
    		));
        	return false;
        }
        if (room.getAllPlayers().contains(player)) {
    		player.disconnect("Invalid packet order!");
        	return false;
        }
        return room.addPlayer(player);
    }
    
    /** Remove room when match starts or owner cancels */
    public void removeRoom(String roomId) {
        var room = waitingRooms.remove(roomId);
        if (room != null && !room.hasStarted()) {
        	room.getAllPlayers().forEach(p -> {
        		p.leaveWaitingRoom();
        		p.playerConnection.sendPacket(new PacketPlayOutRoomDisband());
        	});
        }
    }
    
    /** Auto-start/cleanup rooms that are ready/dead */
    public void tick() {
    	try {
	        for (ArkanoidWaitRoom room : new ArrayList<>(waitingRooms.values())) {
	        	if (!room.getOwner().isOnline()) {
	        		removeRoom(room.getRoomId());
	        		return;
	        	}
	            if (room.canStart()) {
	                room.startMatch();
	                removeRoom(room.getRoomId());
	            }
	        }
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /** Get list of public rooms for listing */
    public List<ArkanoidWaitRoom> getPublicRooms() {
        List<ArkanoidWaitRoom> list = new ArrayList<>();
        for (ArkanoidWaitRoom room : waitingRooms.values()) {
            if (!room.isPrivate()) list.add(room);
        }
        return list;
    }
    
	public PacketPlayOutListPublicRooms buildPublicRoomPacket() {
		List<RoomInfo> infoList = new ArrayList<>();
		for (ArkanoidWaitRoom room : getPublicRooms()) {
			RoomInfo info = new RoomInfo();
			info.roomId = room.getRoomId();
			info.roomName = room.getRoomName();
			info.gameMode = room.getSettings().getGamemode();
			info.playerCount = room.getAllPlayers().size();
			info.maxPlayer = room.getMaxPlayersPerTeam() * 2;
			infoList.add(info);
		}
		return new PacketPlayOutListPublicRooms(infoList);
	}
}
