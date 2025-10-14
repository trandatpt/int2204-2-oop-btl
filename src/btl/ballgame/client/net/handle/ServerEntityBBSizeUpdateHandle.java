package btl.ballgame.client.net.handle;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityBBSizeUpdate;

public class ServerEntityBBSizeUpdateHandle implements PacketHandler<PacketPlayOutEntityBBSizeUpdate, CServerConnection> {
	@Override
	public void handle(PacketPlayOutEntityBBSizeUpdate packet, CServerConnection context) {
		CSWorld world = context.client.getActiveWorld();
		if (world == null) {
			return; // the server is sending bullshit again
		}
		
		int entityId = packet.getEntityId();
		if (!world.hasEntity(entityId)) {
			System.err.println("[ENTITY BB] Entity with ID " + entityId + " does not exist in the client-bound world, ignoring.");
			return;
		}
		
		world.getEntityById(entityId).updateBBSizeFrom(packet);
	}
}
