package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInPaddleControl;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.match.ArkanoidMatch;
import btl.ballgame.server.net.PlayerConnection;

public class ClientPaddleInputHandle implements PacketHandler<PacketPlayInPaddleControl, PlayerConnection> {
	@Override
	public void handle(PacketPlayInPaddleControl packet, PlayerConnection context) {
		if (!context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		
		ArkaPlayer player = context.getPlayer();
		ArkanoidMatch match = player.getCurrentGame();
		if (match == null) {
			context.closeForViolation();
			return;
		}
		
		match.paddleOf(player).enqueueMove(packet);
	}
}
