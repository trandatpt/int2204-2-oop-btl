package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.ui.menus.LobbyScreen;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutListPublicRooms;

public class ServerListPublicRoomsHandle implements PacketHandler<PacketPlayOutListPublicRooms, CServerConnection> {
	@Override
	public void handle(PacketPlayOutListPublicRooms packet, CServerConnection context) {
		if (ArkanoidGame.manager().getCurrentScreen() instanceof LobbyScreen ls) {
			ls.updateLobbyWith(packet);
		}
	}
}
