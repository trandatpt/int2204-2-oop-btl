package btl.ballgame.server.game.buffs;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.match.ArkanoidMatch;
import btl.ballgame.shared.libs.Constants.EffectType;

/**
 * Represents a status effect or buff applied to a player in a match.
 */
public abstract class BaseEffect {
	private long startTime;
	private ArkaPlayer target;
	
	/**
	 * Constructs a new effect targeting a specific player.
	 *
	 * @param target the player this effect is applied to
	 */
	public BaseEffect(ArkaPlayer target) {
		this.target = target;
		if (target.getCurrentGame() == null) {
			throw new IllegalStateException("The player must be in a match!");
		}
	}
	
	/**
	 * Activates the effect.
	 */
	public final void activate() {
		this.startTime = System.currentTimeMillis();
		onEffectActivate();
	}
	
	/**
	 * Removes the effect from the player.
	 */
	public final void remove() {
		onEffectDeactivate();
	}
	
	/**
	 * Called periodically on each game tick to update the effect.
	 * Will remove itself once time runs out
	 */
	public final void tick() {
		ArkanoidMatch match = target.getCurrentGame();
		// if the effect expired, remove itself
		if (match != null && isExpired()) {
			match.removeEffect(target, getType());
			return;
		}
		onTick();
	}
	
	/**
	 * Gets the player this effect is applied to.
	 *
	 * @return the target player
	 */
	public ArkaPlayer getTarget() {
		return target;
	}
	
    /**
     * @return true if the effect is expired, false otherwise
     */
	public final boolean isExpired() {
		if (getDuration() < 0) { // Instant effects, wont call onTick()
			return true;
		}
		return System.currentTimeMillis() - startTime >= getDuration();
	}
	
    /**
     * Called when the effect is removed or expires.
     */
	public void onEffectDeactivate() {};
	
	/**
	 * Called on each tick while the effect is active. Subclasses must implement
	 * this to handle per-tick behavior.
	 */
	public void onTick() {};

	/**
	 * Called when the effect is first activated. Subclasses must implement this to
	 * apply the effect's behavior.
	 */
	public abstract void onEffectActivate();
	
	public abstract int getDuration();
	public abstract EffectType getType();
	public abstract String getName();
}
