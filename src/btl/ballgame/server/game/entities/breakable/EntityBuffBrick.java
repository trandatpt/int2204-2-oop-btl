package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.shared.libs.Location;
import btl.ballgame.server.game.entities.dynamic.EntityBuff;

public class EntityBuffBrick extends EntityBrick {
	public EntityBuffBrick(int id, Location location) {
		super(id, location);
	}

	@Override
	public int getMaxHealth() {
		return 1;
	}

	// TODO: giup dat xu ly cai nay!
	@Override
	public void onObjectBroken() {
		Location loc = getLocation();
		EntityBuff buff = new EntityBuff(
			world.nextEntityId(),
			new Location(world, loc.getX(), loc.getY(), 0),
			null,
			null
		);
		world.addEntity(buff);
		this.remove();
	}
}
