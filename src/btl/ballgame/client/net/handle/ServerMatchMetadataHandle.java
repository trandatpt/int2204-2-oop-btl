package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidClient;
import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata;

public class ServerMatchMetadataHandle implements PacketHandler<PacketPlayOutMatchMetadata, CServerConnection> {
	@Override
	public void handle(PacketPlayOutMatchMetadata packet, CServerConnection context) {
		ArkanoidClient client = context.client;
		if (client.getActiveMatch() == null) {
			client.setActiveMatch(new ClientArkanoidMatch(packet.getMode()));
		}
		
		client.getActiveMatch().applyMetadata(packet);
	}
}
