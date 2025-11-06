package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInRequestRoomList;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.net.PlayerConnection;

public class ClientRoomListRequestHandle implements PacketHandler<PacketPlayInRequestRoomList, PlayerConnection> {
	@Override
	public void handle(PacketPlayInRequestRoomList packet, PlayerConnection context) {
		if (!context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		context.sendPacket(ArkanoidServer.getServer().getMatchManager().buildPublicRoomPacket());
	}
}
