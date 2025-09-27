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
		// TODO Do this later
		String passwordHash = "ba sau manh dat anh hung co ba doi dep mat cung mot dem";
		
		// username predicates (enforced on both client + server)
		if (!username.matches("^[a-zA-Z0-9_]{3,16}$")) {
			context.closeWithNotify("Malformed username.");
			return;
		}
		
		// TODO: check for password
		PacketPlayOutLoginAck aLoginAck;
		try {
			ArkaPlayer player = ArkanoidServer.getServer().getPlayerManager().addPlayer(context, username);
			aLoginAck = new PacketPlayOutLoginAck(player.getUniqueId()); // success
		} catch (Exception e) {
			aLoginAck = new PacketPlayOutLoginAck(e.getMessage());
		}
		context.sendPacket(aLoginAck);
	}
}
