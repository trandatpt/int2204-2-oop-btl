package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Constants.ItemType;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.entities.IOwnableEntity;
import btl.ballgame.server.game.entities.dynamic.EntityFallingItem;

public class EntityItemBrick extends EntityBrick {
	public EntityItemBrick(int id, Location location) {
		super(id, location);
	}
	
	@Override
	public int getMaxHealth() {
		return 1;
	}

	@Override
	public void onObjectBroken(WorldEntity damager) {
		if (damager instanceof IOwnableEntity wb) {
			ArkaPlayer owner = wb.getOwner();
			Location loc = getLocation();
			EntityFallingItem buff = new EntityFallingItem(
				world.nextEntityId(),
				new Location(world, loc.getX(), loc.getY(), 0),
				owner.getCurrentGame().getTeamOf(owner).getTeamColor(),
				ItemType.values()[world.random.nextInt(2)]
			);
			buff.onPickup(e -> {
				System.out.println(e.getPlayer().getName() + " picked me up!");
			});
			world.runNextTick(() -> world.addEntity(buff));
		}
		super.onObjectBroken(damager);
	}
}
