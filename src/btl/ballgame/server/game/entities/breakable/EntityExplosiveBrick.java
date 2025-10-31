package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.entities.BreakableEntity;
import btl.ballgame.shared.libs.Location;

public class EntityExplosiveBrick extends EntityBrick {
	public EntityExplosiveBrick(int id, Location location) {
		super(id, location);
	}
	
	@Override
	public void onObjectBroken(WorldEntity damager) {
		// entities within 50 units
		var nearby = world.getNearbyEntities(getBoundingBox().expand(50));
		nearby.remove(this); // exclude itself
		for (WorldEntity entity : nearby) {
			if (entity instanceof BreakableEntity be) {
				be.damage(this, 1_000); // deals 1000 damage
			}
		}
		this.remove();
	}
}
