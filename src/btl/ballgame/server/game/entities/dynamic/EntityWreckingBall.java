package btl.ballgame.server.game.entities.dynamic;

import java.util.List;

import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.entities.BreakableEntity;
import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.*;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

/**
 * Represents the Wrecking Ball entity in the Arkanoid world.
 * <p>
 * Handles ball physics, including movement, bouncing against walls, collision
 * response with paddles and breakable blocks, and out-of-bounds (void)
 * handling.
 */
public class EntityWreckingBall extends WorldEntity {
	/** Default movement speed (units per sec). */
	public static final float DEFAULT_SPEED = 320.0f;

	/** Default ball radius (in pixels/units). */
	public static final int DEFAULT_BALL_RADIUS = 32;

	/** Current movement speed. */
	private float speed;
	
	/** Game-dependent flags. */
	private TeamColor temporaryOwner = null;
	private boolean secondaryBall = true;
	
	/**
	 * Constructs a new Wrecking Ball entity at the specified location.
	 *
	 * @param id       Entity ID.
	 * @param location Initial world location.
	 */
	public EntityWreckingBall(int id, Location location) {
		super(id, location);
		this.setBallScale(1);
		this.setSpeed(DEFAULT_SPEED);
	}
	
	public void setPrimaryBall(boolean prime) {
		this.secondaryBall = !prime;
	}
	
	public boolean isPrimaryBall() {
		return !secondaryBall;
	}
	
	public void setTempOwner(TeamColor temporaryOwner) {
		this.temporaryOwner = temporaryOwner;
	}
	
	public TeamColor getTempOwner() {
		return temporaryOwner;
	}
	
	/**
	 * Sets the ball’s size scale.
	 *
	 * @param scale Scale factor applied to {@link #DEFAULT_BALL_RADIUS}.
	 */
	public void setBallScale(float scale) {
		this.setBoundingBox(
			(int) (DEFAULT_BALL_RADIUS * scale), 
			(int) (DEFAULT_BALL_RADIUS * scale)
		);
	}

	/**
	 * Sets the current movement speed.
	 *
	 * @param speed Speed in units per tick.
	 */
	public void setSpeed(float speed) {
		this.speed = speed / Constants.TICKS_PER_SECOND;
	}
	
	/**
	 * Updates the facing direction of the ball.
	 *
	 * @param lookVector New normalized direction vector.
	 */
	private void setDirection(Vector2f lookVector) {
		setLocation(getLocation().clone().setDirection(lookVector));
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
	public void onSpawn() {
		this.dataWatcher.watch(Constants.MISC_META_KEY, this.isPrimaryBall());
	}
	
	@Override
	protected void tick() {
		// move the ball forward
		Location currentLoc = getLocation();
		Vector2f direction = currentLoc.getDirection().normalize();
		setLocation(currentLoc.clone().add(direction.multiply(speed)));
		
		// make the ball reacts to the world
		int worldWidth = world.getWidth();
		int worldHeight = world.getHeight();
		
		boolean bouncedFromWorld = false;
		Vector2f worldNormal = new Vector2f(0, 0);
		Vector2f pushWorld = new Vector2f(0, 0); // correctional vector
		
		// left wall
		if (getBoundingBox().minX < 0) {
			pushWorld.x = -getBoundingBox().minX;
			worldNormal = new Vector2f(1, 0);
			bouncedFromWorld = true;
		} 
		// right wall
		else if (getBoundingBox().maxX > worldWidth) {
			pushWorld.x = getBoundingBox().maxX - worldWidth;
			worldNormal = new Vector2f(-1, 0);
			bouncedFromWorld = true;
		}
		
		// ceiling handle
		if (getBoundingBox().minY < 0) {
			// if the world has a ceiling, bounces down
			if (world.hasCeiling()) {
				pushWorld.y = -getBoundingBox().minY;
				worldNormal = new Vector2f(0, 1);
				bouncedFromWorld = true;
			} else {
				// if the world has no ceiling, the user just lost a ball
				this.world.getHandle().onBallFallIntoVoid(this, VoidSide.CEILING);
			}
		} 
		// floor handle (there's no floor, so the ball is lost)
		else if (getBoundingBox().maxY > worldHeight) {
			this.world.getHandle().onBallFallIntoVoid(this, VoidSide.FLOOR);
		}
		
		// apply bounce response for world collisions
		if (bouncedFromWorld) {
			setLocation(currentLoc.clone().add(pushWorld));
			bounce(worldNormal);
		}
		
		// check collisions with other entities
		List<WorldEntity> collided = queryCollisions();
		for (WorldEntity collider : collided) {
			if (!collider.isCollidable()) {
				continue;
			}
			
			// compute the normals and correctional vector
			Vector2f overlap = getBoundingBox().getIntersectionDepth(collider.getBoundingBox());
			Vector2f normal; // the normal of the contact surface (everything is a axis-aligned)
			Vector2f push = Vector2f.ZERO; // correctional vector, prevent entity from being stuck in another
			
			// overlap on the X axis (horizontal)
			if (Math.abs(overlap.getX()) < Math.abs(overlap.getY())) {
				normal = new Vector2f(Math.signum(overlap.getX()), 0);
				push.x = overlap.getX();
			}
			// overlap on the Y axis (vertical)
			else {
				normal = new Vector2f(0, Math.signum(overlap.getY()));
				push.y = overlap.getY();
			}
			
			// in arkanoid (breakout) clones, this sep. is essential to prevent
			// softlocks and less frustrating gameplay-wise. If the paddle acts as 
			// just another physics prop, it will softlock the game gradually 
			// if the ball points straight up
			if (collider instanceof EntityPaddle paddle) {
				this.setTempOwner(paddle.getTeam());
				if (normal.x != 0) { // SPECIAL CASE: ball bounces on the edge
					setLocation(currentLoc.add(push));
					setDirection(paddle.isLowerPaddle() ? new Vector2f(0, -1) : new Vector2f(0, 1));
					break;
				}
				
				AABB paddleBox = paddle.getBoundingBox();
				float paddleCenter = paddleBox.getCenterX();

				// offset from center, range [-1, 1]
				// the more titled the ball is to the sides (upon contact), the steeper the refl
				// angle gets
				float relative = (x - paddleCenter) / (paddleBox.getWidth() / 2f);
				relative = Math.max(-1f, Math.min(1f, relative)); // clamp
				Vector2f newDir = Vector2f.fromTheta(Math.toRadians(90 - 75 * relative));
				if (direction.y > 0) {
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
		
		// apply damage to breakable entities
		for (WorldEntity collider : collided) {
			if (collider instanceof BreakableEntity ib) {
				ib.damage(1);
			}
		}
	}
}
