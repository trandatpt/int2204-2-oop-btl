package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInDisconnect;
import btl.ballgame.server.net.PlayerConnection;

public class ClientDisconnectHandle implements PacketHandler<PacketPlayInDisconnect, PlayerConnection> {
	@Override
	public void handle(PacketPlayInDisconnect packet, PlayerConnection context) {
		System.out.println("client gracefully left");
		context.closeConnection();
	}
}
