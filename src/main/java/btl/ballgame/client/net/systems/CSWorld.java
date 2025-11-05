package btl.ballgame.client.net.systems;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import btl.ballgame.shared.libs.IWorld;

public class CSWorld implements IWorld {
	private int width, height;
	private ConcurrentHashMap<Integer, CSEntity> allEntities = new ConcurrentHashMap<>();
	private ParticleSystem particleSystem;
	
	public CSWorld(int width, int height) {
		this.height = height;
		this.width = width;
		this.particleSystem = new ParticleSystem();
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
	
	public ParticleSystem particles() {
		return particleSystem;
	}
	
	public void tick() {
		for (CSEntity entity : getAllEntities()) {
			if (entity instanceof ITickableCEntity tickable) {
				try { tickable.onTick(); } catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
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
