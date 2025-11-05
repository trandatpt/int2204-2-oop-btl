package btl.ballgame.server.net.handle;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInClientLogin;
import btl.ballgame.protocol.packets.out.PacketPlayOutLoginAck;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.data.PlayerData;
import btl.ballgame.server.net.PlayerConnection;
import btl.ballgame.shared.libs.Utils;

public class ClientLoginHandle implements PacketHandler<PacketPlayInClientLogin, PlayerConnection> {
	@Override
	public void handle(PacketPlayInClientLogin packet, PlayerConnection context) {
		if (context.hasPlayer()) {
			context.closeForViolation();
			return;
		}
		
		String username = packet.who();
		String password = packet.getPassword();
		
		// username predicates (enforced on both client + server)
		if (!username.matches("^[a-zA-Z0-9_]{3,16}$")) {
			context.closeWithNotify("Malformed username.");
			return;
		}
		
		var data = PlayerData.get(ArkaPlayer.getUUIDFromName(username));
		PacketPlayOutLoginAck loginAck;
		if (data == null) {
			// not registered
			loginAck = new PacketPlayOutLoginAck("User not registered. Please create an account!");
			context.sendPacket(loginAck);
			return;
		}
		
		if (!Utils.SHA256(password).equals(data.getPasswordHash())) {
			// wrong password
			loginAck = new PacketPlayOutLoginAck("Incorrect password. Please try again!");
			context.sendPacket(loginAck);
			return;
		}
		
		try {
			ArkaPlayer player = ArkanoidServer.getServer()
				.getPlayerManager()
			.addPlayer(context, data);
			loginAck = new PacketPlayOutLoginAck(player.getName(), player.getUniqueId()); // success
		} catch (Exception e) {
			// an internal error occurred
			loginAck = new PacketPlayOutLoginAck(e.getMessage());
		}
		context.sendPacket(loginAck);
	}
}
