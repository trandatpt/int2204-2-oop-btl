package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.ui.game.GameRenderCanvas;
import btl.ballgame.client.ui.game.GameScreen;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutWorldInit;
import javafx.application.Platform;

public class ServerWorldInitHandle implements PacketHandler<PacketPlayOutWorldInit, CServerConnection> {
	@Override
	public void handle(PacketPlayOutWorldInit packet, CServerConnection context) {
		ArkanoidClientCore client = context.client;
		if (client.getActiveMatch() == null) {
			// the server sent bullshit
			context.closeWithNotify("Invalid client-server synchronization state!");
			return;
		}
		
		client.getActiveMatch().createGameWorld(
			packet.getWorldWidth(), 
			packet.getWorldHeight()
		);
		
		Platform.runLater(() -> {
            // 1. Create the game renderer FIRST
            GameRenderCanvas grc = new GameRenderCanvas();

            // 2. Create the UI screen and PASS the renderer TO it
            GameScreen ui = new GameScreen(grc);

            // 3. Set the 'ui' (GameScreen) as the only screen
            ArkanoidGame.manager().setScreen(ui);

            // 4. Tell the 'ui' to give focus to the game
            ui.requestGameFocus();
		});
	}
}
