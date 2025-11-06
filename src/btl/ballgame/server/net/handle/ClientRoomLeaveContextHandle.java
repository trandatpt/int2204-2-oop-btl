package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInLeaveContext;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.net.PlayerConnection;

public class ClientRoomLeaveContextHandle implements PacketHandler<PacketPlayInLeaveContext, PlayerConnection> {
	@Override
	public void handle(PacketPlayInLeaveContext packet, PlayerConnection context) {
		if (!context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		
		ArkaPlayer player = context.getPlayer();
		if (!player.isBusy()) {
			return;
		}
		
		if (player.getCurrentWaitingRoom() != null) {
			player.getCurrentWaitingRoom().removePlayer(player);
		} else if (player.getCurrentGame() != null) {
			player.getCurrentGame().onPlayerLeft(player);
		}
	}
}
