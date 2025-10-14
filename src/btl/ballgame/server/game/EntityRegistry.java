package btl.ballgame.server.game;

import java.util.HashMap;
import java.util.Map;
import btl.ballgame.shared.libs.EntityType;

public class EntityRegistry {
	private Map<Class<? extends WorldEntity>, EntityType> registered = new HashMap<>();
	
	public void registerEntity(EntityType type, Class<? extends WorldEntity> clazz) {
		if (registered.containsValue(type) || registered.containsKey(clazz)) {
			throw new IllegalArgumentException("Entity type \"" + type + "\" with class \"" + clazz.toString() + "\" is already registered!");
		}
		registered.put(clazz, type);
	}
	
	public EntityType getRegisteredType(Class<? extends WorldEntity> clazz) {
		return registered.get(clazz);
	}
}
