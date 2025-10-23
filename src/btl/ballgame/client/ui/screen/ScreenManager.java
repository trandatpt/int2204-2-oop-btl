package btl.ballgame.client.ui.screen;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {
	private final Stage stage;
	private Screen currentScreen;
	private double width, height; // default is 720p

	public ScreenManager(Stage stage) {
		this.stage = stage;
		this.stage.setTitle("NULL");
		this.setResolution(1240, 720);
	}
	
	public void setResolution(double width, double height) {
		stage.setWidth(this.width = width);
		stage.setHeight(this.height = height);
	}

	/**
	 * Set the current screen. Calls onRemove() on the old screen and onInit() on
	 * the new one.
	 */
	public void setScreen(Screen newScreen) {
		if (newScreen == null) {
			return;
		}
		if (currentScreen != null) {
			currentScreen.onRemove();
		}
		
		newScreen.setManager(this);
		stage.setTitle(newScreen.getScreenTitle());
		newScreen.onInit();
		
		if (stage.getScene() == null) {
			stage.setScene(new Scene(newScreen, width, height));
		} else {
			stage.getScene().setRoot(newScreen);
		}
		currentScreen = newScreen;
	}

	/**
	 * @return the current active screen, or null if none is set.
	 */
	public Screen getCurrentScreen() {
		return currentScreen;
	}
}
