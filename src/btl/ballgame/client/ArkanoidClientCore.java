package btl.ballgame.client;

import java.applet.Applet;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.net.handle.ServerEntityBBSizeUpdateHandle;
import btl.ballgame.client.net.handle.ServerEntityDestroyHandle;
import btl.ballgame.client.net.handle.ServerEntityMetadataUpdateHandle;
import btl.ballgame.client.net.handle.ServerEntityPositionUpdateHandle;
import btl.ballgame.client.net.handle.ServerEntitySpawnHandle;
import btl.ballgame.client.net.handle.ServerLoginAckHandle;
import btl.ballgame.client.net.handle.ServerMatchMetadataHandle;
import btl.ballgame.client.net.handle.ServerPingHandle;
import btl.ballgame.client.net.handle.ServerWorldInitHandle;
import btl.ballgame.client.net.handle.ServerSocketCloseHandle;
import btl.ballgame.client.net.systems.CSEntityRegistry;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.client.net.systems.entities.CEntityBrickNormal;
import btl.ballgame.client.net.systems.entities.CEntityWreckingBall;
import btl.ballgame.client.ui.login.LoginMenu;
import btl.ballgame.client.ui.window.WindowManager;
import btl.ballgame.protocol.PacketCodec;
import btl.ballgame.protocol.PacketRegistry;
import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.in.PacketPlayInClientLogin;
import btl.ballgame.protocol.packets.in.PacketPlayInClientUserCreation;
import btl.ballgame.protocol.packets.out.PacketPlayOutCloseSocket;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityBBSizeUpdate;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityDestroy;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityPosition;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntitySpawn;
import btl.ballgame.protocol.packets.out.PacketPlayOutWorldInit;
import btl.ballgame.protocol.packets.out.PacketPlayOutLoginAck;
import btl.ballgame.protocol.packets.out.PacketPlayOutMatchMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutPing;
import btl.ballgame.shared.libs.EntityType;
import btl.ballgame.shared.libs.Utils;
import javafx.application.Application;
import javafx.stage.Stage;

public class ArkanoidClientCore {
	private CServerConnection connection;
	private PacketRegistry registry;
	private PacketCodec codec;
	private CSEntityRegistry entityRegistry;
	
	private ClientPlayer clientPlayer;
	private ClientArkanoidMatch activeMatch;
	
	public ArkanoidClientCore(String serverAddress, int port) throws IOException {
		this.registry = new PacketRegistry();
		this.codec = new PacketCodec(this.registry);
		this.entityRegistry = new CSEntityRegistry();
		ProtoUtils.registerMutualPackets(this.registry); // ensure that the client & server share the same understanding of packet types
		this.registerPacketHandlers();
		this.registerEntities();
		
	// 	this.connection = new CServerConnection(
	// 		new Socket(serverAddress, port), this
	// 	);
	// }
	
	private void registerPacketHandlers() {
		// connection/protocol handlers
		this.registry.registerHandler(PacketPlayOutCloseSocket.class, new ServerSocketCloseHandle());
		this.registry.registerHandler(PacketPlayOutLoginAck.class, new ServerLoginAckHandle());
		this.registry.registerHandler(PacketPlayOutPing.class, new ServerPingHandle());
		// more to add
		this.registry.registerHandler(PacketPlayOutWorldInit.class, new ServerWorldInitHandle());
		this.registry.registerHandler(PacketPlayOutMatchMetadata.class, new ServerMatchMetadataHandle());
		this.registry.registerHandler(PacketPlayOutEntitySpawn.class, new ServerEntitySpawnHandle());
		this.registry.registerHandler(PacketPlayOutEntityPosition.class, new ServerEntityPositionUpdateHandle());
		this.registry.registerHandler(PacketPlayOutEntityMetadata.class, new ServerEntityMetadataUpdateHandle());
		this.registry.registerHandler(PacketPlayOutEntityBBSizeUpdate.class, new ServerEntityBBSizeUpdateHandle());
		this.registry.registerHandler(PacketPlayOutEntityDestroy.class, new ServerEntityDestroyHandle());
	}
	
	/**
	 * Send a login packet to the server
	 * @param username
	 * @param password
	 */
	public void login(String username, String password) {
		connection.sendPacket(new PacketPlayInClientLogin(
			username, // Login credentials
			Utils.SHA256(password), // 
			ProtoUtils.PROTOCOL_VERSION
		));
	}
	
	// public void registerUser(String username, String password, String repeatPassword) {
	// 	if (!password.equals(repeatPassword)) {
	// 		throw new IllegalArgumentException("Passwords do not match.");
	// 	}
		
	// 	connection.sendPacket(new PacketPlayInClientUserCreation(
	// 		username, // Login credentials
	// 		Utils.SHA256(password) //
	// 	));
	// }
	
	public void setUser(String userName, UUID uuid) {
		this.clientPlayer = new ClientPlayer(userName, uuid);
	}
	
	public void setActiveMatch(ClientArkanoidMatch activeMatch) {
		this.activeMatch = activeMatch;
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
		this.entityRegistry.registerEntity(EntityType.ENTITY_BALL, CEntityWreckingBall::new);
		this.entityRegistry.registerEntity(EntityType.ENTITY_BRICK_NORMAL, CEntityBrickNormal::new);
	}
	
	// public CSEntityRegistry getEntityRegistry() {
	// 	return entityRegistry;
	// }
	
	// public PacketRegistry getRegistry() {
	// 	return registry;
	// }
	
	// public PacketCodec codec() {
	// 	return codec;
	// }
	
	// public CServerConnection getConnection() {
	// 	return connection;
	// }

	@Override
	public void start(Stage stage) throws Exception {
		WindowManager manager = new WindowManager(stage);
		LoginMenu login = new LoginMenu(manager);
		manager.show(login, "Login", login.getId());
	}
}
