package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInRoomSwapTeam;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.net.PlayerConnection;

public class ClientRoomSwapTeamHandle implements PacketHandler<PacketPlayInRoomSwapTeam, PlayerConnection> {
	@Override
	public void handle(PacketPlayInRoomSwapTeam packet, PlayerConnection context) {
		if (!context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		
		ArkaPlayer player = context.getPlayer();
		if (player.getCurrentWaitingRoom() == null) {
			context.closeForViolation();
			return;
		}
		
		player.getCurrentWaitingRoom().swapTeam(player, packet.getTeamColor());
	}
}
