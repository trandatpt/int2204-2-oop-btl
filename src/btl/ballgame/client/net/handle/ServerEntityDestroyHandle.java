package btl.ballgame.client.net.handle;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityDestroy;

public class ServerEntityDestroyHandle implements PacketHandler<PacketPlayOutEntityDestroy, CServerConnection> {
	@Override
	public void handle(PacketPlayOutEntityDestroy packet, CServerConnection context) {
		CSWorld world = context.client.getActiveWorld();
		if (world == null) {
			return; // the server is sending bullshit again
		}
		
		int entityId = packet.getEntityId();
		if (!world.hasEntity(entityId)) {
			System.err.println("[ENTITY REMOVE] Entity with ID " + entityId + " does not exist in the client-bound world, ignoring.");
			return;
		}
		
		world.getEntityById(entityId).onEntityDespawn();
		world.untrack(entityId);
	}
}
