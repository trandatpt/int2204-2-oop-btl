package btl.ballgame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import btl.ballgame.server.game.EntityRegistry;
import btl.ballgame.server.game.entities.breakable.EntityBrick;
import btl.ballgame.server.game.entities.dynamic.EntityPaddle;
import btl.ballgame.server.game.entities.dynamic.EntityWreckingBall;
import btl.ballgame.server.game.match.ArkanoidMatch;
import btl.ballgame.server.net.NetworkManager;
import btl.ballgame.server.net.PlayerConnection;
import btl.ballgame.server.net.handle.ClientDisconnectHandle;
import btl.ballgame.server.net.handle.ClientHelloHandle;
import btl.ballgame.server.net.handle.ClientLoginHandle;
import btl.ballgame.server.net.handle.ClientPaddleInputHandle;
import btl.ballgame.server.net.handle.ClientPongHandle;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.EntityType;
import btl.ballgame.shared.libs.external.Json;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import btl.ballgame.shared.libs.Constants.TeamColor;
import btl.ballgame.protocol.PacketCodec;
import btl.ballgame.protocol.PacketRegistry;
import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.in.PacketPlayInClientHello;
import btl.ballgame.protocol.packets.in.PacketPlayInClientLogin;
import btl.ballgame.protocol.packets.in.PacketPlayInDisconnect;
import btl.ballgame.protocol.packets.in.PacketPlayInPaddleControl;
import btl.ballgame.protocol.packets.in.PacketPlayInPong;

public class ArkanoidServer {
	public static final int VERSION_NUMERIC = 1;
	
	/** Each Arkanoid world ticks at a fixed 30 TPS */
	public static final int TICKS_PER_SECOND = Constants.TICKS_PER_SECOND;
	public static final int MS_PER_TICK = Constants.MS_PER_TICK;
	
	private static ArkanoidServer server = null;
	
	public static void main(String[] args) {
		server = new ArkanoidServer();
		server.startDedicatedServer();
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
	
	private DataManager dataManager;
	
	// misc data
	private int serverPort;
	private int maxPlayers;
	
	public ArkanoidServer() {
		try {
			this.dataManager = new DataManager();
			
			Json serverProperties = dataManager.getServerProperties();
			this.serverPort = serverProperties.at("tcp-port").asInteger();
			this.maxPlayers = serverProperties.at("max-players").asInteger();
			
			this.serverSocket = new ServerSocket(this.serverPort);
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
	
	private long globalTicksPassed = 0;
	public void startDedicatedServer() {
		System.out.println("[TEST] Started dedi server");
		
		// notify all network dispatchers to flush queued packets every
		// 33 milliseconds (1 tick)
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			if (globalTicksPassed % (5 * ArkanoidServer.TICKS_PER_SECOND) == 0) { // 5s, 30tps
				netMan.pingAllClients();
			}
			netMan.notifyAllDispatcher();
			++globalTicksPassed;
		}, 0, ArkanoidServer.MS_PER_TICK, TimeUnit.MILLISECONDS);
		
		new Thread(() -> {
			Scanner scanner = new Scanner(System.in);
			while (true) {
				String line = scanner.nextLine();
				try {
					handleCommand(line);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "CII/COI").start();
		
		// begin listening to player connection
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
	
	private void handleCommand(String line) {
		String[] parts = line.split(" ");
		String cmd = parts[0].toLowerCase();

		switch (cmd) {
		case "stop":
			System.out.println("Shutting down server...");
			netMan.disconnectAll("Server closed");
			System.exit(0);
			break;
		case "kick":
			playerManager.getPlayer(parts[1]).kick("bruh");
			break;
		case "test": {
			ArkanoidMatch match = new ArkanoidMatch(ArkanoidMode.ONE_VERSUS_ONE);
			match.assignTeam(TeamColor.RED, Arrays.asList(playerManager.getPlayer(parts[1])));
			match.assignTeam(TeamColor.BLUE, Arrays.asList(playerManager.getPlayer(parts[2])));
			match.start();
			break;
		}
		default:
			System.out.println("Unknown command: " + line);
			break;
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
		this.registry.registerHandler(PacketPlayInClientHello.class, new ClientHelloHandle());
		this.registry.registerHandler(PacketPlayInClientLogin.class, new ClientLoginHandle());
		this.registry.registerHandler(PacketPlayInDisconnect.class, new ClientDisconnectHandle());
		this.registry.registerHandler(PacketPlayInPong.class, new ClientPongHandle());
		this.registry.registerHandler(PacketPlayInPaddleControl.class, new ClientPaddleInputHandle());
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
	
	public DataManager getDataManager() {
		return dataManager;
	}
	
	// misc
	public int getMaxPlayers() {
		return maxPlayers;
	}
}
