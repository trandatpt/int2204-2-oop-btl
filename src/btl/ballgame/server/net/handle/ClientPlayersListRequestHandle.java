package btl.ballgame.server.net.handle;

import java.io.IOException;
import java.util.Collections;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInRequestAllPlayersList;
import btl.ballgame.protocol.packets.out.PacketPlayOutGetAllPlayers;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.net.PlayerConnection;

public class ClientPlayersListRequestHandle implements PacketHandler<PacketPlayInRequestAllPlayersList, PlayerConnection> {
	@Override
	public void handle(PacketPlayInRequestAllPlayersList packet, PlayerConnection context) {
		if (!context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		try {
			// slap
			context.sendPacket(new PacketPlayOutGetAllPlayers(
				ArkanoidServer.getServer().getDataManager().getAllPlayerDetails()
			));
		} catch (IOException e) {
			// ah who cares anymore, fuck it
			context.sendPacket(new PacketPlayOutGetAllPlayers(Collections.emptyList()));
		}
	}
}
