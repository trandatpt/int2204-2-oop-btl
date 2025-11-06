package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInPauseGame;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.match.ArkanoidMatch;
import btl.ballgame.server.net.PlayerConnection;

public class ClientPauseGameHandle implements PacketHandler<PacketPlayInPauseGame, PlayerConnection> {
	@Override
	public void handle(PacketPlayInPauseGame packet, PlayerConnection context) {
		if (!context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		
		ArkaPlayer player = context.getPlayer();
		ArkanoidMatch match = player.getCurrentGame();
		if (match == null) {
			return;
		}
		
		if (!match.getGameMode().isSinglePlayer()) {
			context.closeWithNotify("Attempted to pause a multiplayer game!");
			return;
		}
		
		match.setClientRequestedPause(packet.isPaused());
		match.syncMatchStateWithClients();
	}
}
