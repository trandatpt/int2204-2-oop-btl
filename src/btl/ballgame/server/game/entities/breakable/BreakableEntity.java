package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.server.game.entities.dynamic.EntityDynamic;
import btl.ballgame.shared.libs.Location;

/**
 * Represents a dynamic entity in the game world that can take damage
 * and eventually break when its health reaches zero.
 * 
 * This abstract class defines the basic behavior for any "breakable" entity.
 * Subclasses must define their maximum health and what happens when the entity breaks.
 */
public abstract class BreakableEntity extends EntityDynamic {
	// the current remaining health of this entity
	private int remainingHealth;
	
	/**
	 * Constructs a new BreakableEntity with the given ID and location. Initializes
	 * the entity's health to its maximum value.
	 *
	 * @param id       the unique identifier for this entity
	 * @param location the initial location of this entity
	 */
	public BreakableEntity(int id, Location location) {
		super(id, location);
		this.setHealth(getMaxHealth());
	}
	
	/**
	 * Applies damage to this entity. If health drops to zero or below, the entity
	 * is considered broken.
	 *
	 * @param damage the amount of damage to apply
	 */
	public void damage(int damage) {
		this.setHealth(this.getHealth() - damage);
		if (this.getHealth() <= 0) {
			onObjectBroken();
		}
	}
	
    /**
     * Sets the entity's health
     *
     * @param health the new health value
     */
	public void setHealth(int health) {
		this.remainingHealth = Math.min(getMaxHealth(), Math.max(0, health));
		this.dataWatcher.watch(0xA, this.remainingHealth);
	}
	
	public final int getHealth() {
		return remainingHealth;
	}
	
	/**
	 * Called when the entity's health reaches zero. 
	 * Subclasses must define what happens when the object breaks.
	 */
	abstract void onObjectBroken();

	/**
	 * Returns the maximum health for this entity type. 
	 * Subclasses must define this value.
	 *
	 * @return the maximum health value
	 */
	abstract int getMaxHealth();
}
