package btl.ballgame.client.net.systems;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import btl.ballgame.shared.libs.IWorld;

public class CSWorld implements IWorld {
	private int width, height;
	private Map<Integer, CSEntity> allEntities = new HashMap<>();
	
	public CSWorld(int width, int height) {
		this.height = height;
		this.width = width;
	}
	
	public CSEntity getEntityById(int id) {
		return this.allEntities.get(id);
	}
	
	public boolean hasEntity(int id) {
		return allEntities.containsKey(id);
	}
	
	public void trackEntity(CSEntity entity) {
		allEntities.put(entity.getId(), entity);
	}
	
	public void untrack(int id) {
		allEntities.remove(id);
	}
	
	public Collection<CSEntity> getAllEntities() {
		return allEntities.values();
	}
	
	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public int getWidth() {
		return this.width;
	}
}
