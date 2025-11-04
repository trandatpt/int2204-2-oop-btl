package btl.ballgame.client.net.handle;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.client.net.systems.entities.CEntityPaddleLocal;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutClientFlags;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityDestroy;

public class ServerClientFlagsHandle implements PacketHandler<PacketPlayOutClientFlags, CServerConnection> {
	@Override
	public void handle(PacketPlayOutClientFlags packet, CServerConnection context) {
		CEntityPaddleLocal paddle = context.client.getPaddle();
		if (paddle == null) {
			return; // the server is sending bullshit again
		}
		
		if (!packet.isMovementDisabled()) {
			paddle.setCanMoveStatus(true);
		}
	}
}
