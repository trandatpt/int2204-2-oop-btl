package btl.ballgame.server.net.handle;

import java.util.Arrays;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInPlayClassicArkanoid;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.match.ArkanoidMatch;
import btl.ballgame.server.game.match.MatchSettings;
import btl.ballgame.server.net.PlayerConnection;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import btl.ballgame.shared.libs.Constants.TeamColor;

public class ClientPlayClassicArkanoidHandle implements PacketHandler<PacketPlayInPlayClassicArkanoid, PlayerConnection> {
	@Override
	public void handle(PacketPlayInPlayClassicArkanoid packet, PlayerConnection context) {
		if (!context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		
		ArkaPlayer player = context.getPlayer();
		ArkanoidMatch match = player.getCurrentGame();
		if (match != null) {
			context.closeForViolation();
			return;
		}
		
		ArkanoidMatch solo = new ArkanoidMatch(new MatchSettings(ArkanoidMode.SOLO_ENDLESS, 0, 0, 5));
		solo.assignTeam(TeamColor.RED, Arrays.asList(player));
		solo.start();
	}
}
