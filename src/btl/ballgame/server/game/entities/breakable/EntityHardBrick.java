package btl.ballgame.server.game.entities.breakable;
import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.shared.libs.Location;

public class EntityHardBrick extends EntityBrick {
	public EntityHardBrick(int id, Location location) {
		super(id, location);
	}
	
	@Override
	public int getMaxHealth() {
		return 2 + (int) (Math.random() * 2); // 2-4 HP
	}

	@Override
	public void onObjectBroken(WorldEntity damager) {
		this.remove();
	}
}
