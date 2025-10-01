package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.shared.libs.Location;

public class EntityBrick extends BreakableEntity {

	public EntityBrick(int id, Location location) {
		super(id, location);
	}

	@Override
	public void tick() {
	}

	@Override
	public int getWidth() {
		return 48;
	}

	@Override
	public int getHeight() {
		return 18;
	}
	
	@Override
	public int hitsTakeToBreak() {
		return 1;
	}

	@Override
	void onObjectBroken() {
		this.remove();		
	}

}
