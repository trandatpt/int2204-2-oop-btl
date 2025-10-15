package btl.ballgame.server.game.entities.dynamic;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.match.ArkanoidMatch.TeamColor;
import btl.ballgame.shared.libs.Location;

public class EntityPaddle extends EntityDynamic {
	private ArkaPlayer player;
	private TeamColor team;
	
	public EntityPaddle(ArkaPlayer p, int id, Location location) {
		super(id, location);
		this.player = p;
	}
	
	public void moveRight() {
		
	}
	
	public void moveLeft() {
		
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
