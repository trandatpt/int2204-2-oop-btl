package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInRequestRoomList;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.net.PlayerConnection;

public class ClientRoomListRequestHandle implements PacketHandler<PacketPlayInRequestRoomList, PlayerConnection> {
	@Override
	public void handle(PacketPlayInRequestRoomList packet, PlayerConnection context) {
		if (!context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		// if the player is still "in-game" while they are in the lobby, leave it
		ArkaPlayer player = context.getPlayer();
		if (player.getCurrentGame() != null) {
			player.leaveGame();
		}
		context.sendPacket(ArkanoidServer.getServer().getMatchManager().buildPublicRoomPacket());
	}
}
