package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInRoomSetReady;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.net.PlayerConnection;

public class ClientRoomReadyHandle implements PacketHandler<PacketPlayInRoomSetReady, PlayerConnection> {
	@Override
	public void handle(PacketPlayInRoomSetReady packet, PlayerConnection context) {
		if (!context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		
		ArkaPlayer player = context.getPlayer();
		if (player.getCurrentWaitingRoom() == null) {
			context.closeForViolation();
			return;
		}
		
		player.getCurrentWaitingRoom().setReady(player, packet.isReady());
	}
}
