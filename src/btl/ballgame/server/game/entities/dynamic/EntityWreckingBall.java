package btl.ballgame.server.game.entities.dynamic;

import java.util.List;
import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.WorldServer;
import btl.ballgame.server.game.WorldVisualizer;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

public class EntityWreckingBall extends EntityDynamic {
	public static final float DEFAULT_SPEED = 5.0f; // units per tick 
	private float speed = DEFAULT_SPEED;
	
	public EntityWreckingBall(int id, Location location) {
		super(id, location);
		WorldVisualizer.addVectorVisualizer(id);
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
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
		).normalize();
		setDirection(bounce);
	}
	
	@Override
	public void tick() {
		// move the ball every tick by "speed" (constant velocity)
		Location pos = getLocation();
		Vector2f direction = pos.getDirection().normalize();
		Location newPos = pos.clone().add(direction.multiply(speed));
		setLocation(newPos);
		
		// make the ball reacts to the world
		WorldServer world = (WorldServer) getWorld();
		int worldWidth = world.getWidth();
		int worldHeight = world.getHeight();
		
		boolean bouncedFromWorld = false;
		Vector2f worldNormal = new Vector2f(0, 0);
		Vector2f separation = new Vector2f(0, 0);

		if (getBoundingBox().minX < 0) { // ball touches left wall
			separation.x = -getBoundingBox().minX;
			worldNormal = new Vector2f(1, 0);
			bouncedFromWorld = true;
		} else if (getBoundingBox().maxX > worldWidth) { // ball touches right wall
			separation.x = getBoundingBox().maxX - worldWidth;
			worldNormal = new Vector2f(-1, 0);
			bouncedFromWorld = true;
		}
		
		// DEMO code, wont be used in prod
		if (getBoundingBox().minY < 0) {
			separation.y = -getBoundingBox().minY;
			worldNormal = Math.abs(separation.x) > Math.abs(separation.y) 
				? new Vector2f(Math.signum(separation.x), 0)
				: new Vector2f(0, 1)
			;
			bouncedFromWorld = true;
		} else if (getBoundingBox().maxY > worldHeight) {
			separation.y = getBoundingBox().maxY - worldHeight;
			worldNormal = Math.abs(separation.x) > Math.abs(separation.y) 
				? new Vector2f(Math.signum(separation.x), 0)
				: new Vector2f(0, -1)
			;
			bouncedFromWorld = true;
		}
		
		// if the ball interacted with the world, apply the force
		if (bouncedFromWorld) {
			setLocation(pos.clone().add(separation));
			bounce(worldNormal);
		}
		
		// make the ball reacts to collisions by other entities
		List<WorldEntity> collided = queryCollisions();
		for (WorldEntity collider : collided) {
			if (!collider.isCollidable()) {
				continue;
			}

			Vector2f overlap = getBoundingBox().getIntersectionDepth(collider.getBoundingBox());
			Vector2f normal;
			Vector2f push = new Vector2f(0, 0);

			if (Math.abs(overlap.getX()) < Math.abs(overlap.getY())) {
				normal = new Vector2f(Math.signum(overlap.getX()), 0);
				push.x = overlap.getX();
			} else {
				normal = new Vector2f(0, Math.signum(overlap.getY()));
				push.y = overlap.getY();
			}

			setLocation(getLocation().add(push));
			bounce(normal);
			break;
		}
		WorldVisualizer.updateVV(getId(), direction.clone().multiply(5));
	}
	
	private void setDirection(Vector2f lookVector) {
		setLocation(getLocation().clone().setDirection(lookVector));
	}
	
	@Override
	public int getWidth() {
		return 32 / 1;
	}

	@Override
	public int getHeight() {
		return 32 / 1;
	}

}
