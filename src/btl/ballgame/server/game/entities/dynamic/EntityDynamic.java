package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.shared.libs.Location;

public abstract class EntityDynamic extends WorldEntity {
	public EntityDynamic(int id, Location location) {
		super(id, location);
	}
}
