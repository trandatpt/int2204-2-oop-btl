package btl.ballgame.server.game.match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.WorldServer;
import btl.ballgame.server.game.entities.dynamic.EntityPaddle;
import btl.ballgame.server.game.entities.dynamic.EntityWreckingBall;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.*;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

// TODO TODO TODO WILL FINISH TMR!!!
public class ArkanoidMatch {
	private ArkanoidMode gameMode;
	private WorldServer world;
	
	// team/players related mappings
	private Map<TeamColor, TeamInfo> teams = new HashMap<>();
	private Map<ArkaPlayer, TeamColor> teamMap = new HashMap<>();
	
	// paddle ownership
	private Map<ArkaPlayer, EntityPaddle> paddlesMap = new HashMap<>();
	
	// match state
	private boolean matchStarted = false;
	private TeamColor winner = null;
	
	public ArkanoidMatch(ArkanoidMode mode) {
		this.gameMode = mode;
		this.world = new WorldServer(this, 600, 800);
	}
	
	public void assignTeam(TeamColor team, List<ArkaPlayer> players) {
		teams.put(team, new TeamInfo(players));
		players.forEach(p -> {
			teamMap.put(p, team);
			p.joinGame(this);
		});
	}
	
	public TeamInfo getTeamOf(ArkaPlayer player) {
		return teams.get(teamMap.get(player));
	}
	
	public Collection<ArkaPlayer> getPlayers() {
		return teamMap.keySet();
	}
	
	public ArkanoidMode getGameMode() {
		return this.gameMode;
	}
	
	private void spawnPaddleFor(ArkaPlayer owner, TeamColor team, boolean isLowerPaddle, int y) {
		EntityPaddle paddle = new EntityPaddle(world.nextEntityId(), 
			new Location(world, world.getWidth() / 2, y, 0),
			owner, team // paddle metadata
		);
		
		paddle.setLowerPaddle(isLowerPaddle);
		paddle.setHealth(getTeamOf(owner).getHealth(owner));
		
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
		for (TeamColor team : teams.keySet()) {
			List<ArkaPlayer> teamPlayers = teams.get(team).getPlayers();
			
			boolean isBottomTeam = (team == TeamColor.RED);
			int baseY = isBottomTeam ? world.getHeight() - BASE_MARGIN : BASE_MARGIN;
			
			for (int i = 0; i < teamPlayers.size(); i++) {
	            // calculate the Y-position offset for this paddle
	            // RED team paddles are stacked upward (-1),
	            // BLUE team paddles are stacked downward (+1)
				this.spawnPaddleFor(teamPlayers.get(i), team, isBottomTeam,
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
			ball.setPrimaryBall(true);
			world.addEntity(ball);
		}
	}
	
	// events fired by subclasses/utilities class
	public void onBallFallIntoVoid(EntityWreckingBall ball, VoidSide side) {
		ball.remove();
		System.out.println("the ball is primary? " + ball.isPrimaryBall());
	}
	
	// this event is called when a player of this match leaves
	// the server
	public void onPlayerLeft(ArkaPlayer player) {
		if (getTeamOf(player) == null) return;
	}
	
	public class TeamInfo {
		private TeamColor teamColor;
		private LinkedHashMap<ArkaPlayer, Integer> healthPoints = new LinkedHashMap<>();
		private int teamLivesLeft;
		private int teamScore;
		
		private TeamInfo(List<ArkaPlayer> teamMembers) {
			teamMembers.forEach(teammate -> {
				healthPoints.put(teammate, Constants.PADDLE_MAX_HEALTH);
			});
			this.teamLivesLeft = 3;
			this.teamScore = 0;
		}
		
		public List<ArkaPlayer> getPlayers() {
			return new ArrayList<>(healthPoints.keySet());
		}
		
		public int getHealth(ArkaPlayer player) {
			if (!healthPoints.containsKey(player)) {
				throw new IllegalArgumentException("This player does not belong to this team");
			}
			return healthPoints.get(player);
		}
		
		public void setHealth(ArkaPlayer player, int health) {
			if (!healthPoints.containsKey(player)) {
				throw new IllegalArgumentException("This player does not belong to this team");
			}
			
			// clamp health range
			int newHealth = Math.max(0, Math.min(Constants.PADDLE_MAX_HEALTH, health));
			this.healthPoints.put(player, newHealth); //
			
			// update the value inside the entity for rendering
			EntityPaddle paddle = paddleOf(player);
			paddle.setHealth(newHealth);
			paddle.updateMetadata();
		}
		
		public void damage(ArkaPlayer player, int damage) {
			setHealth(player, getHealth(player) - damage);
		}
	}
}
