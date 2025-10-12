package btl.ballgame.server.game.match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.WorldServer;
import btl.ballgame.server.game.entities.dynamic.EntityPaddle;
import btl.ballgame.shared.libs.Location;

public class ArkanoidMatch {
	ArkanoidMode gameMode;
	WorldServer world;
	
	Map<TeamColor, List<ArkaPlayer>> players = new HashMap<>();
	Map<ArkaPlayer, TeamColor> teamMap = new HashMap<>();
	
	public ArkanoidMatch(ArkanoidMode mode) {
		this.gameMode = mode;
		this.world = new WorldServer(800, 600);
	}
	
	public void assignPlayerTo(TeamColor team, ArkaPlayer player) {
		players.computeIfAbsent(team, k -> new ArrayList<>(2)).add(player);
		teamMap.put(player, team);
		player.joinGame(this);
	}
	
	public ArkanoidMode getGameMode() {
		return this.gameMode;
	}
	
	private void spawnPaddleFor(ArkaPlayer p, int y) {
//		EntityPaddle paddle = new EntityPaddle(p, world.nextEntityId(), 
//		new Location(world, world.getWidth() / 2, y, 0)
//	);
	//world.addEntity(paddle);
	}
	
	public void start() {
		// if the gamemode is a single player
		// spawn paddles based on team
		for (TeamColor team : players.keySet()) {
			List<ArkaPlayer> teamPlayers = players.get(team);
			int yPosition = team == TeamColor.RED ? world.getHeight() - 20 : 20;
			for (ArkaPlayer p : teamPlayers) {
				spawnPaddleFor(p, yPosition);
			}
		}
	}
	
	public static enum TeamColor {
		RED, BLUE
	}
}
