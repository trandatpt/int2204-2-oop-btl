package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutLoginAck;

public class ServerLoginAckHandle implements PacketHandler<PacketPlayOutLoginAck, CServerConnection> {
	@Override
	public void handle(PacketPlayOutLoginAck packet, CServerConnection context) {
		ArkanoidClientCore client = context.client;
		client.setUser(packet.getUserName(), packet.getServerSideUUID());
	}
}
