package btl.ballgame.server.game.match;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import btl.ballgame.protocol.packets.out.PacketPlayOutMatchJoin;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutWorldInit;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata.PlayerEntry;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata.TeamEntry;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.WorldServer;
import btl.ballgame.server.game.entities.breakable.EntityBrick;
import btl.ballgame.server.game.entities.dynamic.*;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.*;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

/**
 * Represents a single Arkanoid match. Supports Classic single-player mode and
 * PvP arena mode. Tracks rounds, scores, team states, and match phases.
 */
public class ArkanoidMatch {
	private final ArkanoidMode gameMode;
	private final WorldServer world;
	private final UUID matchId;
	
	private boolean matchStarted = false;
	private MatchPhase currentPhase = MatchPhase.MATCH_IDLING;
	private TeamColor winner = null;
	
	private int roundIndex = 1;

	private final Map<TeamColor, TeamInfo> teams = new HashMap<>();
	private final Map<ArkaPlayer, TeamColor> teamMap = new HashMap<>();
	private final Map<ArkaPlayer, EntityPaddle> paddlesMap = new HashMap<>();
	
	static final int PADDLE_SPACING = 40;
	static final int BASE_MARGIN = 60;

	/**
	 * Creates a new ArkanoidMatch instance.
	 * 
	 * @param mode The game mode
	 */
	public ArkanoidMatch(ArkanoidMode mode) {
		this.gameMode = mode;
		this.world = new WorldServer(this, 600, 800);
		this.matchId = UUID.randomUUID();
		
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			if (matchStarted && currentPhase != MatchPhase.CONCLUDED) {
				this.world.tick();
				this.onMatchTick();
			}
		}, 0, ArkanoidServer.MS_PER_TICK, TimeUnit.MILLISECONDS);
	}
	
	public void start() {
		Map<UUID, String> nameMatch = new HashMap<>();
		getPlayers().forEach(p -> {
			nameMatch.put(p.getUniqueId(), p.getName());
		});
		
		getPlayers().forEach(player -> {
			player.playerConnection.sendPacket(new PacketPlayOutMatchJoin(matchId, gameMode, nameMatch));
		});
		this.syncMatchStateWithClients();
		getPlayers().forEach(player -> {
			player.playerConnection.sendPacket(new PacketPlayOutWorldInit(world));
		});
		this.prepareMatch();
		this.matchStarted = true;
		changePhase(MatchPhase.MATCH_ACTIVE);
	}

	/**
	 * Prepares the match based on the current game mode. Spawns paddles, balls, and
	 * bricks as required.
	 */
	public void prepareMatch() {
		spawnPaddles();
	}
	
	// this function works regardless of mode due to the way it was implemented
	private void spawnPaddles() {
		// spawn paddles based on team
		for (TeamColor team : teams.keySet()) {
			List<ArkaPlayer> teamPlayers = teams.get(team).getPlayers();
			
			boolean isBottomTeam = (team == TeamColor.RED);
			int baseY = isBottomTeam ? world.getHeight() - BASE_MARGIN : BASE_MARGIN;
			
			for (int i = 0; i < teamPlayers.size(); i++) {
				// calculate the Y-position offset for this paddle
				// RED team paddles are stacked upward (-1),
				// BLUE team paddles are stacked downward (+1)
				this.spawnPaddleFor(team, teamPlayers.get(i),
					baseY + (isBottomTeam ? -1 : 1) * (i * PADDLE_SPACING)
				);
			}
			
			// the ball falls into the respective paddle
			// the ball will fall down (+1) if the team is the upper one
			// and fly up (-1) if lower
			Vector2f initial = isBottomTeam ? new Vector2f(0, 1) : new Vector2f(0, -1);
			EntityWreckingBall ball = new EntityWreckingBall(world.nextEntityId(),
				new Location(world, world.getWidth() / 2, // middle the screen
					world.getWidth() / 2, // a little higher than the base
					initial // initial flying vector
				)
			);
			ball.setPrimaryBall(true);
			world.addEntity(ball);
		}
	}
	
	/**
	 * Called when a round ends. Updates FT scores and resets teams for the next
	 * round.
	 * 
	 * @param roundWinner The team that won the round, can be null for tie.
	 */
	public void onRoundEnd(TeamColor roundWinner) {
		if (roundWinner != null) {
			teams.get(roundWinner).addFTScore(1);
		}
		roundIndex++;
		resetForNextRound();
		changePhase(MatchPhase.MATCH_IDLING);
		prepareMatch();
		syncMatchStateWithClients();
	}

	/**
	 * Resets the world and teams for a new round.
	 */
	private void resetForNextRound() {
		for (TeamInfo team : teams.values()) {
			team.resetForNextRound();
		}
		world.getEntities().forEach(WorldEntity::remove);
		paddlesMap.clear();
	}

	/**
	 * Called when a round times out. Determines the winner by Arkanoid score and
	 * ends the round.
	 */
	public void onTimeout() {
		TeamColor roundWinner = null;
		// find the team with the highest Arkanoid score
		TeamInfo red = teams.get(TeamColor.RED);
		TeamInfo blu = teams.get(TeamColor.BLUE);
		// co moi hai team thoi nen thang nao bao tao dung for thi tao cho an don
		if (red.getArkanoidScore() > blu.getArkanoidScore()) {
			roundWinner = TeamColor.RED;
		} else if (blu.getArkanoidScore() > red.getArkanoidScore()) {
			roundWinner = TeamColor.BLUE;
		} else {
			// gambling
			roundWinner = Math.random() < 0.5 ? TeamColor.RED : TeamColor.BLUE;
		}
		onRoundEnd(roundWinner);
	}

	/**
	 * Assigns a team to a list of players.
	 * 
	 * @param team    The team color.
	 * @param players The list of players.
	 */
	public void assignTeam(TeamColor team, List<ArkaPlayer> players) {
		teams.put(team, new TeamInfo(team, players));
		players.forEach(p -> {
			teamMap.put(p, team);
			p.joinGame(this);
		});
	}

	/**
	 * Returns the TeamInfo for a given player.
	 * 
	 * @param player The player.
	 * @return The team the player belongs to.
	 */
	public TeamInfo getTeamOf(ArkaPlayer player) {
		return teams.get(teamMap.get(player));
	}

	
	public TeamInfo getTeam(TeamColor teamColor) {
		return teams.get(teamColor);
	}
	
	/**
	 * Returns the paddle entity owned by a player.
	 * 
	 * @param player The player.
	 * @return The corresponding paddle.
	 */
	public EntityPaddle paddleOf(ArkaPlayer player) {
		return paddlesMap.get(player);
	}

	/**
	 * Spawns a paddle for a specific player.
	 * 
	 * @param owner         The player.
	 * @param isLowerPaddle Whether the paddle is on the bottom.
	 * @param y             The Y-position of the paddle.
	 */
	private void spawnPaddleFor(TeamColor team, ArkaPlayer owner, int y) {
		EntityPaddle paddle = new EntityPaddle(world.nextEntityId(), 
			new Location(world, world.getWidth() / 2, y, 0),
			owner, team // paddle metadata
		);
		
		paddle.setLowerPaddle(team == TeamColor.RED);
		world.addEntity(paddle);
		
		paddlesMap.put(owner, paddle);
	}
	
	long matchStartTime = 0;
	/**
	 * Changes a specific phase of the match.
	 * 
	 * @param phase The phase to start.
	 */
	public void changePhase(MatchPhase phase) {
		if (currentPhase == MatchPhase.MATCH_IDLING && currentPhase != phase) {
			matchStartTime = System.currentTimeMillis();
		}
		
		this.currentPhase = phase;
		switch (phase) {
			case MATCH_IDLING: {
				
			}
			
			case CONCLUDED: {
				// display some text
			}
			
			default: {}
		}
		
		syncMatchStateWithClients();
	}
	
	private void concludeMatch() {
		this.changePhase(MatchPhase.CONCLUDED);
	}
	
	public void onMatchTick() {
		for (TeamInfo team : teams.values()) {
			if (team.getFTScore() >= 3) { // TODO, FT3 hardcoded for now
				winner = team.getTeamColor();
				this.concludeMatch();
				return;
			}
		}
		
		for (TeamInfo team : teams.values()) {
			boolean allDead =
				team.getPlayerInfos().stream()
				.allMatch(player -> player.getHealth() <= 0)
			;
			
			if (allDead) {
				team.loseLife();
				
				if (team.getLivesRemaining() <= 0) {
					// end this round, award FT point to the other team
					TeamColor winner = teams.keySet().stream()
						.filter(c -> c != team.getTeamColor()).findFirst()
					.orElse(null);
					onRoundEnd(winner);
					return;
				}
			}
		}
		
		// Check timeout
		if (System.currentTimeMillis() - matchStartTime >= 180_000) { // TODO PLACEHOLDER
			onTimeout();
		}
	}

	/**
	 * Called when a ball falls into the void.
	 * 
	 * @param ball The ball entity.
	 * @param side The side where it fell.
	 */
	public void onBallFallIntoVoid(EntityWreckingBall ball, VoidSide side) {
		syncMatchStateWithClients();
	}

	/**
	 * Called when a brick is destroyed.
	 * 
	 * @param brick       The brick entity.
	 * @param destroyedBy The team color that destroyed it.
	 */
	public void onBrickDestroyed(EntityBrick brick, TeamColor destroyedBy) {
		syncMatchStateWithClients();
	}

	/**
	 * Called when a buff is collected.
	 * 
	 * @param buff      The buff entity.
	 * @param collector The player who collected it.
	 */
	public void onBuffCollected(EntityBuff buff, ArkaPlayer collector) {
		syncMatchStateWithClients();
	}

	/**
	 * Called when a bullet hits a paddle.
	 * 
	 * @param bullet The bullet entity.
	 * @param target The paddle hit.
	 */
	public void onBulletHit(EntityAKBullet bullet, EntityPaddle target) {
		syncMatchStateWithClients();
	}

	/**
	 * Called when a player leaves the match.
	 * 
	 * @param player The player who left.
	 */
	public void onPlayerLeft(ArkaPlayer player) {
		syncMatchStateWithClients();
	}

	/**
	 * Returns all players currently in the match.
	 * 
	 * @return Collection of players.
	 */
	public Collection<ArkaPlayer> getPlayers() {
		return teamMap.keySet();
	}

	/**
	 * Returns the game mode of this match.
	 * 
	 * @return The game mode.
	 */
	public ArkanoidMode getGameMode() {
		return gameMode;
	}

	/**
	 * Returns whether the match has started.
	 * 
	 * @return True if started, false otherwise.
	 */
	public boolean isMatchStarted() {
		return matchStarted;
	}

	/**
	 * Returns the current phase of the match.
	 * 
	 * @return The current phase.
	 */
	public MatchPhase getCurrentPhase() {
		return currentPhase;
	}

	/**
	 * Returns the current round index.
	 * 
	 * @return The round number.
	 */
	public int getCurrentRound() {
		return roundIndex;
	}
	
	public UUID getMatchId() {
		return matchId;
	}

	/**
	 * Synchronizes the current match state with all clients. Includes player
	 * health, Arkanoid scores, FT scores, round index, and current phase.
	 */
	public void syncMatchStateWithClients() {
		List<TeamEntry> teamEntries = new ArrayList<>();
		for (TeamInfo team : teams.values()) {
			TeamEntry t = new TeamEntry();
			t.teamColor = (byte) team.getTeamColor().ordinal();
			t.ftScore = (byte) team.getFTScore();
			t.arkScore = team.getArkanoidScore();
			t.livesRemaining = (byte) team.getLivesRemaining();
			
			List<PlayerEntry> players = new ArrayList<>();
			for (ArkaPlayer p : team.getPlayers()) {
				PlayerInfo pi = team.getPlayerInfo(p);
				PlayerEntry pe = new PlayerEntry();
				
				pe.uuid = p.getUniqueId(); // UUID
				pe.health = (byte) pi.getHealth(); // paddle HP
				pe.rifleState = (byte) pi.getFiringMode().ordinal(); // rifle mode
				pe.bulletsLeft = (byte) pi.getAKRounds();
				players.add(pe);
			}

			t.players = players.toArray(PlayerEntry[]::new);
			teamEntries.add(t);
		}

		this.world.broadcastPackets(new PacketPlayOutMatchMetadata(
			(byte) getCurrentPhase().ordinal(), 
			(short) getCurrentRound(),
			teamEntries.toArray(TeamEntry[]::new)
		));
	}

	/**
	 * Represents a team within the match. Tracks player health, lives, Arkanoid
	 * score, and FT score.
	 */
	public class TeamInfo {

		private final TeamColor teamColor;
		private final LinkedHashMap<ArkaPlayer, PlayerInfo> playerInfoMap = new LinkedHashMap<>();
		private final List<ArkaPlayer> players;

		private int arkanoidScore;
		private int livesRemaining;
		private int ftScore;
		private boolean eliminated;

		/**
		 * Creates a new team instance.
		 *
		 * @param color   The team color.
		 * @param members The players in the team.
		 */
		public TeamInfo(TeamColor color, List<ArkaPlayer> members) {
			this.teamColor = color;
			this.ftScore = 0;
			this.players = new ArrayList<>(members);
			initializeForNewRound(members);
		}

		/**
		 * Resets the team state for a new round.
		 */
		public void resetForNextRound() {
			playerInfoMap.values().forEach(PlayerInfo::resetHealth);
			this.arkanoidScore = 0;
			this.livesRemaining = Constants.TEAM_STARTING_LIVES;
			this.eliminated = false;
		}

		private void initializeForNewRound(List<ArkaPlayer> members) {
			for (ArkaPlayer p : members) {
				playerInfoMap.put(p, new PlayerInfo(p));
			}
			this.arkanoidScore = 0;
			this.livesRemaining = Constants.TEAM_STARTING_LIVES;
		}

		public int getArkanoidScore() {
			return arkanoidScore;
		}

		public void addArkanoidScore(int delta) {
			this.arkanoidScore += delta;
			syncMatchStateWithClients();
		}

		public int getFTScore() {
			return ftScore;
		}

		public void addFTScore(int delta) {
			this.ftScore += delta;
			syncMatchStateWithClients();
		}

		public void loseLife() {
			if (livesRemaining > 0) {
				livesRemaining--;
			}
			syncMatchStateWithClients();
		}

		public int getLivesRemaining() {
			return livesRemaining;
		}

		public boolean isEliminated() {
			return eliminated;
		}

		public PlayerInfo getPlayerInfo(ArkaPlayer player) {
			return playerInfoMap.get(player);
		}
		
		public List<ArkaPlayer> getPlayers() {
			return Collections.unmodifiableList(this.players);
		}

		public List<PlayerInfo> getPlayerInfos() {
			return Collections.unmodifiableList(new ArrayList<>(playerInfoMap.values()));
		}

		public TeamColor getTeamColor() {
			return teamColor;
		}
	}
	
	public class PlayerInfo {

	    private final ArkaPlayer player;
	    private int health;
	    private RifleMode firingMode;
	    private int akRounds;

	    public PlayerInfo(ArkaPlayer player) {
	        this.player = player;
	        this.health = Constants.PADDLE_MAX_HEALTH;
	        this.firingMode = RifleMode.SAFE;
	        this.akRounds = 0;
	    }

	    public ArkaPlayer getPlayer() {
	        return player;
	    }
	    
	    public int getAKRounds() {
			return akRounds;
		}
	    
	    public void setAKRounds(int akRounds) {
			this.akRounds = Math.max(0, Math.min(Constants.AK_47_MAG_SIZE, akRounds));
		}
	    
	    public boolean pickupAKRounds(int amount) {
	    	if (this.akRounds >= Constants.AK_47_MAG_SIZE) return false;
	    	setAKRounds(getAKRounds() + amount);
	    	return true;
	    }
	    
	    public boolean fireRounds(int amount) {
	    	if (this.akRounds <= 0 || getAKRounds() - amount < 0) return false;
	    	setAKRounds(getAKRounds() - amount);
	    	return true;
	    }
	    
	    public RifleMode getFiringMode() {
			return firingMode;
		}

	    public int getHealth() {
	        return health;
	    }

	    public void setHealth(int health) {
	        this.health = Math.max(0, Math.min(Constants.PADDLE_MAX_HEALTH, health));
	    }

	    public void damage(int amount) {
	        setHealth(health - amount);
	    }

	    public void resetHealth() {
	        this.health = Constants.PADDLE_MAX_HEALTH;
	    }
	    
	    public String getName() {
	    	return this.player.getName();
	    }
	}
}