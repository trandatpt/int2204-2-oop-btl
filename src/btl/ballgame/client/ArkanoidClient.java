package btl.ballgame.client;

import java.io.IOException;
import java.net.Socket;

import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.net.handle.ServerLoginAckHandle;
import btl.ballgame.client.net.handle.ServerSocketCloseHandle;
import btl.ballgame.client.net.handle.TestHandle;
import btl.ballgame.protocol.PacketCodec;
import btl.ballgame.protocol.PacketRegistry;
import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.in.PacketPlayInClientHello;
import btl.ballgame.protocol.packets.out.PacketPlayOutCloseSocket;
import btl.ballgame.protocol.packets.out.PacketPlayOutEntityMetadata;
import btl.ballgame.protocol.packets.out.PacketPlayOutLoginAck;

public class ArkanoidClient {
	public static void main(String[] args) throws IOException {
		ArkanoidClient client = new ArkanoidClient("localhost", 3636);
		client.connection.sendPacket(new PacketPlayInClientHello("generic_user", 0));
	}
	
	private CServerConnection connection;
	private PacketRegistry registry;
	private PacketCodec codec;
	
	public ArkanoidClient(String serverAddress, int port) throws IOException {
		this.registry = new PacketRegistry();
		this.codec = new PacketCodec(this.registry);
		ProtoUtils.registerMutualPackets(this.registry); // ensure that the client & server share the same understanding of packet types
		
		this.connection = new CServerConnection(
			new Socket(serverAddress, port), this
		);
		this.onClientInit();
	}
	
	private void onClientInit() {
		this.registry.registerHandler(PacketPlayOutCloseSocket.class, new ServerSocketCloseHandle());
		this.registry.registerHandler(PacketPlayOutLoginAck.class, new ServerLoginAckHandle());
		// more to add
		this.registry.registerHandler(PacketPlayOutEntityMetadata.class, new TestHandle());
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
