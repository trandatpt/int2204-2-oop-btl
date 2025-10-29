package btl.ballgame.server.game.entities.brush;

import btl.ballgame.server.game.WorldEntity;
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
		return 48;
	}

	@Override
	public int getHeight() {
		return 18;
	}
}
