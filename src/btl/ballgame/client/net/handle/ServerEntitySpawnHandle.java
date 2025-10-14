package btl.ballgame.client.net.handle;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.net.systems.CSEntity;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntitySpawn;
import btl.ballgame.shared.UnknownEntityException;

public class ServerEntitySpawnHandle implements PacketHandler<PacketPlayOutEntitySpawn, CServerConnection> {
	@Override
	public void handle(PacketPlayOutEntitySpawn packet, CServerConnection context) {
		try {
			CSWorld world = context.client.getActiveWorld();
			if (world == null) {
				return; // the server is sending bullshit again
			}
			
			int entityId = packet.getEntityId();
			// allow the client to render duplicated entity ids by
			// by discarding the old one
			if (world.hasEntity(entityId)) {
				System.err.println("Duplicated entity with ID " + entityId + ". Replacing outdated one...");
				world.untrack(entityId);
			}
			
			CSEntity entity = context.client.getEntityRegistry().create(packet.getEntityTypeId());
			// copy the server properties to the client representation of the entity
			entity.copyPropertiesFrom(packet);
			entity.bindWorld(world);
			
			// begin tracking the entity (render-able)
			world.trackEntity(entity);
			entity.onEntitySpawn(); // event-call
		} catch (UnknownEntityException e) {
			context.closeWithNotify("Server sent an invalid entity: EntityServerID@" + packet.getEntityTypeId());
		}
	}
}
