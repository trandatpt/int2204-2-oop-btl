package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata;

public class ServerMatchMetadataHandle implements PacketHandler<PacketPlayOutMatchMetadata, CServerConnection> {
	@Override
	public void handle(PacketPlayOutMatchMetadata packet, CServerConnection context) {
		ArkanoidClientCore client = context.client;
		if (client.getActiveMatch() == null) {
			// the server sent absolute bullshit
			context.closeWithNotify("Invalid server to client state!");
			return;
		}
		
		client.getActiveMatch().applyMetadata(packet);
	}
}
