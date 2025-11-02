package btl.ballgame.server.game.entities.brush;

import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Location;

public class EntityUnbreakableBrick extends WorldEntity {
    public EntityUnbreakableBrick(int id, Location location) {
        super(id, location);
    }

    @Override
	protected void tick() {
	}

	@Override
	public int getWidth() {
		return Constants.BRICK_WIDTH;
	}

	@Override
	public int getHeight() {
		return Constants.BRICK_HEIGHT;
	}
}
