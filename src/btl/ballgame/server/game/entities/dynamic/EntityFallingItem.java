package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.shared.libs.Constants.ItemType;
import btl.ballgame.shared.libs.Constants.TeamColor;

import java.util.function.Consumer;

import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

public class EntityFallingItem extends WorldEntity {
	final int velocity = 4;
	
	private TeamColor receiver;
	private Consumer<EntityPaddle> onPickUp;
	private ItemType itemType;
	
	public EntityFallingItem(int id, Location location, TeamColor teamColor, ItemType itemType) {
		super(id, location);
		this.setDirection(teamColor.equals(TeamColor.RED)
			? new Vector2f(0, 1)
			: new Vector2f(0, -1)
		);
		this.receiver = teamColor;
		this.itemType = itemType;
	}
	
	public void onPickup(Consumer<EntityPaddle> onPickUp) {
		this.onPickUp = onPickUp;
	}
	
	/**
	 * Updates the facing direction of the falling item.
	 *
	 * @param lookVector New normalized direction vector.
	 */
	private void setDirection(Vector2f lookVector) {
		setLocation(getLocation().clone().setDirection(lookVector));
	}

	@Override
	public void onSpawn() {
		this.dataWatcher.watch(Constants.ITEM_TYPE_META, itemType.ordinal());
		this.dataWatcher.watch(Constants.RENDER_UPSIDEDOWN_META, receiver == TeamColor.BLUE);
	}

	@Override
	protected void tick() {
        if (this.getWorld().isEntirelyOutOfWorld(getBoundingBox())) {
            this.remove();
            return;
        }
		
		var collided = queryCollisions();
		for (WorldEntity we : collided) {
			if (we instanceof EntityPaddle ep) {
				if (this.onPickUp != null) this.onPickUp.accept(ep);
				this.remove();
				return;
			}
		}
        
        // move along the path
		Location currentLoc = getLocation();
		Vector2f direction = currentLoc.getDirection().normalize();
		setLocation(currentLoc.clone().add(direction.multiply(velocity)));
	}
	
	@Override
	public int getHeight() {
		return 24;
	}
	
	@Override
	public int getWidth() {
		return 24;
	}
}
