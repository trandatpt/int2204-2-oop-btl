package btl.ballgame.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import btl.ballgame.protocol.PacketCodec;
import btl.ballgame.protocol.PacketRegistry;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.protocol.packets.in.PacketPlayInClientHello;
import btl.ballgame.protocol.packets.out.PacketPlayOutServerAck;
import btl.ballgame.protocol.packets.out.PacketPlayOutSocketClose;
import btl.ballgame.shared.UnknownPacketException;

public class TestClient {
	public static void main(String[] args) throws UnknownPacketException {
		try (Socket socket = new Socket("localhost", 3636)) {
			PacketRegistry registry = new PacketRegistry();
			PacketCodec codec = new PacketCodec(registry);
			
			registry.registerPacket(0x1, 
				PacketPlayInClientHello.class, PacketPlayInClientHello::new
			);
			registry.registerPacket(0x3, 
					PacketPlayOutServerAck.class, PacketPlayOutServerAck::new
				);
			
			System.out.println("[Client] Connected to server.");
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			
			PacketPlayInClientHello hello = new PacketPlayInClientHello("baSauNigga", 1);
			codec.writePacket(out, hello);
			
			while (true) {
				NetworkPacket packet = codec.readPacket(in);
				System.out.println("[Client] received: " + packet.getClass().getSimpleName());
				if (packet instanceof PacketPlayOutSocketClose) {
					System.out.println("[Client] sv asked to close");
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
