package btl.ballgame.protocol.packets.out;

import java.util.UUID;
import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Constants.EffectType;
import btl.ballgame.shared.libs.Constants.MatchPhase;
import btl.ballgame.shared.libs.Constants.UPlayerEffect;

public class PacketPlayOutMatchMetadata extends NetworkPacket implements IPacketPlayOut {
	private byte phase;
	private short roundIndex;
	private TeamEntry[] teams;
	
	public PacketPlayOutMatchMetadata() {};

	public PacketPlayOutMatchMetadata(byte phase, short roundIndex, TeamEntry[] teams) {
		this.phase = phase;
		this.roundIndex = roundIndex;
		this.teams = teams;
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
		// sync the match's state
		buf.writeInt8(phase);
		buf.writeInt16(roundIndex);
		buf.writeInt8((byte) teams.length);
		// sync teams and players state
		for (TeamEntry team : teams) {
			buf.writeInt8(team.teamColor);
			buf.writeInt8(team.ftScore);
			buf.writeVarUInt(team.arkScore);
			buf.writeInt8(team.livesRemaining);
			buf.writeInt8((byte) team.players.length); // len
			for (PlayerEntry p : team.players) {
				// evil bit hack
				buf.writeInt64(p.uuid.getMostSignificantBits());
				buf.writeInt64(p.uuid.getLeastSignificantBits());
				buf.writeInt8(p.health);
				buf.writeInt8(p.rifleState);
				buf.writeInt8(p.rifleAmmo);
				// write effect list
				buf.writeInt8((byte) p.effects.length);
				for (UPlayerEffect effect : p.effects) {
					// yes
					buf.writeInt8((byte) effect.effect().ordinal());
					buf.writeInt64(effect.endTime());
				}
			}
		}
		// about 99 - 110 bytes per packet
	}

	@Override
	public void read(PacketByteBuf buf) {
		// i have no words
		this.phase = buf.readInt8();
		this.roundIndex = buf.readInt16();
		this.teams = new TeamEntry[(int) buf.readInt8()];
		for (int i = 0; i < teams.length; i++) {
			TeamEntry team = new TeamEntry();
			team.teamColor = buf.readInt8();
			team.ftScore = buf.readInt8();
			team.arkScore = buf.readVarUInt();
			team.livesRemaining = buf.readInt8();
			team.players = new PlayerEntry[buf.readInt8()];
			for (int j = 0; j < team.players.length; j++) {
				PlayerEntry pe = new PlayerEntry();
				long msb = buf.readInt64(); // evil
				long lsb = buf.readInt64();
				pe.uuid = new UUID(msb, lsb);
				pe.health = buf.readInt8();
				pe.rifleState = buf.readInt8();
				pe.rifleAmmo = buf.readInt8();
				// read the effect list
				pe.effects = new UPlayerEffect[buf.readInt8()];
				for (int k = 0; k < pe.effects.length; k++) {
					// rebuild piece by piece
					pe.effects[k] = new UPlayerEffect(
						EffectType.of(buf.readInt8()), 
						buf.readInt64()
					);
				}
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
		public PlayerEntry players[];
	}
	
	public static class PlayerEntry {
		public UUID uuid;
		public byte health;
		public byte rifleAmmo;
		public byte rifleState;
		public UPlayerEffect effects[];
	}
}
