package btl.ballgame.protocol.packets.out;

import java.util.ArrayList;
import java.util.List;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;

public class PacketPlayOutListPublicRooms extends NetworkPacket implements IPacketPlayOut {
	private List<RoomInfo> rooms;
	
	public PacketPlayOutListPublicRooms() {}
	
	public PacketPlayOutListPublicRooms(List<RoomInfo> rooms) {
		this.rooms = rooms;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		// write the number of rooms first
		buffer.writeInt16((short) rooms.size());
		for (RoomInfo room : rooms) {
			buffer.writeU8String(room.roomId);
			buffer.writeU8String(room.roomName);
			buffer.writeInt8((byte) room.gameMode.ordinal());
			buffer.writeInt8((byte) room.playerCount);
			buffer.writeInt8((byte) room.maxPlayer);
		}
	}
	
	@Override
	public void read(PacketByteBuf buffer) {
		int size = buffer.readInt16();
		this.rooms = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			RoomInfo info = new RoomInfo();
			info.roomId = buffer.readU8String();
			info.roomName = buffer.readU8String();
			info.gameMode = ArkanoidMode.of(buffer.readInt8());
			info.playerCount = buffer.readInt8();
			info.maxPlayer = buffer.readInt8();
			rooms.add(info);
		}
	}

	public List<RoomInfo> getRooms() {
		return rooms;
	}

	public static class RoomInfo {
		public String roomId;
		public String roomName;
		public ArkanoidMode gameMode;
		public int playerCount;
		public int maxPlayer;
	}
}
