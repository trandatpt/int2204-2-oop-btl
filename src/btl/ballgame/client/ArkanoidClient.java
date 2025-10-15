package btl.ballgame.client;

import java.io.IOException;
import java.net.Socket;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.net.handle.ServerEntityBBSizeUpdateHandle;
import btl.ballgame.client.net.handle.ServerEntityDestroyHandle;
import btl.ballgame.client.net.handle.ServerEntityMetadataUpdateHandle;
import btl.ballgame.client.net.handle.ServerEntityPositionUpdateHandle;
import btl.ballgame.client.net.handle.ServerEntitySpawnHandle;
import btl.ballgame.client.net.handle.ServerLoginAckHandle;
import btl.ballgame.client.net.handle.ServerSocketCloseHandle;
import btl.ballgame.client.net.handle.TestHandle;
import btl.ballgame.client.net.systems.CSEntityRegistry;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.client.net.systems.entities.CEntityWreckingBall;
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
import btl.ballgame.protocol.packets.out.PacketPlayOutLoginAck;
import btl.ballgame.shared.libs.EntityType;
import btl.ballgame.shared.libs.Utils;

public class ArkanoidClient {
	public static void main(String[] args) throws IOException {
		ArkanoidClient client = new ArkanoidClient("localhost", 3636);
	}
	
	private CServerConnection connection;
	private PacketRegistry registry;
	private PacketCodec codec;
	private CSEntityRegistry entityRegistry;
	
	private CSWorld activeWorld = null;
	
	public CSWorld getActiveWorld() {
		return activeWorld;
	}
	
	public ArkanoidClient(String serverAddress, int port) throws IOException {
		this.registry = new PacketRegistry();
		this.codec = new PacketCodec(this.registry);
		ProtoUtils.registerMutualPackets(this.registry); // ensure that the client & server share the same understanding of packet types
		this.registerPacketHandlers();
		this.registerEntities();
		
		this.connection = new CServerConnection(
			new Socket(serverAddress, port), this
		);
	}
	
	public void login(String username, String password) {
		connection.sendPacket(new PacketPlayInClientLogin(
			username, // Login credentials
			Utils.SHA256(password), // 
			ProtoUtils.PROTOCOL_VERSION
		));
	}
	
	public void registerUser(String username, String password, String repeatPassword) {
		if (!password.equals(repeatPassword)) {
			throw new IllegalArgumentException("Passwords do not match.");
		}
		
		connection.sendPacket(new PacketPlayInClientUserCreation(
			username, // Login credentials
			Utils.SHA256(password) //
		));
	}
	
	private void registerPacketHandlers() {
		this.registry.registerHandler(PacketPlayOutCloseSocket.class, new ServerSocketCloseHandle());
		this.registry.registerHandler(PacketPlayOutLoginAck.class, new ServerLoginAckHandle());
		// more to add
		this.registry.registerHandler(PacketPlayOutEntitySpawn.class, new ServerEntitySpawnHandle());
		this.registry.registerHandler(PacketPlayOutEntityPosition.class, new ServerEntityPositionUpdateHandle());
		this.registry.registerHandler(PacketPlayOutEntityMetadata.class, new ServerEntityMetadataUpdateHandle());
		this.registry.registerHandler(PacketPlayOutEntityBBSizeUpdate.class, new ServerEntityBBSizeUpdateHandle());
		this.registry.registerHandler(PacketPlayOutEntityDestroy.class, new ServerEntityDestroyHandle());
	}
	
	private void registerEntities() {
		this.entityRegistry.registerEntity(EntityType.ENTITY_BALL, CEntityWreckingBall::new);
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
}
