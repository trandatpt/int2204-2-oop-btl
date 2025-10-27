package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.shared.libs.Constants.BuffType;
import btl.ballgame.shared.libs.Constants.TeamColor;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

public class EntityBuff extends EntityDynamic {
	final int velocity = 9;
	final double width = 18;
	final double height = 18;
	private final BuffType buffType;

	public EntityBuff(int id, Location location, BuffType type, TeamColor teamColor) {
		super(id, location);
		this.buffType = type;
		this.setDirection(teamColor.equals(TeamColor.RED)
			? new Vector2f(0, 1)
			: new Vector2f(0, -1)
		);
	}

	/**
	 * Updates the facing direction of the ball.
	 *
	 * @param lookVector New normalized direction vector.
	 */
	private void setDirection(Vector2f lookVector) {
		setLocation(getLocation().clone().setDirection(lookVector));
	}

	@Override
	public void onSpawn() {
		this.dataWatcher.watch(Constants.BUFF_TYPE_META, buffType.ordinal());
	}

	@Override
	protected void tick() {
		// move the ball forward
		Location currentLoc = getLocation();
		Vector2f direction = currentLoc.getDirection().normalize();
		setLocation(currentLoc.clone().add(direction.multiply(velocity)));
	}

}
