package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Constants.TeamColor;

public class PacketPlayInRoomSwapTeam extends NetworkPacket implements IPacketPlayIn {
	private TeamColor teamColor;
	public PacketPlayInRoomSwapTeam() {}
	
	public PacketPlayInRoomSwapTeam(TeamColor teamColor) {
		this.teamColor = teamColor;
	}
	
	public TeamColor getTeamColor() {
		return teamColor;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt8((byte) teamColor.ordinal());
	}
	
	@Override
	public void read(PacketByteBuf buffer) {
		this.teamColor = TeamColor.of(buffer.readInt8());
	}
}
