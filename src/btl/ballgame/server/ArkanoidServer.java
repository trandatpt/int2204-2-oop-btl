package btl.ballgame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import btl.ballgame.server.net.NetworkManager;
import btl.ballgame.server.net.PlayerConnection;
import btl.ballgame.server.net.handle.ClientDisconnectHandle;
import btl.ballgame.server.net.handle.ClientHelloHandle;
import btl.ballgame.protocol.PacketCodec;
import btl.ballgame.protocol.PacketRegistry;
import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.in.PacketPlayInClientHello;
import btl.ballgame.protocol.packets.in.PacketPlayInDisconnect;

public class ArkanoidServer {
	public static final int VERSION_NUMERIC = 1;
	private static ArkanoidServer server = null;
	
	public static void main(String[] args) {
		server = new ArkanoidServer(3636);
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
	
	public ArkanoidServer(int port) {
		try {
			this.serverSocket = new ServerSocket(port);
			this.registry = new PacketRegistry();
			this.codec = new PacketCodec(registry);
			ProtoUtils.registerMutualPackets(this.registry);
			
			this.netMan = new NetworkManager();
			this.playerManager = new PlayerManager();
			
			this.onServerInit();
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

	private void onServerInit() {
		this.registry.registerHandler(PacketPlayInClientHello.class, new ClientHelloHandle());
		this.registry.registerHandler(PacketPlayInDisconnect.class, new ClientDisconnectHandle());
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
