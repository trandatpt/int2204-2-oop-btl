package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInClientLogin;
import btl.ballgame.protocol.packets.out.PacketPlayOutLoginAck;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.net.PlayerConnection;

public class ClientLoginHandle implements PacketHandler<PacketPlayInClientLogin, PlayerConnection> {
	@Override
	public void handle(PacketPlayInClientLogin packet, PlayerConnection context) {
		if (context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		
		String username = packet.who();
		String passwordHash = packet.getPasswordHash();
		
		// username predicates (enforced on both client + server)
		if (!username.matches("^[a-zA-Z0-9_]{3,16}$")) {
			context.closeWithNotify("Malformed username.");
			return;
		}
		
		// TODO: check for password
		PacketPlayOutLoginAck aLoginAck;
		try {
			ArkaPlayer player = ArkanoidServer.getServer().getPlayerManager().addPlayer(context, username);
			aLoginAck = new PacketPlayOutLoginAck(player.getName(), player.getUniqueId()); // success
		} catch (Exception e) {
			aLoginAck = new PacketPlayOutLoginAck(e.getMessage());
		}
		context.sendPacket(aLoginAck);
	}
}
