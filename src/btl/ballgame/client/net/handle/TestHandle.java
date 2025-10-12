package btl.ballgame.client.net.handle;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutCloseSocket;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityPosition;

public class TestHandle implements PacketHandler<PacketPlayOutEntityMetadata, CServerConnection> {
	@Override
	public void handle(PacketPlayOutEntityMetadata packet, CServerConnection context) {
		packet.getWatcher().entries().forEach(e -> {
			System.out.println(e.keyId + " -> " + e.value);
		});
	}
}
