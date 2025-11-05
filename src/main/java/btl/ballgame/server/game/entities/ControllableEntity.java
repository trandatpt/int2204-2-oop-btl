package btl.ballgame.server.game.entities;

import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.shared.libs.Location;

/**
 * Represents a dynamic entity in the game that can be manipulated by
 * an {@link ArkaPlayer} via a client-bound (PlayIn) packet
 */
public abstract class ControllableEntity extends WorldEntity {
	/**
	 * This is a marker class
	 */
	public ControllableEntity(int id, Location location) {
		super(id, location);
	}
}
