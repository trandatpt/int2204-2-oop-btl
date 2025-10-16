package btl.ballgame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import btl.ballgame.server.game.EntityRegistry;
import btl.ballgame.server.game.entities.breakable.EntityBrick;
import btl.ballgame.server.game.entities.dynamic.EntityPaddle;
import btl.ballgame.server.game.entities.dynamic.EntityWreckingBall;
import btl.ballgame.server.net.NetworkManager;
import btl.ballgame.server.net.PlayerConnection;
import btl.ballgame.server.net.handle.ClientDisconnectHandle;
import btl.ballgame.server.net.handle.ClientLoginHandle;
import btl.ballgame.shared.libs.EntityType;
import btl.ballgame.protocol.PacketCodec;
import btl.ballgame.protocol.PacketRegistry;
import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.in.PacketPlayInClientLogin;
import btl.ballgame.protocol.packets.in.PacketPlayInDisconnect;

public class ArkanoidServer {
	public static final int VERSION_NUMERIC = 1;
	
	/** Each Arkanoid world ticks at a fixed 30 TPS */
	public static final int TICKS_PER_SECOND = 30;
	public static final int MS_PER_TICK = (int) (1000.f / TICKS_PER_SECOND);
	
	private static ArkanoidServer server = null;
	
	public static void main(String[] args) {
		server = new ArkanoidServer(3636);
		//server.startDedicatedServer();
	}
	
	public static ArkanoidServer getServer() {
		return server;
	}
	
	private ServerSocket serverSocket;
	private PacketRegistry registry;
	private PacketCodec codec;
	
	private NetworkManager netMan;
	private PlayerManager playerManager;
	
	private EntityRegistry entityRegistry;
	
	public ArkanoidServer(int port) {
		try {
			this.serverSocket = new ServerSocket(port);
			this.registry = new PacketRegistry();
			this.codec = new PacketCodec(registry);
			ProtoUtils.registerMutualPackets(this.registry);
			
			this.netMan = new NetworkManager();
			this.playerManager = new PlayerManager();
			this.entityRegistry = new EntityRegistry();
			
			this.registerServerEntities();
			this.registerPacketHandlers();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startDedicatedServer() {
		System.out.println("[TEST] Started dedi server");
		while (true) {
			try {
				Socket client = serverSocket.accept();
				client.setSoTimeout(15_000);
				System.out.println("[TEST] connected: " + client.getInetAddress());
				netMan.track(new PlayerConnection(this, client));
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	private void registerServerEntities() {
		// active participants
		this.entityRegistry.registerEntity(EntityType.ENTITY_PADDLE, EntityPaddle.class);
		this.entityRegistry.registerEntity(EntityType.ENTITY_BALL, EntityWreckingBall.class);
		
		// static world entities (bricks)
		this.entityRegistry.registerEntity(EntityType.ENTITY_BRICK_NORMAL, EntityBrick.class);
	}

	private void registerPacketHandlers() {
		this.registry.registerHandler(PacketPlayInClientLogin.class, new ClientLoginHandle());
		this.registry.registerHandler(PacketPlayInDisconnect.class, new ClientDisconnectHandle());
	}
	
	public EntityRegistry getEntityRegistry() {
		return this.entityRegistry;
	}
	
	public PacketRegistry getRegistry() {
		return this.registry;
	}
	
	public PacketCodec codec() {
		return this.codec;
	}
	
	public NetworkManager getNetworkManager() {
		return this.netMan;
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
}
