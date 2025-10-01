package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.shared.libs.Location;

public class EntityPaddle extends EntityDynamic {

	public EntityPaddle(int id, Location location) {
		super(id, location);
	}
	
	public void moveRight() {
		setLocation(getLocation().setX(getLocation().getX() + 20));
	}
	
	public void moveLeft() {
		setLocation(getLocation().setX(getLocation().getX() - 20));
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
