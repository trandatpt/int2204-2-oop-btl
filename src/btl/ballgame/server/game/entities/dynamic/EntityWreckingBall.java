package btl.ballgame.server.game.entities.dynamic;

import java.util.List;

import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.WorldServer;
import btl.ballgame.server.game.WorldVisualizer;
import btl.ballgame.server.game.entities.breakable.BreakableEntity;
import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

public class EntityWreckingBall extends EntityDynamic {
	public static final float DEFAULT_SPEED = 5.0f; // units per tick
	public static final int DEFAULT_BALL_RADIUS = 32;
	
	private float speed = DEFAULT_SPEED;
	
	public EntityWreckingBall(int id, Location location) {
		super(id, location);
		setBallScale(1);
		WorldVisualizer.addVectorVisualizer(id);
	}
	
	public void setBallScale(float scale) {
		this.setBoundingBox(
			(int) (DEFAULT_BALL_RADIUS * scale), 
			(int) (DEFAULT_BALL_RADIUS * scale)
		);
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
		Vector2f pushWorld = new Vector2f(0, 0);

		if (getBoundingBox().minX < 0) { // ball touches left wall
			pushWorld.x = -getBoundingBox().minX;
			worldNormal = new Vector2f(1, 0); // e
			bouncedFromWorld = true;
		} else if (getBoundingBox().maxX > worldWidth) { // ball touches right wall
			pushWorld.x = getBoundingBox().maxX - worldWidth;
			worldNormal = new Vector2f(-1, 0); // normal of the left wall
			bouncedFromWorld = true;
		}
		
		// DEMO code, wont be used in prod
		if (getBoundingBox().minY < 0) {
			pushWorld.y = -getBoundingBox().minX;
			worldNormal = Math.abs(pushWorld.x) > Math.abs(pushWorld.y) 
				? new Vector2f(Math.signum(pushWorld.x), 0)
				: new Vector2f(0, 1)
			;
			bouncedFromWorld = true;
		} else if (getBoundingBox().maxY > worldHeight) {
			pushWorld.y = getBoundingBox().maxY - worldHeight;
			worldNormal = Math.abs(pushWorld.x) > Math.abs(pushWorld.y) 
				? new Vector2f(Math.signum(pushWorld.x), 0)
				: new Vector2f(0, -1)
			;
			bouncedFromWorld = true;
		}
		
		// if the ball interacted with the world, apply the force
		if (bouncedFromWorld) {
			setLocation(pos.clone().add(pushWorld));
			bounce(worldNormal);
		}
		
		// make the ball reacts to collisions by other entities
		List<WorldEntity> collided = queryCollisions();
		for (WorldEntity collider : collided) {
			if (!collider.isCollidable()) {
				continue;
			}
			
			// in arkanoid (breakout) clones, this sep. is essential to prevent
			// softlocks and less frustrating gameplay-wise. If the paddle acts as 
			// just another physics prop, it will softlock the game gradually 
			// if the ball points straight up
			if (collider instanceof EntityPaddle paddle) {
			    AABB paddleBox = paddle.getBoundingBox();
			    float paddleCenter = paddleBox.getCenterX();
			    if (getBoundingBox().getCenterY() > paddleBox.minY) {
			    	System.out.println("phase through");
			    	break;
			    }
			    
			    // offset from center, range [-1, 1]
			    // the more titled the ball is to the sides (upon contact), the steeper the refl angle gets
			    float relative = (x - paddleCenter) / (paddleBox.getWidth() / 2f);
//			    if (relative < -1 || relative > 1) {
//			    	setLocation(newPos.add(0, direction.y > 0 ? -15 : 15));
//			    	relative = getWorld().random.nextFloat(-.5f, .5f);
//			    }
			    relative = Math.max(-1f, Math.min(1f, relative)); // clamp
			    Vector2f newDir = Vector2f.fromTheta(Math.toRadians(90 - 75 * relative));
			    if (direction.y > 0) { // large Y -> lower, smaller == higher
			    	newDir.y *= -1; // if the ball is coming down, force it to fly up
			    }
			    setDirection(newDir.normalize());
			    break;
			}
			
			/** for normal physics prop, we use the {@link EntityWreckingBall#bounce(Vector2f)} */
			Vector2f overlap = getBoundingBox().getIntersectionDepth(collider.getBoundingBox());
			Vector2f normal; // the normal of the contact surface (everything is a axis-aligned)
			Vector2f push = Vector2f.ZERO; // correctional vector, prevent this entity from being stuck in another prop
			
			// overlap on the X axis
			if (Math.abs(overlap.getX()) < Math.abs(overlap.getY())) {
				normal = new Vector2f(Math.signum(overlap.getX()), 0);
				push.x = overlap.getX();
			} else {
				// overlap on the Y axis
				normal = new Vector2f(0, Math.signum(overlap.getY()));
				push.y = overlap.getY();
			}

			setLocation(getLocation().add(push)); // pushes the ball out of the prop first
			bounce(normal); // boing boing
			break;
		}
		
		for (WorldEntity collider : collided) {
			if (collider instanceof BreakableEntity ib) {
				ib.onHit(1);
			}
		}
		
		WorldVisualizer.updateVV(getId(), direction.clone().multiply(5));
	}
	
	private void setDirection(Vector2f lookVector) {
		setLocation(getLocation().clone().setDirection(lookVector));
	}
}
