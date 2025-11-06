package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.ui.menus.LobbyScreen;
import btl.ballgame.client.ui.menus.MenuUtils;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutRoomJoinError;
import javafx.scene.control.Alert.AlertType;

public class ServerRoomJoinErrorHandle implements PacketHandler<PacketPlayOutRoomJoinError, CServerConnection> {
	@Override
	public void handle(PacketPlayOutRoomJoinError packet, CServerConnection context) {
		if (!(ArkanoidGame.manager().getCurrentScreen() instanceof LobbyScreen ls)) {
			MenuUtils.displayLobbyScreen();
		}
		MenuUtils.toast(AlertType.ERROR, packet.getError());
	}
}
