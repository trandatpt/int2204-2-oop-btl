package btl.ballgame.protocol;

import btl.ballgame.protocol.packets.in.*;
import btl.ballgame.protocol.packets.out.*;

/**
 * Utility class for defining and registering all network packet types
 * used in the PennyWortProtocol server-client protocol.
 *
 * The {@link #registerMutualPackets(PacketRegistry)} method must be
 * called on startup (on both server and client) to ensure both ends
 * agree on the protocol structure.
 */
public class ProtoUtils {
	public static final int 
		// inbound packets (client -> server)
		PLAYIN_CLIENT_HELLO  	= 0x000,
		PLAYIN_DISCONNECT		= 0x001,
		// outbound packets (server -> client)
		PLAYOUT_LOGIN_ACK 		= 0xC00,
		PLAYOUT_CLOSE_SOCKET 	= 0xC01
	;
	
	/**
	 * Registers all packet types known to both the client and the server.
	 * <p>
	 * This must be called on initialization of the {@link PacketRegistry} to ensure
	 * correct packet ID <-> class mappings during runtime.
	 *
	 * @param registry The {@link PacketRegistry} to register all packets into.
	 */
	public static void registerMutualPackets(PacketRegistry registry) {
		registry.registerPacket(PLAYIN_CLIENT_HELLO, PacketPlayInClientHello.class, PacketPlayInClientHello::new);
		registry.registerPacket(PLAYIN_DISCONNECT, PacketPlayInDisconnect.class, PacketPlayInDisconnect::new);
		registry.registerPacket(PLAYOUT_CLOSE_SOCKET, PacketPlayOutCloseSocket.class, PacketPlayOutCloseSocket::new);
		registry.registerPacket(PLAYOUT_LOGIN_ACK, PacketPlayOutLoginAck.class, PacketPlayOutLoginAck::new);
	}
}
