package btl.ballgame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import btl.ballgame.protocol.PacketRegistry;

public class ArkanoidServer {
	private ServerSocket serverSocket;
	
	private PacketRegistry registry;
	
	public ArkanoidServer(int port) {
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PacketRegistry getRegistry() {
		return this.registry;
	}
}
