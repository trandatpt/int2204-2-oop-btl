package btl.ballgame.client;

import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata.PlayerEntry;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata.TeamEntry;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import btl.ballgame.shared.libs.Constants.MatchPhase;
import btl.ballgame.shared.libs.Constants.RifleMode;
import btl.ballgame.shared.libs.Constants.TeamColor;
import btl.ballgame.shared.libs.Constants.UPlayerEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientArkanoidMatch {
	private final ArkanoidMode mode;
	private CSWorld gameWorld;
	
	private MatchPhase phase = MatchPhase.MATCH_IDLING;
	private TeamColor currentTeam;
	private int roundIndex = 0;
	
	private Map<TeamColor, CTeamInfo> teams = new HashMap<>();
	private final Map<UUID, String> uuidToName;
	
	/**
	 * Construct an ArkanoidMatch representation on the client-side.
	 * 
	 * @param mode the gamemode
	 * @param team the team that this client belongs to
	 * @param uuidToName the map that maps UUID to the player's name
	 */
	public ClientArkanoidMatch(ArkanoidMode mode, TeamColor team, Map<UUID, String> uuidToName) {
		this.mode = mode;
		this.currentTeam = team;
		this.uuidToName = uuidToName;
	}
	
	public CSWorld getGameWorld() {
		return gameWorld;
	}
	
	public TeamColor getCurrentTeam() {
		return currentTeam;
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

    public Map<TeamColor, CTeamInfo> getTeams() {
        return teams;
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
		this.phase = packet.getPhase();
		this.roundIndex = packet.getRoundIndex();
		
		// copy the data from the packet (efficiently)
		for (TeamEntry team : packet.getTeams()) {
			CTeamInfo teamInfo = teams.computeIfAbsent(
				TeamColor.of(team.teamColor), 
				k -> new CTeamInfo(TeamColor.of(team.teamColor))
			);
			teamInfo.ftScore = team.ftScore;
			teamInfo.arkScore = team.arkScore;
			teamInfo.livesRemaining = team.livesRemaining;
			
			// copy players data (optimize later)
			PlayerEntry players[] = team.players;
			CPlayerInfo playerInfos[] = new CPlayerInfo[players.length];
			
			for (int i = 0; i < playerInfos.length; i++) {
				CPlayerInfo cpi = new CPlayerInfo();
				PlayerEntry pe = players[i];
				
				cpi.uuid = pe.uuid;
				cpi.health = pe.health;
				cpi.firingMode = RifleMode.of(pe.rifleState);
				cpi.bulletsLeft = pe.rifleAmmo;
				cpi.effects = pe.effects;
				
				playerInfos[i] = cpi;
			}
			
			teamInfo.players = playerInfos;
		}
	}
	
	public class CTeamInfo {
		public final TeamColor teamColor;
		public byte ftScore;
		public int arkScore;
		public byte livesRemaining;
		public CPlayerInfo[] players;
		
		public CTeamInfo(TeamColor color) {
			this.teamColor = color;
		}
	}
	
	public class CPlayerInfo {
		public UUID uuid;
		public byte health;
		public byte bulletsLeft;
		public RifleMode firingMode;
		public UPlayerEffect[] effects;
		
		/**
		 * goofy ahh function name
		 * @return true if this instance belongs to "me" (the client's player)
		 */
		public boolean isMe() {
			return ArkanoidGame.core().getPlayer().getUniqueId().equals(uuid);
		}
		
		public UPlayerEffect[] getEffects() {
			return this.effects;
		}
		
		public String getName() {
			return uuidToName.get(this.uuid);
		}
	}
}
