package btl.ballgame.server.game.entities.dynamic;

import java.util.List;

import btl.ballgame.server.game.WorldEntity;
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
		//WorldVisualizer.addVectorVisualizer(id);
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
		Location currentLoc = getLocation();
		Vector2f direction = currentLoc.getDirection().normalize();
		setLocation(currentLoc.clone().add(direction.multiply(speed)));
		
		// make the ball reacts to the world
		int worldWidth = world.getWidth();
		int worldHeight = world.getHeight();
		
		boolean bouncedFromWorld = false;
		Vector2f worldNormal = new Vector2f(0, 0);
		Vector2f pushWorld = new Vector2f(0, 0); // correctional vector

		if (getBoundingBox().minX < 0) { // ball touches left wall
			pushWorld.x = -getBoundingBox().minX;
			worldNormal = new Vector2f(1, 0); // e
			bouncedFromWorld = true;
		} else if (getBoundingBox().maxX > worldWidth) { // ball touches right wall
			pushWorld.x = getBoundingBox().maxX - worldWidth;
			worldNormal = new Vector2f(-1, 0); // normal of the left wall
			bouncedFromWorld = true;
		}
		
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
			setLocation(currentLoc.clone().add(pushWorld));
			bounce(worldNormal);
		}
		
		// make the ball reacts to collisions by other entities
		List<WorldEntity> collided = queryCollisions();
		for (WorldEntity collider : collided) {
			if (!collider.isCollidable()) {
				continue;
			}
			
			// compute the normals and correctional vector
			Vector2f overlap = getBoundingBox().getIntersectionDepth(collider.getBoundingBox());
			Vector2f normal; // the normal of the contact surface (everything is a axis-aligned)
			Vector2f push = Vector2f.ZERO; // correctional vector, prevent entity from being stuck in another
			
			if (Math.abs(overlap.getX()) < Math.abs(overlap.getY())) {
				// overlap on the X axis
				normal = new Vector2f(Math.signum(overlap.getX()), 0);
				push.x = overlap.getX();
			} else {
				// overlap on the Y axis
				normal = new Vector2f(0, Math.signum(overlap.getY()));
				push.y = overlap.getY();
			}
			
			// in arkanoid (breakout) clones, this sep. is essential to prevent
			// softlocks and less frustrating gameplay-wise. If the paddle acts as 
			// just another physics prop, it will softlock the game gradually 
			// if the ball points straight up
			if (collider instanceof EntityPaddle paddle) {
				System.out.println(normal);
				if (normal.x != 0) { // ball cannot bounce on the edge of the paddle
					setLocation(currentLoc.add(push));
					setDirection(new Vector2f(0, -1));
					break;
				}
				
			    AABB paddleBox = paddle.getBoundingBox();
			    float paddleCenter = paddleBox.getCenterX();
			    
			    // offset from center, range [-1, 1]
			    // the more titled the ball is to the sides (upon contact), the steeper the refl angle gets
			    float relative = (x - paddleCenter) / (paddleBox.getWidth() / 2f);
			    relative = Math.max(-1f, Math.min(1f, relative)); // clamp
			    Vector2f newDir = Vector2f.fromTheta(Math.toRadians(90 - 75 * relative));
			    if (direction.y > 0) { // large Y -> lower, smaller == higher
			    	newDir.y *= -1; // if the ball is coming down, force it to fly up
			    }
			    setDirection(newDir.normalize());
			    break;
			}
			
			/** for normal physics prop, we use the {@link EntityWreckingBall#bounce(Vector2f)} */
			setLocation(getLocation().add(push)); // pushes the ball out of the prop first
			bounce(normal); // boing boing
			break;
		}
		
		for (WorldEntity collider : collided) {
			if (collider instanceof BreakableEntity ib) {
				ib.damage(1);
			}
		}
		
		//WorldVisualizer.updateVV(getId(), direction.clone().multiply(5));
	}
	
	private void setDirection(Vector2f lookVector) {
		setLocation(getLocation().clone().setDirection(lookVector));
	}
}
