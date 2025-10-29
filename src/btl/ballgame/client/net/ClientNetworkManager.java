package btl.ballgame.client.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ui.menus.InformationalScreen;
import btl.ballgame.client.ui.menus.MenuUtils;
import javafx.application.Platform;

public class ClientNetworkManager {
	public static void connectToServer(String address) {
		// like Minecraft's multiplayer screen
		InformationalScreen infoScreen = new InformationalScreen(
			"Connecting to \"" + address + "\"...",
			"Connecting to this server..."
		);
		
		// launch another thread to handle the connection
		Socket socket = new Socket();
		Thread connectionThread = new Thread(() -> {
			try {
				// attempt to connect to the given address
				socket.connect(new InetSocketAddress(address, 3636), 5000);
				// if success, hand control to Arkanoid Core
				ArkanoidGame.createCore(socket);
			} catch (IOException e) {
				// if the user is the one "cancelling" this connection, dont show that screen
				if (Thread.currentThread().isInterrupted()) return;
				MenuUtils.failedToConnectScreen(e.toString());
			}
		}, "CNM:ConnectionThread");
		infoScreen.addButton("Cancel", () -> {
			try {
				socket.close(); // force the connection to close
				connectionThread.interrupt(); // mark the thread as interrupted
				MenuUtils.displayServerSelector();
			} catch (IOException e) {}
		});
		
		connectionThread.start(); // begin trying to connect
		ArkanoidGame.manager().setScreen(infoScreen);
	}
}
