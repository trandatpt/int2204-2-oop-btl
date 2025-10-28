package btl.ballgame.client.net.handle;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.net.systems.CSEntity;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.client.net.systems.entities.CEntityPaddle;
import btl.ballgame.client.net.systems.entities.CEntityPaddleLocal;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntitySpawn;
import btl.ballgame.shared.UnknownEntityException;
import btl.ballgame.shared.libs.EntityType;

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
			
			// WHAT THE FUCK IS THIS CODE
			// ABSOLUTE GARBAGE, but hey, its fast?
			CSEntity entity;
			// creates a new entity, checks if it is the paddle owned by this client
			// if yes, instantiate the local predicted version, otherwise use normal
			// registry to create shit
			if (packet.getEntityId() == EntityType.ENTITY_PADDLE.ordinal()
			 && CEntityPaddleLocal.isOwnedByThisClient(packet.getDataWatcher())
			) {
				entity = new CEntityPaddleLocal(); // this looks bad, but trust me, its worth it
			} else {
				entity = context.client.getEntityRegistry().create(packet.getEntityTypeId());
			}
			
			// copy the server properties to the client representation of the entity
			entity.copyPropertiesFrom(packet);
			
			// begin tracking the entity (render-able)
			entity.bindWorld(world);
			world.trackEntity(entity);
			entity.onEntitySpawn(); // event-call
		} catch (UnknownEntityException e) {
			context.closeWithNotify("Server sent an invalid entity: EntityServerID@" + packet.getEntityTypeId());
		}
	}
}
