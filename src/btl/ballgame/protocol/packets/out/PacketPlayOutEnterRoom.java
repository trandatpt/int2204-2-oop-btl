package btl.ballgame.protocol.packets.out;

public class PacketPlayOutEnterRoom {
    public final int roomId;
    public final String roomName;
    public final int players;
    public final int maxPlayers;

    public PacketPlayOutEnterRoom(int roomId, String roomName, int players, int maxPlayers) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.players = players;
        this.maxPlayers = maxPlayers;
    }
}
