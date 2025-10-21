package btl.ballgame.client;

import java.util.HashMap;
import java.util.Map;

import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata.PlayerEntry;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata.TeamEntry;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import btl.ballgame.shared.libs.Constants.MatchPhase;
import btl.ballgame.shared.libs.Constants.TeamColor;

public class ClientArkanoidMatch {
	private ArkanoidMode mode;
	private CSWorld gameWorld;
	
	private MatchPhase phase = MatchPhase.MATCH_IDLING;
	private int roundIndex = 0;
	
	private Map<TeamColor, CTeamInfo> teams = new HashMap<>();
	
	public ClientArkanoidMatch(ArkanoidMode mode) {
		this.mode = mode;
	}
	
	public CSWorld getGameWorld() {
		return gameWorld;
	}
	
	public ArkanoidMode getMode() {
		return mode;
	}
	
	public MatchPhase getPhase() {
		return phase;
	}
	
	public int getRoundIndex() {
		return roundIndex;
	}
	
	/**
	 * Creates a simple client side representation of a WorldServer.
	 * 
	 * @param width of the world
	 * @param height of the world
	 */
	public void createGameWorld(int width, int height) {
		this.gameWorld = new CSWorld(width, height);
	}
	
	/**
	 * Syncs this client-side match state with a metadata packet from the server.
	 */
	public void applyMetadata(PacketPlayOutMatchMetadata packet) {
		this.mode = packet.getMode();
		this.phase = packet.getPhase();
		this.roundIndex = packet.getRoundIndex();
		
		teams.clear();
		for (TeamEntry teamEntry : packet.getTeams()) {
			CTeamInfo teamInfo = new CTeamInfo();
			teamInfo.teamColor = TeamColor.values()[teamEntry.teamColor];
			teamInfo.ftScore = teamEntry.ftScore;
			teamInfo.arkScore = teamEntry.arkScore;
			teamInfo.livesRemaining = teamEntry.livesRemaining;
			teamInfo.players = teamEntry.players;
			teams.put(teamInfo.teamColor, teamInfo);
		}
	}
	
	public static class CTeamInfo {
		public TeamColor teamColor;
		public byte ftScore;
		public int arkScore;
		public byte livesRemaining;
		public PlayerEntry[] players;
	}
}
