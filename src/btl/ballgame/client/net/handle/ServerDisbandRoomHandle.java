package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.ui.menus.LobbyScreen;
import btl.ballgame.client.ui.menus.MenuUtils;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutRoomDisband;
import javafx.scene.control.Alert.AlertType;

public class ServerDisbandRoomHandle implements PacketHandler<PacketPlayOutRoomDisband, CServerConnection> {
	@Override
	public void handle(PacketPlayOutRoomDisband packet, CServerConnection context) {
		if (!(ArkanoidGame.manager().getCurrentScreen() instanceof LobbyScreen ls)) {
			MenuUtils.displayLobbyScreen();
		}
		MenuUtils.toast(AlertType.INFORMATION, "The host has disbanded the room or left the server. You have been sent back to the lobby!");
	}
}
