package btl.ballgame.server.game.match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.WorldServer;

public class ArkanoidMatch {
	ArkanoidMode gameMode;
	List<ArkaPlayer> players = new ArrayList<>();
	Map<TeamColor, List<ArkaPlayer>> teams = new HashMap<>(); 
	WorldServer world;
	
	public ArkanoidMatch() {
		
	}
	
	public static enum TeamColor {
		RED, BLUE
	}
}
