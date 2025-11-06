package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.ui.menus.LeaderboardScreen;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutGetAllPlayers;
import javafx.application.Platform;

public class ServerListPlayersLeaderboardHandle implements PacketHandler<PacketPlayOutGetAllPlayers, CServerConnection> {
	@Override
	public void handle(PacketPlayOutGetAllPlayers packet, CServerConnection context) {
		if (ArkanoidGame.manager().getCurrentScreen() instanceof LeaderboardScreen lbs) {
			Platform.runLater(() -> {
				lbs.updateLeaderboard(packet.getPlayerDetails());
			});
		}
	}
}
