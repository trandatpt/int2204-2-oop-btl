package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.entities.ControllableEntity;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Constants.TeamColor;

public class EntityPaddle extends ControllableEntity {
	private boolean lowerPaddle;
	private ArkaPlayer player;
	private TeamColor team;
	
	public EntityPaddle(
		int id, Location location, // base info
		ArkaPlayer p, TeamColor team // extras
	) {
		super(id, location);
		this.player = p;
		this.team = team;
		setBoundingBox(36 * 2, 18);
	}
	
	public TeamColor getTeam() {
		return team;
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
			player.getUniqueId().getMostSignificantBits()
		);
	}
	
	@Override
	protected void tick() {}
}
