package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.entities.BreakableEntity;
import btl.ballgame.server.game.entities.IOwnableEntity;
import btl.ballgame.server.game.entities.dynamic.EntityPaddle;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Location;

public class EntityBrick extends BreakableEntity {
	private int brickTint = 0xFFFFFF; // no tint
	
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
	
	/**
	 * Sets the color tint mask applied to the brick when rendered on the client.
	 * The tint is a 24-bit RGB value (but java only has 32-bit int).
	 *
	 * @param tint the RGB tint color to apply; 
	 *  defaults to {@code 0xFFFFFF} for no tint
	 */
	public void setTint(int tint) {
		this.brickTint = tint;
	}
	
	@Override
	public void onSpawn() {
		this.dataWatcher.watch(Constants.BRICK_TINT_META, brickTint);
	}
	
	@Override
	public int getMaxHealth() {
		return 1; // one hit and it breaks
	}
	
	@Override
	public void onObjectBroken(WorldEntity damager) {
		this.remove();
		if (damager instanceof IOwnableEntity ownable) {
			world.getHandle().onBrickDestroyed(this, ownable.getOwner());
		}
	}
}
