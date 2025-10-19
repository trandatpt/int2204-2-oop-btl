package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.shared.libs.Location;

public abstract class EntityBuff extends EntityDynamic {
	public EntityBuff(int id, Location location) {
		super(id, location);
	}
}
