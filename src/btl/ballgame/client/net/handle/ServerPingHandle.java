package btl.ballgame.client.net.handle;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInPong;
import btl.ballgame.protocol.packets.out.PacketPlayOutPing;

public class ServerPingHandle implements PacketHandler<PacketPlayOutPing, CServerConnection> {
	@Override
	public void handle(PacketPlayOutPing packet, CServerConnection context) {
		context.sendPacket(new PacketPlayInPong());
	}
}
