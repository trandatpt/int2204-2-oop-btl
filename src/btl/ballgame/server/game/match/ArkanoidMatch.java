package btl.ballgame.server.game.match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.WorldServer;
import btl.ballgame.server.game.entities.dynamic.EntityPaddle;
import btl.ballgame.server.game.entities.dynamic.EntityWreckingBall;
import btl.ballgame.shared.libs.Constants.*;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

public class ArkanoidMatch {
	private ArkanoidMode gameMode;
	private WorldServer world;
	
	// team/players related mappings
	private Map<TeamColor, List<ArkaPlayer>> players = new HashMap<>();
	private Map<ArkaPlayer, TeamColor> teamMap = new HashMap<>();
	
	// paddle ownership
	Map<ArkaPlayer, EntityPaddle> paddlesMap = new HashMap<>();
	
	public ArkanoidMatch(ArkanoidMode mode) {
		this.gameMode = mode;
		this.world = new WorldServer(this, 600, 800);
	}
	
	public void assignPlayerTo(TeamColor team, ArkaPlayer player) {
		players.computeIfAbsent(team, k -> new ArrayList<>(2)).add(player);
		teamMap.put(player, team);
		player.joinGame(this);
	}
	
	public TeamColor getTeamOf(ArkaPlayer player) {
		return teamMap.get(player);
	}
	
	public Collection<ArkaPlayer> getPlayers() {
		return teamMap.keySet();
	}
	
	public ArkanoidMode getGameMode() {
		return this.gameMode;
	}
	
	private void spawnPaddleFor(ArkaPlayer owner, boolean isLowerPaddle, int y) {
		EntityPaddle paddle = new EntityPaddle(owner, world.nextEntityId(), 
			new Location(world, world.getWidth() / 2, y, 0)
		);
		paddle.setLowerPaddle(isLowerPaddle);
		world.addEntity(paddle);
		paddlesMap.put(owner, paddle);
	}
	
	public EntityPaddle paddleOf(ArkaPlayer player) {
		return paddlesMap.get(player);
	}
	
	static final int PADDLE_SPACING = 40;
	static final int BASE_MARGIN = 60;
	
	public void prepareMatch() {
		// spawn the bricks
		// NOTE: THẰNG ĐẠT CODE THUẬT TOÁN SINH GẠCH RA
		
		// spawn the static geometries
		// NOTE: THẰNG ĐẠT CODE THUẬT TOÁN SINH KHỐI CỨNG (WORLD GEOMETRIES) RA		
		
		// spawn paddles based on team
		for (TeamColor team : players.keySet()) {
			List<ArkaPlayer> teamPlayers = players.get(team);
			
			boolean isBottomTeam = (team == TeamColor.RED);
			int baseY = isBottomTeam ? world.getHeight() - BASE_MARGIN : BASE_MARGIN;
			
			for (int i = 0; i < teamPlayers.size(); i++) {
	            // calculate the Y-position offset for this paddle
	            // RED team paddles are stacked upward (-1),
	            // BLUE team paddles are stacked downward (+1)
				this.spawnPaddleFor(teamPlayers.get(i), isBottomTeam,
					baseY + (isBottomTeam ? -1 : 1) * (i * PADDLE_SPACING)
				);
			}
			
			// the ball falls into the respective paddle
			// the ball will fall down (+1) if the team is the upper one
			// and fly up (-1) if lower 
			Vector2f initial = isBottomTeam ? new Vector2f(0, -1) : new Vector2f(0, 1);
			EntityWreckingBall ball = new EntityWreckingBall(world.nextEntityId(), 
				new Location(world, 
					world.getWidth() / 2, // middle the screen
					(isBottomTeam ? -1 : 1) * 120, // a little higher than the base
					initial // initial flying vector
				)
			);
			world.addEntity(ball);
		}
	}
	
	// events fired by subclasses
	public void onBallFallIntoVoid(EntityWreckingBall ball, VoidSide side) {
		ball.remove();
	}
}
