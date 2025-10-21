package btl.ballgame.protocol.packets.out;

import java.util.UUID;
import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;

public class PacketPlayOutMatchMetadata extends NetworkPacket implements IPacketPlayOut {
	private byte mode;
	private byte phase;
	private int roundIndex;
	private TeamEntry[] teams;
	
	public PacketPlayOutMatchMetadata() {};

	public PacketPlayOutMatchMetadata(byte mode, byte phase, int roundIndex, TeamEntry[] teams) {
		this.mode = mode;
		this.phase = phase;
		this.roundIndex = roundIndex;
		this.teams = teams;
	}
	
	public ArkanoidMode getMode() {
		return ArkanoidMode.values()[mode];
	}
	
	public byte getPhase() {
		return phase;
	}
	
	public int getRoundIndex() {
		return roundIndex;
	}

	public TeamEntry[] getTeams() {
		return teams;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt8(mode);
		buffer.writeInt8(phase);
		buffer.writeInt32(roundIndex);
		buffer.writeInt8((byte) teams.length);
		
		for (TeamEntry team : teams) {
			buffer.writeInt8(team.teamColor);
			buffer.writeInt8(team.ftScore);
			buffer.writeInt32(team.arkScore);
			buffer.writeInt8(team.livesRemaining);
			buffer.writeInt8((byte) team.players.length);
			for (PlayerEntry p : team.players) {
				buffer.writeU8String(p.uuid.toString());
				buffer.writeInt16(p.health);
			}
		}
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.mode = buffer.readInt8();
		this.phase = buffer.readInt8();
		this.roundIndex = buffer.readInt32();

		this.teams = new TeamEntry[buffer.readInt8() & 0xFF];

		for (int i = 0; i < teams.length; i++) {
			TeamEntry team = new TeamEntry();
			team.teamColor = buffer.readInt8();
			team.ftScore = buffer.readInt8();
			team.arkScore = buffer.readInt32();
			team.livesRemaining = buffer.readInt8();
			
			int playerCount = buffer.readInt8() & 0xFF;
			team.players = new PlayerEntry[playerCount];
			for (int p = 0; p < playerCount; p++) {
				PlayerEntry pe = new PlayerEntry();
				pe.uuid = UUID.fromString(buffer.readU8String());
				pe.health = buffer.readInt16();
				team.players[p] = pe;
			}
			this.teams[i] = team;
		}
	}

	public static class TeamEntry {
		public byte teamColor;
		public byte ftScore;
		public int arkScore;
		public byte livesRemaining;
		public PlayerEntry[] players;
	}

	public static class PlayerEntry {
		public UUID uuid;
		public short health;
	}
}
