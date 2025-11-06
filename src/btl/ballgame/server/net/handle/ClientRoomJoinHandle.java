package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInJoinRoom;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.net.PlayerConnection;

public class ClientRoomJoinHandle implements PacketHandler<PacketPlayInJoinRoom, PlayerConnection> {
	@Override
	public void handle(PacketPlayInJoinRoom packet, PlayerConnection context) {
		if (!context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		
		ArkaPlayer player = context.getPlayer();
		if (player.getCurrentWaitingRoom() != null) {
			player.leaveWaitingRoom();
			return;
		}
		
		ArkanoidServer.getServer().getMatchManager().joinRoom(
			player, packet.getRoomID()
		);
	}
}
