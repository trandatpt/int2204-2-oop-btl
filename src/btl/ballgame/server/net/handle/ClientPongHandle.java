package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInPong;
import btl.ballgame.server.net.PlayerConnection;

public class ClientPongHandle implements PacketHandler<PacketPlayInPong, PlayerConnection> {
	@Override
	public void handle(PacketPlayInPong packet, PlayerConnection context) {}
}
