package btl.ballgame.client.ui.menus;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ui.menus.ServerSelector.PredefinedServer;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MenuUtils {
	public static void showLoadingScreen(String reason) {
		Platform.runLater(() -> {
			InformationalScreen loading = new InformationalScreen(
				reason, "", reason
			);
			ArkanoidGame.manager().setScreen(loading);
		});
	}
	
	public static void connectionLostScreen(String reason) {
		Platform.runLater(() -> {
			InformationalScreen disconnected = new InformationalScreen(
				"Disconnected from server", 
				"Connection Lost",
				reason
			);
			disconnected.addButton("Return to Server Selector", () -> {
				MenuUtils.displayServerSelector();
			});
			ArkanoidGame.manager().setScreen(disconnected);
		});
	}
	
	public static void failedToConnectScreen(String reason) {
		Platform.runLater(() -> {
			// error screen, like minecraft
			InformationalScreen errorScreen = new InformationalScreen(
				"Connection Error!",
				"Failed to connect to this server",
				reason
			);
			errorScreen.addButton("Return to Server Selector", () -> {
				MenuUtils.displayServerSelector();
			});
			ArkanoidGame.manager().setScreen(errorScreen);
		});
	}
	
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
