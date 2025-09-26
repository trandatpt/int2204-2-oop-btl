package btl.ballgame.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.protocol.packets.in.PacketPlayInClientHello;
import btl.ballgame.protocol.packets.out.PacketPlayOutSocketClose;

public class TestClient {
	public static void main(String[] args) {
		try (Socket socket = new Socket("localhost", 3636)) {
			System.out.println("[Client] Connected to server.");
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			PacketPlayInClientHello hello = new PacketPlayInClientHello("baSauNigga", 1);
			hello.write(out);
			out.flush();
			while (true) {
				NetworkPacket packet = NetworkPacket.readNextPacket(in);
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
