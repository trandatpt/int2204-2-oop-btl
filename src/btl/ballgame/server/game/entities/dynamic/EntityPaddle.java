package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.shared.libs.Location;

public class EntityPaddle extends EntityDynamic {

	public EntityPaddle(int id, Location location) {
		super(id, location);
	}
	
	public void moveRight() {
		Location oldLoc = getLocation();
		setLocation(getLocation().setX(getLocation().getX() + 20));
		if (queryCollisions().size() >= 1) {
			setLocation(oldLoc);
		}
	}
	
	public void moveLeft() {
		Location oldLoc = getLocation();
		setLocation(getLocation().setX(getLocation().getX() - 20));
		if (queryCollisions().size() >= 1) {
			setLocation(oldLoc);
		}
	}

	@Override
	public void tick() {
		
	}

	@Override
	public int getWidth() {
		return 36 * 2;
	}

	@Override
	public int getHeight() {
		return 18;
	}

}
