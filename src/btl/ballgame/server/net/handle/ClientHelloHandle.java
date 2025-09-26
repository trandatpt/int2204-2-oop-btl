package btl.ballgame.server.net.handle;

import java.util.UUID;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInClientHello;
import btl.ballgame.protocol.packets.out.PacketPlayOutServerAck;
import btl.ballgame.server.net.PlayerConnection;

public class ClientHelloHandle implements PacketHandler<PacketPlayInClientHello, PlayerConnection> {

	@Override
	public void handle(PacketPlayInClientHello packet, PlayerConnection context) {
		System.out.println(packet.who());
		context.sendPacket(new PacketPlayOutServerAck(UUID.randomUUID()));
	}

}
