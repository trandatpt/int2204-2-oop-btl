package btl.ballgame.server.game.entities.breakable;
import btl.ballgame.shared.libs.Location;

public class EntityHardBrick extends EntityBrick {
	public EntityHardBrick(int id, Location location) {
		super(id, location);
	}
	
	@Override
	public int getMaxHealth() {
        // từ 2 đến 4 máu, cần 2-4 lần va chạm 
		return 2 + (int) (Math.random() * 2); 
	}

	@Override
	public void onObjectBroken() {
		this.remove();
	}
}
