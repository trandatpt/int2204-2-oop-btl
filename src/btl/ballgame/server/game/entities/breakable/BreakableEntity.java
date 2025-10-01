package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.server.game.entities.dynamic.EntityDynamic;
import btl.ballgame.shared.libs.Location;

public abstract class BreakableEntity extends EntityDynamic {
	private int hitLeft = hitsTakeToBreak();
	
	public BreakableEntity(int id, Location location) {
		super(id, location);
	}
	
	public void onHit(int hitAmount) {
		hitLeft -= hitAmount;
		if (hitLeft <= 0) onObjectBroken();
	}
	
	public int getHitLeft() {
		return hitLeft;
	}
	
	abstract void onObjectBroken();
	abstract int hitsTakeToBreak();
}
