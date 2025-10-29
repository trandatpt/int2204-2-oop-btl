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
			Platform.runLater(MenuUtils::displayLoginScreen);
			return;
		}
		client.setUser(packet.getUserName(), packet.getServerSideUUID());
		Platform.runLater(() -> {
			InformationalScreen test = new InformationalScreen("test", "logged in as " + packet.getUserName());
			ArkanoidGame.manager().setScreen(test);
		});
	}
}
