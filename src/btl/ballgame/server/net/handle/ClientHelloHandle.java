package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInClientHello;
import btl.ballgame.protocol.packets.out.PacketPlayOutLoginAck;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.net.PlayerConnection;

public class ClientHelloHandle implements PacketHandler<PacketPlayInClientHello, PlayerConnection> {
	@Override
	public void handle(PacketPlayInClientHello packet, PlayerConnection context) {
		String username = packet.who();
		String passwordHash = "TODO"; // TODO
		
		// username predicates
		if (!username.matches("^[a-zA-Z0-9_]{3,16}$")) {
			context.closeWithNotify("Malformed username.");
			return;
		}
		
		// TODO: check for password
		try {
			ArkaPlayer player = ArkanoidServer.getServer().getPlayerManager().addPlayer(context, username);
			player.playerConnection.sendPacket(new PacketPlayOutLoginAck(player.getUniqueId()));
		} catch (Exception e) {
			context.closeWithNotify(e.getMessage());
		}
	}
}
