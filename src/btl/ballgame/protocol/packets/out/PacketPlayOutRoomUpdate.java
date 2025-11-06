package btl.ballgame.protocol.packets.out;

import java.util.UUID;
import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutRoomUpdate extends NetworkPacket implements IPacketPlayOut {
	private RoomTeamEntry[] teams;
	private String roomId;

	public PacketPlayOutRoomUpdate() {}

	public PacketPlayOutRoomUpdate(RoomTeamEntry[] teams, String roomId) {
		this.teams = teams;
		this.roomId = roomId;
	}

	public RoomTeamEntry[] getTeams() {
		return teams;
	}
	
	public String getRoomId() {
		return roomId;
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeU8String(roomId);
		buf.writeInt8((byte) teams.length);
		for (RoomTeamEntry team : teams) {
			buf.writeInt8(team.teamColor);
			buf.writeInt8((byte) team.players.length);
			for (RoomPlayerEntry player : team.players) {
				buf.writeInt64(player.uuid.getMostSignificantBits()); // MSB
				buf.writeInt64(player.uuid.getLeastSignificantBits()); // LSB
				buf.writeU8String(player.name);
				buf.writeBool(player.ready);
			}
		}
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.roomId = buf.readU8String();
		this.teams = new RoomTeamEntry[buf.readInt8()];
		for (int i = 0; i < teams.length; i++) {
			RoomTeamEntry team = new RoomTeamEntry();
			team.teamColor = buf.readInt8();
			team.players = new RoomPlayerEntry[buf.readInt8()];
			for (int j = 0; j < team.players.length; j++) {
				RoomPlayerEntry player = new RoomPlayerEntry();
				long msb = buf.readInt64();
				long lsb = buf.readInt64();
				player.uuid = new UUID(msb, lsb);
				player.name = buf.readU8String();
				player.ready = buf.readBool();
				team.players[j] = player;
			}
			teams[i] = team;
		}
	}

	/** represents a single team in the room. */
	public static class RoomTeamEntry {
		public byte teamColor;
		public RoomPlayerEntry[] players;
	}

	/** represents a single player in the room. */
	public static class RoomPlayerEntry {
		public UUID uuid;
		public String name;
		public boolean ready;
	}
}
