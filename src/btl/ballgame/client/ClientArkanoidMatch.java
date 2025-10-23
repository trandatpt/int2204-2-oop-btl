package btl.ballgame.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata.PlayerEntry;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata.TeamEntry;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import btl.ballgame.shared.libs.Constants.MatchPhase;
import btl.ballgame.shared.libs.Constants.RifleMode;
import btl.ballgame.shared.libs.Constants.TeamColor;

public class ClientArkanoidMatch {
	private ArkanoidMode mode;
	private CSWorld gameWorld;
	
	private MatchPhase phase = MatchPhase.MATCH_IDLING;
	private int roundIndex = 0;
	
	public Map<TeamColor, CTeamInfo> teams = new HashMap<>();
	
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
			PlayerEntry pentry[] = teamEntry.players;
			CPlayerInfo cpis[] = new CPlayerInfo[pentry.length];
			for (int i = 0; i < cpis.length; i++) {
				CPlayerInfo cpi = new CPlayerInfo();
				PlayerEntry pe = pentry[i];
				cpi.uuid = pe.uuid;
				cpi.name = pe.name;
				cpi.health = pe.health;
				cpi.firingMode = RifleMode.values()[pe.rifleState];
				cpi.bulletsLeft = pe.bulletsLeft;
				cpis[i] = cpi;
			}
			
			teams.put(teamInfo.teamColor, teamInfo);
		}
	}
	
	public static class CTeamInfo {
		public TeamColor teamColor;
		public byte ftScore;
		public int arkScore;
		public byte livesRemaining;
		public CPlayerInfo[] players;
	}
	
	public static class CPlayerInfo {
		public UUID uuid;
		public String name;
		public byte health;
		public byte bulletsLeft;
		public RifleMode firingMode;
	}
}
