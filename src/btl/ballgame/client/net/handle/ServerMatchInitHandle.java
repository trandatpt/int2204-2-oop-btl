package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.ui.menus.MenuUtils;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchJoin;

public class ServerMatchInitHandle implements PacketHandler<PacketPlayOutMatchJoin, CServerConnection> {
	@Override
	public void handle(PacketPlayOutMatchJoin packet, CServerConnection context) {
		ArkanoidClientCore client = context.client;
		if (client.getActiveMatch() != null) {
			// the server sent bullshit
			context.closeWithNotify("Invalid client-server synchronization state!");
			return;
		}
		
		client.setActiveMatch(new ClientArkanoidMatch(
			packet.getArkanoidMode(), 
			packet.getNameMap()
		));
		client.setControlPaddle(null); // reset state
		
		MenuUtils.showLoadingScreen("Joining world...");
	}
}
