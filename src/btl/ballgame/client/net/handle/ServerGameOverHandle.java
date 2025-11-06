package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.ui.game.GameOverScreen;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutGameOver;
import javafx.application.Platform;

public class ServerGameOverHandle implements PacketHandler<PacketPlayOutGameOver, CServerConnection> {
	@Override
	public void handle(PacketPlayOutGameOver packet, CServerConnection context) {
		ArkanoidClientCore client = context.client;
		if (client.getActiveMatch() == null) {
			context.closeWithNotify("Invalid client-server synchronization state!");
			return;
		}
		Platform.runLater(() -> {
			try {
				Screen currentGameScreen = ArkanoidGame.manager().getCurrentScreen();
				if (currentGameScreen != null) {
					currentGameScreen.onRemove();
				}
				GameOverScreen gameOverScreen = new GameOverScreen(packet);
				ArkanoidGame.manager().setScreen(gameOverScreen);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}