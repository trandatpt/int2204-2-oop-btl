package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInClientHello;
import btl.ballgame.protocol.packets.out.PacketPlayOutHelloAck;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.PlayerManager;
import btl.ballgame.server.net.PlayerConnection;

public class ClientHelloHandle implements PacketHandler<PacketPlayInClientHello, PlayerConnection> {
	@Override
	public void handle(PacketPlayInClientHello packet, PlayerConnection context) {
		String rejectReason = null; // assume successful at first (null = success)
		
		// do not allow neither older nor newer clients
		if (packet.getProtocolVersion() > ProtoUtils.PROTOCOL_VERSION) {
			rejectReason = "Outdated Server! This server is still on version: " + Integer.toHexString(ProtoUtils.PROTOCOL_VERSION);
		} else if (packet.getProtocolVersion() < ProtoUtils.PROTOCOL_VERSION) {
			rejectReason = "Outdated Client! This server is on version: " + Integer.toHexString(ProtoUtils.PROTOCOL_VERSION);
		}
		
		// check if the server is full
		ArkanoidServer server = ArkanoidServer.getServer();
		PlayerManager playerMan = server.getPlayerManager();
		
		synchronized (playerMan) {
			if (rejectReason == null && playerMan.total() >= server.getMaxPlayers()) {
				// only show this reason IF the client proto version is correct
				rejectReason = "This server is full!";
			}
		}
		
		// acknowledge
		PacketPlayOutHelloAck ack = new PacketPlayOutHelloAck(rejectReason);
		if (rejectReason != null) {
			context.dispatchLastPacketAndClose(ack);
		} else {
			context.completeHandshake(); // this could be a valid client
			context.sendPacket(ack);
		}
	}
}
