package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ui.menus.RoomScreenDynamic;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutRoomUpdate;
import btl.ballgame.client.net.CServerConnection;
import javafx.application.Platform;

public class ServerWaitingRoomUpdateHandle implements PacketHandler<PacketPlayOutRoomUpdate, CServerConnection> {
	@Override
	public void handle(PacketPlayOutRoomUpdate packet, CServerConnection context) {
		Platform.runLater(() -> {
			// Get current screen
			var manager = ArkanoidGame.manager();

			RoomScreenDynamic screen;
			if (manager.getCurrentScreen() instanceof RoomScreenDynamic current) {
				screen = current;
			} else {
				// not currently in a room — create and set a new one
				screen = new RoomScreenDynamic();
				manager.setScreen(screen);
			}

			// now update UI safely
			screen.updateRoom(packet);
		});
	}
}
