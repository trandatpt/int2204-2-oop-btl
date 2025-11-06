package btl.ballgame.server.game.entities.breakable;

import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Constants.ItemType;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.entities.IOwnableEntity;
import btl.ballgame.server.game.entities.dynamic.EntityFallingItem;
import btl.ballgame.server.game.match.ArkanoidMatch;

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
		if (damager instanceof IOwnableEntity wb && wb.getOwner() != null) {
			ArkaPlayer owner = wb.getOwner();
			Location loc = getLocation();
			ArkanoidMatch match = owner.getCurrentGame();
			if (match == null) return;
			
			EntityFallingItem buff = new EntityFallingItem(
				world.nextEntityId(),
				new Location(world, loc.getX(), loc.getY(), 0),
				match.getTeamOf(owner).getTeamColor(),
				ItemType.values()[world.random.nextInt(3)]
			);
			buff.onPickup(e -> {
				match.onItemCollected(buff, owner);
			});
			world.runNextTick(() -> world.addEntity(buff));
		}
		super.onObjectBroken(damager);
	}
}
