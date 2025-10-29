package btl.ballgame.server.game.entities.dynamic;

import java.util.List;
import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.entities.BreakableEntity;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

/**
 * Vien dan cua sung AK47(version 1)
 * - vien dan co ban chua co ly thuyet vat ly
 */
public class EntityAKBullet extends WorldEntity {

    private static final float INITIAL_VELOCITY = 8f; // v0
    private static final float SLOW_DOWN = 0.92f;	  // toc do giam dan khi va cham
    private static final float MIN_SPEED = 3f;		  // vmin
    private static final int MAX_BRICK_PIERCE = 10;   // so gach toi da xuyen qua duoc
    private static final int DAMAGE = 1;			  // -1hp cua gach

    private Vector2f velocity;
    private int pierceBrickCount = 0;
    private boolean removed = false;

	/**
	 * Bullet.
	 * 
	 * @param id
	 * @param location
	 */
    public EntityAKBullet(int id, Location location) {
        super(id, location);
        this.setBoundingBox(6, 6);
        Vector2f dir = location.getDirection().normalize();
        this.velocity = new Vector2f(dir).multiply(INITIAL_VELOCITY);
    }

    @Override
    protected void tick() {
        if (removed) return;
        Location location = this.getLocation();
        location.add(velocity);
        this.setLocation(location);

        List<WorldEntity> collisions = this.queryCollisions();
        for (WorldEntity entity : collisions) {
            if (entity instanceof BreakableEntity brick) {
                brick.damage(DAMAGE);
                pierceBrickCount++;
                velocity.multiply(SLOW_DOWN);

                if (velocity.length() < MIN_SPEED || pierceBrickCount >= MAX_BRICK_PIERCE) {
                    destroy();
                    return;
                }
            }
        }

        if (this.getWorld().isEntirelyOutOfWorld(this.getBoundingBox())) {
            destroy();
        }
    }

    private void destroy() {
        if (!removed) {
            removed = true;
            this.remove();
        }
    }
}
