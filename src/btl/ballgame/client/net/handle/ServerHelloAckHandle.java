package btl.ballgame.client.net.handle;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.ui.menus.MenuUtils;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutHelloAck;
import javafx.application.Platform;

public class ServerHelloAckHandle implements PacketHandler<PacketPlayOutHelloAck, CServerConnection> {
	@Override
	public void handle(PacketPlayOutHelloAck packet, CServerConnection context) {
		if (!packet.isSuccessful()) {
			MenuUtils.failedToConnectScreen(packet.getRejectReason());
			context.handleGracefulDisconnect();
			return;
		}
		// the server agreed
		Platform.runLater(MenuUtils::displayLoginScreen);
	}
}
