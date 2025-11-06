package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.shared.libs.Location;

public class EntityHardBrick extends EntityBrick {
	private int maxHealth;
	
	public EntityHardBrick(int id, Location location) {
		super(id, location);
		setTint(0x575757);
	}
	
	@Override
	public int getMaxHealth() {
		if (this.maxHealth <= 0) {
			this.maxHealth = 3 + (int) (Math.random() * 3); // 3-6 HP
		}
		return this.maxHealth;
	}
}
