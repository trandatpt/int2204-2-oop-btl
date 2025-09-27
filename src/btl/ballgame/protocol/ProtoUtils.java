package btl.ballgame.protocol;

import btl.ballgame.protocol.packets.in.*;
import btl.ballgame.protocol.packets.out.*;

public class ProtoUtils {
	public static final int 
		// inbound packets (client -> server)
		PLAYIN_CLIENT_HELLO  	= 0x000,
		PLAYIN_DISCONNECT		= 0x001,
		// outbound packets (server -> client)
		PLAYOUT_LOGIN_ACK 		= 0xC00,
		PLAYOUT_CLOSE_SOCKET 	= 0xC01
	;
	
	public static void registerMutualPackets(PacketRegistry registry) {
		registry.registerPacket(PLAYIN_CLIENT_HELLO, PacketPlayInClientHello.class, PacketPlayInClientHello::new);
		registry.registerPacket(PLAYIN_DISCONNECT, PacketPlayInDisconnect.class, PacketPlayInDisconnect::new);
		registry.registerPacket(PLAYOUT_CLOSE_SOCKET, PacketPlayOutCloseSocket.class, PacketPlayOutCloseSocket::new);
		registry.registerPacket(PLAYOUT_LOGIN_ACK, PacketPlayOutLoginAck.class, PacketPlayOutLoginAck::new);
	}
}
