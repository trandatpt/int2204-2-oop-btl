package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.entities.BreakableEntity;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Location;

public class EntityExplosiveBrick extends EntityBrick {
	private boolean primed = false;
	private int countdown = (int) (Constants.TICKS_PER_SECOND * 1.5);
	private WorldEntity primerEntity;
	
	public EntityExplosiveBrick(int id, Location location) {
		super(id, location);
	}
	
	@Override
	public int getMaxHealth() {
		return Integer.MAX_VALUE; // this block is invincible, but it will explode
	}
	
	@Override
	protected void tick() {
		if (this.primed) {
			if (--this.countdown <= 0) {
				onObjectBroken(this.primerEntity);
			}
		}
	}
	
	@Override
	public void onSpawn() {
		dataWatcher.watch(Constants.EXPLOSIVE_PRIMED_META, false);
	}
	
	@Override
	public void onObjectDamaged(WorldEntity damager, int damage) {
		if (this.primed) {
			return;
		}
		dataWatcher.watch(Constants.EXPLOSIVE_PRIMED_META, true);
		this.primed = true;
		this.primerEntity = damager;
	}
	
	@Override
	public void onObjectBroken(WorldEntity damager) {
		// entities within 50 units
		var nearby = world.getNearbyEntities(getBoundingBox().expand(50));
		nearby.remove(this); // exclude itself
		for (WorldEntity entity : nearby) {
			if (entity instanceof BreakableEntity be) {
				be.damage(damager, 1_000); // deals 1000 damage, damager = the one that damaged this
			}
		}
		super.onObjectBroken(damager);
	}
}
