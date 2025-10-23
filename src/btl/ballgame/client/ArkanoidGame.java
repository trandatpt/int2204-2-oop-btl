package btl.ballgame.client;

import btl.ballgame.client.ui.server.PickServer;
import btl.ballgame.client.ui.server.PickServer.PredefinedServer;
import btl.ballgame.client.ui.window.WindowManager;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import javafx.application.Application;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ArkanoidGame extends Application {
	
	static ArkanoidClientCore core;
	
	@Override
	public void start(Stage stage) throws Exception {
        // Create a client match and a dummy game world
        ClientArkanoidMatch match = new ClientArkanoidMatch(ArkanoidMode.SOLO_ENDLESS);
        match.createGameWorld(800, 600); // 800x600 game world coordinates

        // Create the GameRenderer
        GameRenderer renderer = new GameRenderer(match);

        // Create a scene with the renderer
        Scene scene = new Scene(renderer, 1200, 700); // 1200 width to give space for info panel

        stage.setTitle("BallGame - Test Renderer");
        stage.setScene(scene);
        stage.show();
	}
}
