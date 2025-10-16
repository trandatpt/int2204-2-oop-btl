package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.match.ArkanoidMatch.TeamColor;
import btl.ballgame.shared.libs.Location;

public class EntityPaddle extends EntityDynamic {
	private boolean lowerPaddle;
	
	private ArkaPlayer player;
	private TeamColor team;
	
	public EntityPaddle(ArkaPlayer p, int id, Location location) {
		super(id, location);
		this.player = p;
	}
	
	public void setLowerPaddle(boolean lowerPaddle) {
		this.lowerPaddle = lowerPaddle;
	}
	
	public boolean isLowerPaddle() {
		return lowerPaddle;
	}
	
	public void moveRight() {
		teleport(getLocation().add(20, 0));
	}
	
	public void moveLeft() {
		teleport(getLocation().add(-20, 0));
	}

	@Override
	public void tick() {
		
	}

	@Override
	public int getWidth() {
		return 36 * 2;
	}

	@Override
	public int getHeight() {
		return 36 / 2;
	}

}
