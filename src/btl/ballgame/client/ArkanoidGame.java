package btl.ballgame.client;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import btl.ballgame.client.ui.menus.LoginScreen;
import btl.ballgame.client.ui.menus.MenuUtils;
import btl.ballgame.client.ui.menus.ServerSelector;
import btl.ballgame.client.ui.menus.ServerSelector.PredefinedServer;
import btl.ballgame.client.ui.screen.ScreenManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ArkanoidGame extends Application {
	public static final Image LOGO = new Image(new File("assets/arkgo.png").toURI().toString());
	private static ArkanoidClientCore core = null;
	private static ScreenManager manager;

	@Override
	public void start(Stage root) throws Exception {
		manager = new ScreenManager(root);

		MenuUtils.displayServerSelector();
//		LoginScreen login = new LoginScreen();
//		manager.setScreen(login);

		root.show();
	}

	public static ScreenManager manager() {
		return manager;
	}

	public static void createCore(Socket socket) throws IOException {
		core = new ArkanoidClientCore(socket);
		Platform.runLater(MenuUtils::displayLoginScreen);
	}

	public static ArkanoidClientCore core() {
		return core;
	}
}
