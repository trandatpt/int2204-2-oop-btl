package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.server.game.entities.dynamic.EntityDynamic;
import btl.ballgame.shared.libs.Location;

public class EntityBrick extends EntityDynamic implements IBreakableEntity {

	public EntityBrick(int id, Location location) {
		super(id, location);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public int getWidth() {
		return 36;
	}

	@Override
	public int getHeight() {
		return 36;
	}

}
