package btl.ballgame.client.ui.menus;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ui.menus.ServerSelector.PredefinedServer;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MenuUtils {
	public static void displayLoginScreen() {
		if (ArkanoidGame.core() == null) {
			displayServerSelector();
			return;
		}
		LoginScreen loginScreen = new LoginScreen();
		ArkanoidGame.manager().setScreen(loginScreen);
	}
	
	public static void displayServerSelector() {
		ServerSelector selector = new ServerSelector(
			new PredefinedServer("Arkanoid Network", "arkanoid.skysim.sbs"),
			new PredefinedServer("Local Host", "localhost")
		);
		ArkanoidGame.manager().setScreen(selector);
	}
	
	public static void styleButton(Button btn, String baseColor, String hoverColor) {
		btn.setFont(Font.font(null, FontWeight.BOLD, 14));
		btn.setStyle("-fx-background-color: " + baseColor + "; -fx-text-fill: white;");
		btn.setOnMouseEntered(e -> { 
			btn.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white;");
		});
		btn.setOnMouseExited(e -> { 
			btn.setStyle("-fx-background-color: " + baseColor + "; -fx-text-fill: white;");
		});
	}
}
