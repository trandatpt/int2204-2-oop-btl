package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.shared.libs.Location;

public class EntityExplosiveBrick extends BreakableEntity {

	public EntityExplosiveBrick(int id, Location location) {
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
		return 1;
	}

	@Override
	void onObjectBroken() {
		double radius = 50; // nổ trong bán kính 50 pixel
        Location pos = getLocation();
        for (var e : world.getEntities()) {
            if (e instanceof BreakableEntity && e != this) {
                Location other = e.getLocation();
                double dx = other.getX() - pos.getX();
                double dy = other.getY() - pos.getY();
                if (Math.abs(dx) <= radius && Math.abs(dy) <= radius) {
                    ((BreakableEntity)e).damage(999);
                }
            }
        }
        this.remove();
	}
}
