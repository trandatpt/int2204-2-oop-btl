package btl.ballgame.protocol.packets.out;

import java.util.List;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.IWorld;
import btl.ballgame.shared.libs.Constants.TeamColor;

public class PacketPlayOutTeamInfo extends NetworkPacket implements IPacketPlayOut {
	public PacketPlayOutTeamInfo() {}
	
	public PacketPlayOutTeamInfo(
		TeamColor teamColor, int teamLives,
		int teamScore // TODO
	) {
		
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
	}

	@Override
	public void read(PacketByteBuf buffer) {
	}
 }
