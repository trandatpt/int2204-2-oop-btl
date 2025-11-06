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
	public static final int PROTOCOL_VERSION = 0x1836;
	
	public static final short
		// inbound packets (client -> server)
		PLAYIN_CLIENT_HELLO  	   = 0x000,
		PLAYIN_DISCONNECT		   = 0x001,
		PLAYIN_USER_LOG_IN         = 0x002,
		PLAYIN_USER_SIGN_UP        = 0x003,
		PLAYIN_PONG                = 0x004,
		PLAYIN_CREATE_ROOM         = 0x005,
		PLAYIN_JOIN_ROOM           = 0x006,
		PLAYIN_LEAVE_ROOM          = 0x007,
		PLAYIN_SWAP_TEAM           = 0x008,
		PLAYIN_SET_READY           = 0x009,
		PLAYIN_PADDLE_INPUT        = 0x00A,
		PLAYIN_FIRING_MODE_CHANGE  = 0x00B,
		PLAYIN_PAUSE_MATCH         = 0x00C,
		PLAYIN_REQUEST_LIST_PR     = 0x00D,
		// outbound packets (server -> client)
		PLAYOUT_CLIENT_HELLO_ACK   = 0xC00,
		PLAYOUT_LOGIN_ACK 		   = 0xC01,
		PLAYOUT_CLOSE_SOCKET 	   = 0xC02,
		PLAYOUT_PING               = 0xC03,
		PLAYOUT_ROOM_JOIN_ERROR    = 0xC04,
		PLAYOUT_WAIT_ROOM_UPDATE   = 0xC05,
		PLAYOUT_WAIT_ROOM_DISBAND  = 0xC06,
		PLAYOUT_ENTITY_SPAWN       = 0xC07,
		PLAYOUT_ENTITY_POSITION    = 0xC08,
		PLAYOUT_ENTITY_METADATA    = 0xC09,
		PLAYOUT_ENTITY_EFFECTS     = 0xC0A,
		PLAYOUT_ENTITY_BB_SIZE     = 0xC0B,
		PLAYOUT_ENTITY_DESTROY     = 0xC0C,
		PLAYOUT_MATCH_JOIN         = 0xC0D,
		PLAYOUT_WORLD_INIT         = 0xC0E,
		PLAYOUT_MATCH_META_UPDATE  = 0xC0F,
		PLAYOUT_GAME_OVER_SCREEN   = 0xC10,
		PLAYOUT_CLIENT_FLAGS       = 0xC11,
		PLAYOUT_DISPLAY_TITLE      = 0xC12,
		PLAYOUT_LIST_PUB_ROOMS     = 0xC14
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
		// PLAYIN (Client -> Server)
		registry.registerPacket(PLAYIN_CLIENT_HELLO, PacketPlayInClientHello.class, PacketPlayInClientHello::new);
		registry.registerPacket(PLAYIN_DISCONNECT, PacketPlayInDisconnect.class, PacketPlayInDisconnect::new);
		registry.registerPacket(PLAYIN_USER_LOG_IN, PacketPlayInClientLogin.class, PacketPlayInClientLogin::new);
		registry.registerPacket(PLAYIN_USER_SIGN_UP, PacketPlayInClientUserCreation.class, PacketPlayInClientUserCreation::new);
		registry.registerPacket(PLAYIN_PONG, PacketPlayInPong.class, PacketPlayInPong::new);
		
		registry.registerPacket(PLAYIN_CREATE_ROOM, PacketPlayInCreateRoom.class, PacketPlayInCreateRoom::new);
		registry.registerPacket(PLAYIN_JOIN_ROOM, PacketPlayInJoinRoom.class, PacketPlayInJoinRoom::new);
		registry.registerPacket(PLAYIN_LEAVE_ROOM, PacketPlayInLeaveContext.class, PacketPlayInLeaveContext::new);
		registry.registerPacket(PLAYIN_SWAP_TEAM, PacketPlayInRoomSwapTeam.class, PacketPlayInRoomSwapTeam::new);
		registry.registerPacket(PLAYIN_SET_READY, PacketPlayInRoomSetReady.class, PacketPlayInRoomSetReady::new);
		
		registry.registerPacket(PLAYIN_PADDLE_INPUT, PacketPlayInPaddleControl.class, PacketPlayInPaddleControl::new);
		registry.registerPacket(PLAYIN_FIRING_MODE_CHANGE, PacketPlayInChangeFireMode.class, PacketPlayInChangeFireMode::new);
		registry.registerPacket(PLAYIN_PAUSE_MATCH, PacketPlayInPauseGame.class, PacketPlayInPauseGame::new);
		registry.registerPacket(PLAYIN_REQUEST_LIST_PR, PacketPlayInRequestRoomList.class, PacketPlayInRequestRoomList::new);
		
		// PLAYOUT (Server -> Client)
		registry.registerPacket(PLAYOUT_CLIENT_HELLO_ACK, PacketPlayOutHelloAck.class, PacketPlayOutHelloAck::new);
		registry.registerPacket(PLAYOUT_CLOSE_SOCKET, PacketPlayOutCloseSocket.class, PacketPlayOutCloseSocket::new);
		registry.registerPacket(PLAYOUT_LOGIN_ACK, PacketPlayOutLoginAck.class, PacketPlayOutLoginAck::new);
		registry.registerPacket(PLAYOUT_PING, PacketPlayOutPing.class, PacketPlayOutPing::new);
		registry.registerPacket(PLAYOUT_WAIT_ROOM_UPDATE, PacketPlayOutRoomUpdate.class, PacketPlayOutRoomUpdate::new);
		registry.registerPacket(PLAYOUT_WAIT_ROOM_DISBAND, PacketPlayOutRoomDisband.class, PacketPlayOutRoomDisband::new);
		registry.registerPacket(PLAYOUT_LIST_PUB_ROOMS, PacketPlayOutListPublicRooms.class, PacketPlayOutListPublicRooms::new);
		registry.registerPacket(PLAYOUT_ROOM_JOIN_ERROR, PacketPlayOutRoomJoinError.class, PacketPlayOutRoomJoinError::new);

		registry.registerPacket(PLAYOUT_ENTITY_SPAWN, PacketPlayOutEntitySpawn.class, PacketPlayOutEntitySpawn::new);
		registry.registerPacket(PLAYOUT_ENTITY_POSITION, PacketPlayOutEntityPosition.class, PacketPlayOutEntityPosition::new);
		registry.registerPacket(PLAYOUT_ENTITY_METADATA, PacketPlayOutEntityMetadata.class, PacketPlayOutEntityMetadata::new);
		registry.registerPacket(PLAYOUT_ENTITY_EFFECTS, PacketPlayOutEntityEffects.class, PacketPlayOutEntityEffects::new);
		registry.registerPacket(PLAYOUT_ENTITY_BB_SIZE, PacketPlayOutEntityBBSizeUpdate.class, PacketPlayOutEntityBBSizeUpdate::new);
		registry.registerPacket(PLAYOUT_ENTITY_DESTROY, PacketPlayOutEntityDestroy.class, PacketPlayOutEntityDestroy::new);
		registry.registerPacket(PLAYOUT_WORLD_INIT, PacketPlayOutWorldInit.class, PacketPlayOutWorldInit::new);
		registry.registerPacket(PLAYOUT_MATCH_JOIN, PacketPlayOutMatchJoin.class, PacketPlayOutMatchJoin::new);
		registry.registerPacket(PLAYOUT_MATCH_META_UPDATE, PacketPlayOutMatchMetadata.class, PacketPlayOutMatchMetadata::new);
		registry.registerPacket(PLAYOUT_GAME_OVER_SCREEN, PacketPlayOutGameOver.class, PacketPlayOutGameOver::new);
		registry.registerPacket(PLAYOUT_CLIENT_FLAGS, PacketPlayOutClientFlags.class, PacketPlayOutClientFlags::new);
		registry.registerPacket(PLAYOUT_DISPLAY_TITLE, PacketPlayOutTitle.class, PacketPlayOutTitle::new);
	}
}
