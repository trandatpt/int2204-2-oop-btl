package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.TeamColor;
import btl.ballgame.shared.libs.Location;

public class EntityPaddle extends EntityDynamic {
	private boolean lowerPaddle;
	
	private ArkaPlayer player;
	private TeamColor team;
	
	public EntityPaddle(ArkaPlayer p, int id, Location location) {
		super(id, location);
		this.player = p;
		setBoundingBox(36 * 2, 18);
	}
	
	public void setLowerPaddle(boolean lowerPaddle) {
		this.lowerPaddle = lowerPaddle;
	}
	
	public boolean isLowerPaddle() {
		return lowerPaddle;
	}
	
	private void move(int relX) {
		if (getBoundingBox().minX + relX <= 0 
		 || getBoundingBox().maxX + relX > world.getWidth()) {
			return;
		}
		teleport(getLocation().add(relX, 0));
	}
	
	public void moveRight() {
		this.move(20);
	}
	
	public void moveLeft() {
		this.move(-20);
	}
	
	@Override
	public void onSpawn() {
		if (player == null) return;
		this.dataWatcher.watch(
			Constants.PADDLE_OWNER_MKEY,
			player.getUniqueId().toString()
		);
	}
	
	@Override
	public void tick() {}
}
