package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.shared.libs.Location;

public class EntityPaddle extends EntityDynamic {

	public EntityPaddle(int id, Location location) {
		super(id, location);
	}
	
	private int toMove = 0;
	public void moveRight() {
		toMove = 50;
	}
	
	public void moveLeft() {
		toMove = -50;
	}

	@Override
	public void tick() {
		if (toMove != 0) {
			Location oldLoc = getLocation();
			setLocation(getLocation().setX(getLocation().getX() + (toMove < 0 ? -5 : 5)));
			if (toMove < 0) {
				toMove += 5;
			} else {
				toMove -= 5;
			}
		}
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
