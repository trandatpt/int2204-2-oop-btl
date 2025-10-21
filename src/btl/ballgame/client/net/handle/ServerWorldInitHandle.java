package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidClient;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutWorldInit;

public class ServerWorldInitHandle implements PacketHandler<PacketPlayOutWorldInit, CServerConnection> {
	@Override
	public void handle(PacketPlayOutWorldInit packet, CServerConnection context) {
		ArkanoidClient client = context.client;
		if (client.getActiveMatch() == null) {
			// the server sent bullshit
			context.closeWithNotify("Invalid client-server synchronization state!");
			return;
		}
		
		client.getActiveMatch().createGameWorld(
			packet.getWorldWidth(), 
			packet.getWorldHeight()
		);
	}
}
