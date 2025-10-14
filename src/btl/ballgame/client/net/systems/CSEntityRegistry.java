package btl.ballgame.client.net.systems;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import btl.ballgame.shared.UnknownEntityException;
import btl.ballgame.shared.libs.EntityType;

public class CSEntityRegistry {
	Map<Integer, Supplier<? extends CSEntity>> entityFactories = new HashMap<>();
	
	public void registerEntity(EntityType type, Supplier<? extends CSEntity> constructor) {
		entityFactories.put(type.ordinal(), constructor);
	}
	
	public CSEntity create(int entityTypeId) throws UnknownEntityException {
		Supplier<? extends CSEntity> supplier = this.entityFactories.get(entityTypeId);
		if (supplier == null) {
			throw new UnknownEntityException(entityTypeId);
		}
		return supplier.get();
	}
}
