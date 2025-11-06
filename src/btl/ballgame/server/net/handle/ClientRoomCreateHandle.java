package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInCreateRoom;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.game.match.MatchSettings;
import btl.ballgame.server.net.PlayerConnection;

public class ClientRoomCreateHandle implements PacketHandler<PacketPlayInCreateRoom, PlayerConnection> {
	@Override
	public void handle(PacketPlayInCreateRoom packet, PlayerConnection context) {
		if (!context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		
		ArkaPlayer player = context.getPlayer();
		if (player.getCurrentWaitingRoom() != null) {
			context.closeForViolation();
			return;
		}
		
		if (packet.teamLives <= 0 
			|| packet.teamLives > 5
			|| packet.firstToScore <= 0
			|| packet.firstToScore > 10
			|| packet.name.length() > 50
			|| packet.timePerRound > 600
		) {
			context.closeWithNotify("Invalid match config!");
			return;
		}
		
		ArkanoidServer.getServer().getMatchManager().createRoom(player,
			packet.name,
			packet.isPrivate,
			new MatchSettings(packet.gamemode, 
				packet.firstToScore, 
				packet.timePerRound, 
				packet.teamLives
			)
		);
	}
}
