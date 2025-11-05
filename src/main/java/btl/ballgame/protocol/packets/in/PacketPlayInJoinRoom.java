package btl.ballgame.protocol.packets.in;

public class PacketPlayInJoinRoom implements IPacketPlayIn {
    public final int roomId;

    public PacketPlayInJoinRoom(int roomId) {
        this.roomId = roomId;
    }
}
