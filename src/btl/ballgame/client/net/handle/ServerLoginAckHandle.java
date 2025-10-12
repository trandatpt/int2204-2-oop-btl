package btl.ballgame.client.net.handle;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutLoginAck;

public class ServerLoginAckHandle implements PacketHandler<PacketPlayOutLoginAck, CServerConnection> {
	@Override
	public void handle(PacketPlayOutLoginAck packet, CServerConnection context) {
		System.out.println(packet.getServerSideUUID());
	}
}
