package btl.ballgame.server.game.entities.breakable;
import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.shared.libs.Location;

public class EntityHardBrick extends EntityBrick {
	private int maxHealth;
	
	public EntityHardBrick(int id, Location location) {
		super(id, location);
		setTint(0x3d3d3d);
	}
	
	@Override
	public int getMaxHealth() {
		if (this.maxHealth <= 0) {
			this.maxHealth = 2 + (int) (Math.random() * 2); // 2-4 HP
		}
		return this.maxHealth;
	}
}
