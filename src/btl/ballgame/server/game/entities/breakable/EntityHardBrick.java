package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.shared.libs.Location;

public class EntityHardBrick extends BreakableEntity {

	public EntityHardBrick(int id, Location location) {
		super(id, location);
	}

	@Override
	protected void tick() {
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
	public int getMaxHealth() {
        // từ 2 đến 4 máu, cần 2-4 lần va chạm 
		return 2 + (int) (Math.random() * 3); 
	}

	@Override
	void onObjectBroken() {
		this.remove();
	}

}
