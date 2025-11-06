package btl.ballgame.client;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.net.handle.*;
import btl.ballgame.client.net.handle.ServerClientFlagsHandle;
import btl.ballgame.client.net.handle.ServerDisplayTitleHandle;
import btl.ballgame.client.net.handle.ServerEntityBBSizeUpdateHandle;
import btl.ballgame.client.net.handle.ServerEntityDestroyHandle;
import btl.ballgame.client.net.handle.ServerEntityEffectsHandle;
import btl.ballgame.client.net.handle.ServerEntityMetadataUpdateHandle;
import btl.ballgame.client.net.handle.ServerEntityPositionUpdateHandle;
import btl.ballgame.client.net.handle.ServerEntitySpawnHandle;
import btl.ballgame.client.net.handle.ServerHelloAckHandle;
import btl.ballgame.client.net.handle.ServerListPublicRoomsHandle;
import btl.ballgame.client.net.handle.ServerLoginAckHandle;
import btl.ballgame.client.net.handle.ServerMatchInitHandle;
import btl.ballgame.client.net.handle.ServerMatchMetadataHandle;
import btl.ballgame.client.net.handle.ServerPingHandle;
import btl.ballgame.client.net.handle.ServerWorldInitHandle;
import btl.ballgame.client.net.handle.ServerSocketCloseHandle;
import btl.ballgame.client.net.handle.ServerWaitingRoomUpdateHandle;
import btl.ballgame.client.net.systems.CSEntityRegistry;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.client.net.systems.entities.*;
import btl.ballgame.protocol.PacketCodec;
import btl.ballgame.protocol.PacketRegistry;
import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.in.PacketPlayInClientLogin;
import btl.ballgame.protocol.packets.in.PacketPlayInClientUserCreation;
import btl.ballgame.protocol.packets.in.PacketPlayInLeaveContext;
import btl.ballgame.protocol.packets.out.PacketPlayOutClientFlags;
import btl.ballgame.protocol.packets.out.PacketPlayOutCloseSocket;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityBBSizeUpdate;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityDestroy;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityEffects;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityPosition;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntitySpawn;
import btl.ballgame.protocol.packets.out.PacketPlayOutGameOver;
import btl.ballgame.protocol.packets.out.PacketPlayOutHelloAck;
import btl.ballgame.protocol.packets.out.PacketPlayOutListPublicRooms;
import btl.ballgame.protocol.packets.out.PacketPlayOutWorldInit;
import btl.ballgame.protocol.packets.out.PacketPlayOutLoginAck;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchJoin;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutPing;
import btl.ballgame.protocol.packets.out.PacketPlayOutRoomJoinError;
import btl.ballgame.protocol.packets.out.PacketPlayOutRoomUpdate;
import btl.ballgame.protocol.packets.out.PacketPlayOutTitle;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.EntityType;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ArkanoidClientCore {
	private CServerConnection connection;
	private PacketRegistry registry;
	private PacketCodec codec;
	private CSEntityRegistry entityRegistry;
	
	private ClientPlayer clientPlayer;
	
	// match related information
	private ClientArkanoidMatch activeMatch;
	private CEntityPaddleLocal controlPaddle;
	
	private ScheduledExecutorService clientExecutor;
	private int currentTick;
	
	public ArkanoidClientCore(Socket socket) throws IOException {
		this.registry = new PacketRegistry();
		this.codec = new PacketCodec(this.registry);
		this.entityRegistry = new CSEntityRegistry();
		ProtoUtils.registerMutualPackets(this.registry); // ensure that the client & server share the same understanding of packet types
		this.registerPacketHandlers();
		this.registerEntities();
		
		this.connection = new CServerConnection(socket, this);
		this.clientExecutor = Executors.newScheduledThreadPool(1);

		// this is the client tick task, runs at 30 TPS
		clientExecutor.scheduleAtFixedRate(() -> {
			CSWorld world = getActiveWorld();
			if (world != null) {
				world.tick();
			}
			++currentTick;
		}, 0, Constants.MS_PER_TICK, TimeUnit.MILLISECONDS);
		
		// this is the client packet dispatcher task, runs at 60TPS (60hz)
		clientExecutor.scheduleAtFixedRate(() -> {
			// notify the network dispatcher to flush queued packets
			this.connection.notifyDispatcher();
		}, 0, Constants.MS_PER_TICK / 2, TimeUnit.MILLISECONDS);
	}
	
	private void registerPacketHandlers() {
		// connection/protocol handlers
		this.registry.registerHandler(PacketPlayOutHelloAck.class, new ServerHelloAckHandle());
		this.registry.registerHandler(PacketPlayOutCloseSocket.class, new ServerSocketCloseHandle());
		this.registry.registerHandler(PacketPlayOutLoginAck.class, new ServerLoginAckHandle());
		this.registry.registerHandler(PacketPlayOutPing.class, new ServerPingHandle());
		this.registry.registerHandler(PacketPlayOutRoomJoinError.class, new ServerRoomJoinErrorHandle());
		// more to add
		this.registry.registerHandler(PacketPlayOutRoomUpdate.class, new ServerWaitingRoomUpdateHandle());
		this.registry.registerHandler(PacketPlayOutListPublicRooms.class, new ServerListPublicRoomsHandle());
		this.registry.registerHandler(PacketPlayOutMatchJoin.class, new ServerMatchInitHandle());
		this.registry.registerHandler(PacketPlayOutWorldInit.class, new ServerWorldInitHandle());
		this.registry.registerHandler(PacketPlayOutMatchMetadata.class, new ServerMatchMetadataHandle());
		this.registry.registerHandler(PacketPlayOutEntitySpawn.class, new ServerEntitySpawnHandle());
		this.registry.registerHandler(PacketPlayOutEntityPosition.class, new ServerEntityPositionUpdateHandle());
		this.registry.registerHandler(PacketPlayOutEntityMetadata.class, new ServerEntityMetadataUpdateHandle());
		this.registry.registerHandler(PacketPlayOutEntityBBSizeUpdate.class, new ServerEntityBBSizeUpdateHandle());
		this.registry.registerHandler(PacketPlayOutEntityDestroy.class, new ServerEntityDestroyHandle());
		this.registry.registerHandler(PacketPlayOutClientFlags.class, new ServerClientFlagsHandle());
		this.registry.registerHandler(PacketPlayOutTitle.class, new ServerDisplayTitleHandle());
		this.registry.registerHandler(PacketPlayOutEntityEffects.class, new ServerEntityEffectsHandle());
        this.registry.registerHandler(PacketPlayOutGameOver.class, new ServerGameOverHandle());
	}
	
	/**
	 * Send a login packet to the server
	 * @param username
	 * @param password
	 */
	public void login(String username, String password) {
		connection.sendPacket(new PacketPlayInClientLogin(
			username, // Login credentials
			password // 
		));
	}
	
	public void registerUser(String username, String password, String repeatPassword) {
		if (!password.equals(repeatPassword)) {
			throw new IllegalArgumentException("Passwords do not match.");
		}
		
		connection.sendPacket(new PacketPlayInClientUserCreation(
			username, password
		));
	}
	
	public void disconnect() {
		this.connection.closeConnection();
	}
	
	public void leaveContext() {
		this.connection.sendPacket(new PacketPlayInLeaveContext());
	}
	
	public void setUser(String userName, UUID uuid) {
		this.clientPlayer = new ClientPlayer(userName, uuid);
	}
	
	public void setActiveMatch(ClientArkanoidMatch activeMatch) {
		this.controlPaddle = null; // a new match, remove the old paddle reference
		this.activeMatch = activeMatch;
	}
	
	public void setControlPaddle(CEntityPaddleLocal paddle) {
		this.controlPaddle = paddle;
	}
	
	public ClientArkanoidMatch getActiveMatch() {
		return activeMatch;
	}
	
	public CSWorld getActiveWorld() {
		return activeMatch == null ? null : activeMatch.getGameWorld();
	}
	
	public ClientPlayer getPlayer() {
		return clientPlayer;
	}
	
	private void registerEntities() {
		this.entityRegistry.registerEntity(EntityType.ENTITY_PADDLE, CEntityPaddle::new);
		this.entityRegistry.registerEntity(EntityType.ENTITY_BALL, CEntityWreckingBall::new);
		this.entityRegistry.registerEntity(EntityType.ENTITY_FALLING_ITEM, CEntityFallingItem::new);
		this.entityRegistry.registerEntity(EntityType.ENTITY_RIFLE_BULLET, CEntityAKBullet::new);

		this.entityRegistry.registerEntity(EntityType.ENTITY_BRICK_NORMAL, CEntityBrickNormal::new);
		this.entityRegistry.registerEntity(EntityType.ENTITY_BRICK_ITEM, CEntityItemBrick::new);
		this.entityRegistry.registerEntity(EntityType.ENTITY_BRICK_HARD, CEntityBrickNormal::new);
		this.entityRegistry.registerEntity(EntityType.ENTITY_BRICK_EXPLOSIVE, CEntityExplosiveBrick::new);
	}
	
	public CSEntityRegistry getEntityRegistry() {
		return entityRegistry;
	}
	
	public PacketRegistry getRegistry() {
		return registry;
	}
	
	public PacketCodec codec() {
		return codec;
	}
	
	public CServerConnection getConnection() {
		return connection;
	}
	
	public CEntityPaddleLocal getPaddle() {
		return controlPaddle;
	}
	
	public int getTick() {
		return currentTick;
	}
	
	public void cleanup() {
		if (clientExecutor != null) {
			this.clientExecutor.shutdownNow();
		}
	}
}