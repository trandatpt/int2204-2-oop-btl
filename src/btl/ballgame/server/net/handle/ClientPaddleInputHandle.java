package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInPaddleInput;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.match.ArkanoidMatch;
import btl.ballgame.server.net.PlayerConnection;

public class ClientPaddleInputHandle implements PacketHandler<PacketPlayInPaddleInput, PlayerConnection> {
	@Override
	public void handle(PacketPlayInPaddleInput packet, PlayerConnection context) {
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
