package btl.ballgame.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import btl.ballgame.protocol.PacketCodec;
import btl.ballgame.protocol.PacketRegistry;
import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.protocol.packets.in.PacketPlayInClientHello;
import btl.ballgame.protocol.packets.in.PacketPlayInDisconnect;
import btl.ballgame.protocol.packets.out.PacketPlayOutCloseSocket;
import btl.ballgame.shared.UnknownPacketException;

public class TestClient {
	public static void main(String[] args) throws UnknownPacketException {
		try (Socket socket = new Socket("localhost", 3636)) {
			PacketRegistry registry = new PacketRegistry();
			PacketCodec codec = new PacketCodec(registry);
			
			ProtoUtils.registerMutualPackets(registry);
			
			System.out.println("[Client] Connected to server.");
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			
			PacketPlayInClientHello hello = new PacketPlayInClientHello("baSauNigga", 1);
			codec.writePacket(out, hello);
			
			PacketPlayInDisconnect disconnect = new PacketPlayInDisconnect();
			codec.writePacket(out, disconnect);
			
			while (true) {
				NetworkPacket packet = codec.readPacket(in);
				System.out.println("[Client] received: " + packet.getClass().getSimpleName());
				if (packet instanceof PacketPlayOutCloseSocket p) {
					System.out.println("[Client] sv asked to close " + p.getReason());
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
