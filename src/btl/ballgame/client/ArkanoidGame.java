package btl.ballgame.client;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.menus.MenuUtils;
import btl.ballgame.client.ui.screen.ScreenManager;
import btl.ballgame.protocol.ProtoUtils;
import btl.ballgame.protocol.packets.in.PacketPlayInClientHello;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ArkanoidGame extends Application {
	private static ArkanoidClientCore core = null;
	private static ScreenManager manager;

	@Override
	public void start(Stage root) throws Exception {
		// initialize sound
		CSAssets.init();
		manager = new ScreenManager(root);

		MenuUtils.displayServerSelector();
//		LoginScreen login = new LoginScreen();
//		manager.setScreen(login);

		root.show();
	}
	
	@Override
	public void stop() throws Exception {
		try {
			// do some cleanup
			SoundManager.stopAllSounds();
			if (core != null) {
				core.disconnect(); // gracefully terminate the connection
			}
		} finally {
			// kill the jvm anyway
			System.exit(0);
		}
	}

	public static ScreenManager manager() {
		return manager;
	}

	public static void createCore(Socket socket) throws IOException {
		core = new ArkanoidClientCore(socket);
		// tell the server what protocol version we're rocking
		core.getConnection().sendPacket(new PacketPlayInClientHello(
			ProtoUtils.PROTOCOL_VERSION
		));
	}
	
	public static void destroyCore() {
		if (core != null) {
			core.disconnect();
			core.cleanup();
		}
		core = null;
	}

	public static ArkanoidClientCore core() {
		return core;
	}
}
