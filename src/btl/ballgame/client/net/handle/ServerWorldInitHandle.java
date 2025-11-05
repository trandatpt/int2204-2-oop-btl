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
            try {
                GameRenderCanvas grc = new GameRenderCanvas();
                GameScreen ui = new GameScreen(grc);
                ArkanoidGame.manager().setScreen(ui);
                ui.requestGameFocus();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
		});
	}
}
