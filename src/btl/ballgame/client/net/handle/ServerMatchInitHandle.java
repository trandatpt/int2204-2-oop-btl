package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidClient;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutInitMatch;
import btl.ballgame.protocol.packets.out.PacketPlayOutLoginAck;

public class ServerMatchInitHandle implements PacketHandler<PacketPlayOutInitMatch, CServerConnection> {
	@Override
	public void handle(PacketPlayOutInitMatch packet, CServerConnection context) {
		ArkanoidClient client = context.client;
		client.setActiveWorld(new CSWorld(packet.getWorldWidth(), packet.getWorldHeight()));
	}
}
