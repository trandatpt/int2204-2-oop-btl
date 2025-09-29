package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

public class EntityWreckingBall extends EntityDynamic {
	private float speed = 0.0f;
	
	public EntityWreckingBall(int id, Location location) {
		super(id, location);
	}
	
	/**
	 * Make the ball bounces off a surface/another entity
	 * 
	 * @param normal the normal vector (véc-tơ pháp tuyến) of the surface
	 */
	private void bounce(Vector2f normal) {
		Vector2f incoming = getLocation().getDirection();
		Vector2f bounce = incoming.subtract(
			// dot là tích vô hướng
			normal.multiply(2 * incoming.dot(normal)) // r = d - 2(d dot n)n
		);
		setLocation(getLocation().clone().setDirection(bounce));
	}
	
	@Override
	public void tick() {
		Location pos = getLocation();
		Location newPos = pos.clone().add(
			pos.getDirection().normalize().multiply(speed)
		);
		
		// move the ball
		setLocation(newPos);
	}

	@Override
	public int getWidth() {
		return 64;
	}

	@Override
	public int getHeight() {
		return 64;
	}

}
