package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.server.game.entities.BreakableEntity;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Location;

public class EntityBrick extends BreakableEntity {

	public EntityBrick(int id, Location location) {
		super(id, location);
	}

	@Override
	protected void tick() {}

	@Override
	public int getWidth() {
		return Constants.BRICK_WIDTH;
	}

	@Override
	public int getHeight() {
		return Constants.BRICK_HEIGHT;
	}
	
	@Override
	public int getMaxHealth() {
		return 1;
	}

	@Override
	public void onObjectBroken() {
		this.remove();		
	}
}
