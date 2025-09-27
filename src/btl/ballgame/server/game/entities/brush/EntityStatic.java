package btl.ballgame.server.game.entities.brush;

import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.shared.libs.Location;

public abstract class EntityStatic extends WorldEntity {
	public EntityStatic(int id, Location location) {
		super(id, location);
	}
}
