package btl.ballgame.server.net.handle;

import java.util.UUID;

import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.in.PacketPlayInClientUserCreation;
import btl.ballgame.protocol.packets.out.PacketPlayOutLoginAck;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.data.PlayerData;
import btl.ballgame.server.net.PlayerConnection;

public class ClientUserCreationHandle implements PacketHandler<PacketPlayInClientUserCreation, PlayerConnection> {
	@Override
	public void handle(PacketPlayInClientUserCreation packet, PlayerConnection context) {
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
		
		UUID uuid;
		var data = PlayerData.get(uuid = ArkaPlayer.getUUIDFromName(username));
		PacketPlayOutLoginAck loginAck;
		if (data != null) {
			// user already registered
			loginAck = new PacketPlayOutLoginAck("This username is already registered.\nPlease log in instead!");
			context.sendPacket(loginAck);
			return;
		}
		
		var newUser = PlayerData.create(uuid, username, password);
		if (newUser == null) {
			// something went wrong
			loginAck = new PacketPlayOutLoginAck("An internal error occurred while creating your account.\nPlease try again later!");
			context.sendPacket(loginAck);
			return;
		}
		
		try {
			ArkaPlayer player = ArkanoidServer.getServer()
				.getPlayerManager()
				.addPlayer(context, newUser);
			// logs the user in as they registered
			loginAck = new PacketPlayOutLoginAck(player.getName(), player.getUniqueId()); // success
		} catch (Exception e) {
			// an internal error occurred
			loginAck = new PacketPlayOutLoginAck(e.getMessage());
		}
		context.sendPacket(loginAck);
	}
}
