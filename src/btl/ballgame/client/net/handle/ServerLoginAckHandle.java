package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.ui.menus.InformationalScreen;
import btl.ballgame.client.ui.menus.MenuUtils;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutLoginAck;
import javafx.application.Platform;

public class ServerLoginAckHandle implements PacketHandler<PacketPlayOutLoginAck, CServerConnection> {
	@Override
	public void handle(PacketPlayOutLoginAck packet, CServerConnection context) {
		ArkanoidClientCore client = context.client;
		if (!packet.isSuccessful()) {
			// show the server error message
			Platform.runLater(() -> {
				InformationalScreen failed = new InformationalScreen(
					"Log-in Error", 
					"Failed to authenticate with the server",
					packet.getErrorMessage()
				);
				failed.addButton("Return to Login Menu", MenuUtils::displayLoginScreen);
				ArkanoidGame.manager().setScreen(failed);
			});
			return;
		}
		client.setUser(packet.getUserName(), packet.getServerSideUUID());
		Platform.runLater(() -> {
			InformationalScreen test = new InformationalScreen("test", "logged in as " + packet.getUserName());
			ArkanoidGame.manager().setScreen(test);
		});
	}
}
