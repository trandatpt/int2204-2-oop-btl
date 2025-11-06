package btl.ballgame.server;

import btl.ballgame.protocol.PacketCodec;
import btl.ballgame.protocol.PacketRegistry;
import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.in.*;
import btl.ballgame.server.data.DataManager;
import btl.ballgame.server.game.EntityRegistry;
import btl.ballgame.server.game.entities.breakable.EntityBrick;
import btl.ballgame.server.game.entities.breakable.EntityExplosiveBrick;
import btl.ballgame.server.game.entities.breakable.EntityHardBrick;
import btl.ballgame.server.game.entities.breakable.EntityItemBrick;
import btl.ballgame.server.game.entities.dynamic.EntityAKBullet;
import btl.ballgame.server.game.entities.dynamic.EntityFallingItem;
import btl.ballgame.server.game.entities.dynamic.EntityPaddle;
import btl.ballgame.server.game.entities.dynamic.EntityWreckingBall;
import btl.ballgame.server.game.match.ArkanoidMatch;
import btl.ballgame.server.game.match.MatchManager;
import btl.ballgame.server.game.match.MatchSettings;
import btl.ballgame.server.net.NetworkManager;
import btl.ballgame.server.net.PlayerConnection;
import btl.ballgame.server.net.handle.ClientChangeRifleModeHandle;
import btl.ballgame.server.net.handle.ClientDisconnectHandle;
import btl.ballgame.server.net.handle.ClientHelloHandle;
import btl.ballgame.server.net.handle.ClientLoginHandle;
import btl.ballgame.server.net.handle.ClientPaddleInputHandle;
import btl.ballgame.server.net.handle.ClientPauseGameHandle;
import btl.ballgame.server.net.handle.ClientPongHandle;
import btl.ballgame.server.net.handle.ClientRoomCreateHandle;
import btl.ballgame.server.net.handle.ClientRoomJoinHandle;
import btl.ballgame.server.net.handle.ClientRoomLeaveContextHandle;
import btl.ballgame.server.net.handle.ClientRoomListRequestHandle;
import btl.ballgame.server.net.handle.ClientRoomReadyHandle;
import btl.ballgame.server.net.handle.ClientRoomSwapTeamHandle;
import btl.ballgame.server.net.handle.ClientUserCreationHandle;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import btl.ballgame.shared.libs.Constants.TeamColor;
import btl.ballgame.shared.libs.EntityType;
import btl.ballgame.shared.libs.Utils;
import btl.ballgame.shared.libs.external.Json;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import btl.ballgame.shared.libs.Constants.TeamColor;
import btl.ballgame.protocol.PacketCodec;
import btl.ballgame.protocol.PacketRegistry;
import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.in.PacketPlayInChangeFireMode;
import btl.ballgame.protocol.packets.in.PacketPlayInClientHello;
import btl.ballgame.protocol.packets.in.PacketPlayInClientLogin;
import btl.ballgame.protocol.packets.in.PacketPlayInClientUserCreation;
import btl.ballgame.protocol.packets.in.PacketPlayInCreateRoom;
import btl.ballgame.protocol.packets.in.PacketPlayInDisconnect;
import btl.ballgame.protocol.packets.in.PacketPlayInJoinRoom;
import btl.ballgame.protocol.packets.in.PacketPlayInLeaveContext;
import btl.ballgame.protocol.packets.in.PacketPlayInPaddleControl;
import btl.ballgame.protocol.packets.in.PacketPlayInPauseGame;
import btl.ballgame.protocol.packets.in.PacketPlayInPong;
import btl.ballgame.protocol.packets.in.PacketPlayInRequestRoomList;
import btl.ballgame.protocol.packets.in.PacketPlayInRoomSetReady;
import btl.ballgame.protocol.packets.in.PacketPlayInRoomSwapTeam;

public class ArkanoidServer {
	public static final int VERSION_NUMERIC = 1;
	
	/** Each Arkanoid world ticks at a fixed 30 TPS */
	public static final int TICKS_PER_SECOND = Constants.TICKS_PER_SECOND;
	public static final int MS_PER_TICK = Constants.MS_PER_TICK;
	
	private static ArkanoidServer server = null;
	
	public static void main(String[] args) throws Exception {
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
	private MatchManager matchManager;
	
	private EntityRegistry entityRegistry;
	
	private DataManager dataManager;
	
	// misc data
	private int serverPort;
	private int maxPlayers;
	
	public ArkanoidServer() throws Exception {
		System.out.println("[ArkanoidServer] Loading server properties, please wait...");
		// override sys out, err
		PrintStream out = new PrintStream(System.out) {
			@Override
			public void println(String x) {
				super.println(Utils.time() + x);
			}
		};
		System.setOut(out);
		PrintStream err = new PrintStream(System.err) {
			@Override
			public void println(String x) {
				super.println(Utils.time() + x);
			}
		};
		System.setErr(err);
		this.dataManager = new DataManager();
		
		Json serverProperties = dataManager.getServerProperties();
		this.serverPort = serverProperties.at("tcp-port").asInteger();
		this.maxPlayers = serverProperties.at("max-players").asInteger();
		
		System.out.println("[ArkanoidServer] Loading managers and registries...");
		this.registry = new PacketRegistry();
		this.codec = new PacketCodec(this.registry);
			
		this.netMan = new NetworkManager();
		this.playerManager = new PlayerManager();
		this.entityRegistry = new EntityRegistry();
		this.matchManager = new MatchManager();
		
		System.out.println("[ArkanoidServer] Registering necessary components...");
		ProtoUtils.registerMutualPackets(this.registry);
		this.registerServerEntities();
		this.registerPacketHandlers();
		
		System.out.println("[ArkanoidServer] Attempting to start dedicated server... (port: " + serverPort + ")");
		this.serverSocket = new ServerSocket(this.serverPort);
	}
	
	private long globalTicksPassed = 0;
	public void startDedicatedServer() {
		System.out.println("[ArkanoidServer] Started dedicated server on port: " + serverPort);

		var executor = Executors.newScheduledThreadPool(1);
		// ping every clients every 5s
		executor.scheduleAtFixedRate(() -> {
			if (globalTicksPassed % (5 * ArkanoidServer.TICKS_PER_SECOND) == 0) { // 5s, 30tps
				netMan.pingAllClients();
			}
			++globalTicksPassed;
			matchManager.tick();
		}, 0, ArkanoidServer.MS_PER_TICK, TimeUnit.MILLISECONDS);
		
		executor.scheduleAtFixedRate(() -> {
			netMan.notifyAllDispatcher();
		}, 0, ArkanoidServer.MS_PER_TICK / 2, TimeUnit.MILLISECONDS);
		
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
				netMan.track(new PlayerConnection(this, client));
			} catch (IOException e) {
				e.printStackTrace();
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
			playerManager.getPlayer(parts[1]).kick("Kicked");
			break;
		case "test": {
			ArkanoidMatch match = new ArkanoidMatch(new MatchSettings(ArkanoidMode.SOLO_ENDLESS, 2, 180, 3));
			match.assignTeam(TeamColor.RED, Arrays.asList(playerManager.getPlayer(parts[1])));
			//match.assignTeam(TeamColor.BLUE, Arrays.asList(playerManager.getPlayer(parts[2])));
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
		this.entityRegistry.registerEntity(EntityType.ENTITY_FALLING_ITEM, EntityFallingItem.class);
		this.entityRegistry.registerEntity(EntityType.ENTITY_RIFLE_BULLET, EntityAKBullet.class);
		
		// static world entities (bricks)
		this.entityRegistry.registerEntity(EntityType.ENTITY_BRICK_NORMAL, EntityBrick.class);
		this.entityRegistry.registerEntity(EntityType.ENTITY_BRICK_ITEM, EntityItemBrick.class);
		this.entityRegistry.registerEntity(EntityType.ENTITY_BRICK_HARD, EntityHardBrick.class);
		this.entityRegistry.registerEntity(EntityType.ENTITY_BRICK_EXPLOSIVE, EntityExplosiveBrick.class);
	}

	private void registerPacketHandlers() {
		this.registry.registerHandler(PacketPlayInClientHello.class, new ClientHelloHandle());
		this.registry.registerHandler(PacketPlayInClientUserCreation.class, new ClientUserCreationHandle());
		this.registry.registerHandler(PacketPlayInClientLogin.class, new ClientLoginHandle());
		this.registry.registerHandler(PacketPlayInDisconnect.class, new ClientDisconnectHandle());
		this.registry.registerHandler(PacketPlayInPauseGame.class, new ClientPauseGameHandle());
		this.registry.registerHandler(PacketPlayInPong.class, new ClientPongHandle());
		this.registry.registerHandler(PacketPlayInRequestRoomList.class, new ClientRoomListRequestHandle());
		this.registry.registerHandler(PacketPlayInPaddleControl.class, new ClientPaddleInputHandle());
		this.registry.registerHandler(PacketPlayInChangeFireMode.class, new ClientChangeRifleModeHandle());
		this.registry.registerHandler(PacketPlayInCreateRoom.class, new ClientRoomCreateHandle());
		this.registry.registerHandler(PacketPlayInLeaveContext.class, new ClientRoomLeaveContextHandle());
		this.registry.registerHandler(PacketPlayInRoomSwapTeam.class, new ClientRoomSwapTeamHandle());
		this.registry.registerHandler(PacketPlayInRoomSetReady.class, new ClientRoomReadyHandle());
		this.registry.registerHandler(PacketPlayInJoinRoom.class, new ClientRoomJoinHandle());
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
	
	public MatchManager getMatchManager() {
		return matchManager;
	}
	
	// misc
	public int getMaxPlayers() {
		return maxPlayers;
	}
}
