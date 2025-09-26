package btl.ballgame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import btl.ballgame.server.net.PlayerConnection;
import btl.ballgame.server.net.handle.ClientHelloHandle;

import btl.ballgame.protocol.PacketRegistry;
import btl.ballgame.protocol.packets.in.PacketPlayInClientHello;

public class ArkanoidServer {
	public static void main(String[] args) {
		new ArkanoidServer(3636).startDedicatedServer();
	}
	
	private ServerSocket serverSocket;
	private PacketRegistry registry;
	
	public ArkanoidServer(int port) {
		try {
			this.serverSocket = new ServerSocket(port);
			this.registry = new PacketRegistry();
			
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
	            client.setSoTimeout(5000);
	            System.out.println("[TEST] connected: " + client.getInetAddress());
	            new PlayerConnection(this, client);
	        } catch (IOException e) {
	            e.printStackTrace();
	            break;
	        }
	    }
	}
	
	public void onServerInit() {
		this.registry.register(PacketPlayInClientHello.class, new ClientHelloHandle());
	}
	
	public PacketRegistry getRegistry() {
		return this.registry;
	}
}
