package btl.ballgame.client.net.handle;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutCloseSocket;

public class ServerSocketCloseHandle implements PacketHandler<PacketPlayOutCloseSocket, CServerConnection> {
	@Override
	public void handle(PacketPlayOutCloseSocket packet, CServerConnection context) {
		context.handleGracefulDisconnect();
	}
}
