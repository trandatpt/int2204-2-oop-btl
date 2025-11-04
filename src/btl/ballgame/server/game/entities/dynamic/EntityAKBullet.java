package btl.ballgame.server.game.entities.dynamic;

import java.util.List;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.entities.BreakableEntity;
import btl.ballgame.server.game.entities.IOwnableEntity;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

/**
 * Vien dan cua sung AK47(version 2)
 * - vien dan co luc can khong khi, mat nang luong khi va cham, dao dong nhe khi va cham
 */
public class EntityAKBullet extends WorldEntity implements IOwnableEntity {

    private static final float INITIAL_VELOCITY = 20f; // v0
	private static final float AIR_RESISTANCE = 0.01f; // luc can khong khi (1%)
    private static final float ENERGY_LOSS = 0.8f;	  // mat nang luong khi va cham
    private static final float MIN_SPEED = 2.5f;		  // vmin
    private static final int MAX_BRICK_PIERCE = 2;   // so gach toi da xuyen qua duoc
    private static final int DAMAGE = 1;			  // -1hp cua gach

    private Vector2f velocity;
    private int pierceBrickCount = 0;
    
    private ArkaPlayer owner;

	/**
	 * Bullet.
	 * 
	 * @param id
	 * @param location
	 */
    public EntityAKBullet(int id, Location location) {
        super(id, location);
        this.setBoundingBox(10, 6);
        Vector2f dir = location.getDirection().normalize();
        this.velocity = new Vector2f(dir).multiply(INITIAL_VELOCITY);
    }
    
    public void setOwner(ArkaPlayer owner) {
		this.owner = owner;
	}
    
    @Override
    public ArkaPlayer getOwner() {
		return owner;
	}

    @Override
    protected void tick() {
		velocity.multiply(1 - AIR_RESISTANCE); // luc can khong khi

        Location location = this.getLocation();
        location.add(velocity);
        this.setLocation(location);

        List<WorldEntity> collisions = this.queryCollisions();
        for (WorldEntity entity : collisions) {
            if (entity instanceof BreakableEntity brick) {
                brick.damage(this, DAMAGE);
                pierceBrickCount++;

                velocity.multiply((float)Math.sqrt(ENERGY_LOSS)); // E = 1/2 mv^2 => v ~ sqrt(E)

				/*
				 * Hieu ung rung khi va cham voi gach
				 * Dan se lech 1 chut sang trai/phai tren/duoi, giong dao dong nhe
				 */
				velocity.setX(velocity.getX() + (float)(Math.random() - 0.5) * 0.4f);
				velocity.setY(velocity.getY() + (float)(Math.random() - 0.5) * 0.4f);

                if (velocity.length() < MIN_SPEED || pierceBrickCount >= MAX_BRICK_PIERCE) {
                    this.remove();
                    return;
                }
            }
        }

        if (this.getWorld().isEntirelyOutOfWorld(this.getBoundingBox())) {
        	this.remove();
        	return;
        }
    }
    
    @Override
    public int getWidth() {
    	return 6;
    }
    
    @Override
    public int getHeight() {
    	return 24;
    }
}
