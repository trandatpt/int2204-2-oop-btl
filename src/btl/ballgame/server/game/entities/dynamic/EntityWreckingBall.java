package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.shared.libs.Location;

public class EntityWreckingBall extends EntityDynamic {

	public EntityWreckingBall(int id, Location location) {
		super(id, location);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public int getWidth() {
		return 36;
	}

	@Override
	public int getHeight() {
		return 36;
	}

}
