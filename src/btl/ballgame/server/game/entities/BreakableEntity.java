package btl.ballgame.server.game.entities;

import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Utils;

/**
 * Represents a dynamic/static entity in the game world that can take damage
 * and eventually break when its health reaches zero.
 * 
 * This abstract class defines the basic behavior for any "breakable" entity.
 * Subclasses must define their maximum health and what happens when the entity breaks.
 */
public abstract class BreakableEntity extends WorldEntity {
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
		this.remainingHealth = Utils.clamp(health, 0, getMaxHealth());
		this.dataWatcher.watch(Constants.HP_META_KEY, this.remainingHealth);
		this.updateMetadata();
	}
	
	public final int getHealth() {
		return remainingHealth;
	}
	
	/**
	 * Called when the entity's health reaches zero. 
	 * Subclasses must define what happens when the object breaks.
	 */
	public abstract void onObjectBroken();

	/**
	 * Returns the maximum health for this entity type. 
	 * Subclasses must define this value.
	 *
	 * @return the maximum health value
	 */
	public abstract int getMaxHealth();
}
