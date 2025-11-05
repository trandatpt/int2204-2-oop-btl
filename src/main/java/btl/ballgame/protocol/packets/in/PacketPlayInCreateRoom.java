package btl.ballgame.protocol.packets.in;

public class PacketPlayInCreateRoom implements IPacketPlayIn {
    public final String roomName;

    public PacketPlayInCreateRoom(String roomName) {
        this.roomName = roomName;
    }
}
