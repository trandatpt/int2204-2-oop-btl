package btl.ballgame.protocol.packets.out;

import java.util.UUID;
import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import btl.ballgame.shared.libs.Constants.MatchPhase;

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
	
	public MatchPhase getPhase() {
		return MatchPhase.values()[phase];
	}
	
	public int getRoundIndex() {
		return roundIndex;
	}

	public TeamEntry[] getTeams() {
		return teams;
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt8(mode);
		buf.writeInt8(phase);
		buf.writeInt32(roundIndex);
		buf.writeInt8((byte) teams.length);
		for (TeamEntry team : teams) {
			buf.writeInt8(team.teamColor);
			buf.writeInt8(team.ftScore);
			buf.writeInt32(team.arkScore);
			buf.writeInt8(team.livesRemaining);
			buf.writeInt8((byte) team.players.length);
			for (PlayerEntry p : team.players) {
				buf.writeInt64(p.uuid.getMostSignificantBits());
				buf.writeInt64(p.uuid.getLeastSignificantBits());
				buf.writeU8String(p.name);
				buf.writeInt8(p.health);
				buf.writeInt8(p.rifleState);
				buf.writeInt8(p.bulletsLeft);
			}
		}
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.mode = buf.readInt8();
		this.phase = buf.readInt8();
		this.roundIndex = buf.readInt32();
		this.teams = new TeamEntry[(int) buf.readInt8()];
		for (int i = 0; i < teams.length; i++) {
			TeamEntry team = new TeamEntry();
			team.teamColor = buf.readInt8();
			team.ftScore = buf.readInt8();
			team.arkScore = buf.readInt32();
			team.livesRemaining = buf.readInt8();
			team.players = new PlayerEntry[buf.readInt8()];
			for (int j = 0; j < team.players.length; j++) {
				PlayerEntry pe = new PlayerEntry();
				long msb = buf.readInt64(); // evil
				long lsb = buf.readInt64();
				pe.uuid = new UUID(msb, lsb);
				pe.name = buf.readU8String();
				pe.health = buf.readInt8();
				pe.rifleState = buf.readInt8();
				pe.bulletsLeft = buf.readInt8();
				team.players[j] = pe;
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
		public String name;
		public byte health;
		public byte bulletsLeft;
		public byte rifleState;
	}
}
