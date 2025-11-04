package btl.ballgame.protocol.packets.out;

import java.util.List;

public class PacketPlayOutRoomList {

    public static class RoomInfoNet {
        public final int id;
        public final String name;
        public final int players;
        public final int maxPlayers;
        public final String status;

        public RoomInfoNet(int id, String name, int players, int maxPlayers, String status) {
            this.id = id;
            this.name = name;
            this.players = players;
            this.maxPlayers = maxPlayers;
            this.status = status;
        }
    }

    public final List<RoomInfoNet> rooms;

    public PacketPlayOutRoomList(List<RoomInfoNet> rooms) {
        this.rooms = rooms;
    }
}
