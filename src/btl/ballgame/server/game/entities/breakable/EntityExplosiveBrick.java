package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.entities.BreakableEntity;
import btl.ballgame.shared.libs.Location;

public class EntityExplosiveBrick extends EntityBrick {
	public EntityExplosiveBrick(int id, Location location) {
		super(id, location);
	}

	@Override
	public void onObjectBroken() {
		// các thực thể trong khoảng 50 unit
		var nearby = world.getNearbyEntities(getBoundingBox().expand(50));
		nearby.remove(this); // hàm trên trả về cả bản thân nên bỏ đi
		for (WorldEntity entity : nearby) {
			if (entity instanceof BreakableEntity be) {
				be.damage(1_000); // deals 1000 damage
			}
		}
		this.remove();
	}
}
