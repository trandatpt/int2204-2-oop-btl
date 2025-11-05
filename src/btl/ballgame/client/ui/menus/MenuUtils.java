package btl.ballgame.client.ui.menus;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ui.menus.ServerSelector.PredefinedServer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
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
	
	public static void displayTwoVsTwo() {

	}

	public static void displayOneVsOne() {

	}

	public static void displayLobbyScreen() {
		
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

		// Click effect
		btn.setOnMousePressed(e -> btn.setStyle(
				"-fx-background-color: " + baseColor + ";"
			+ "-fx-text-fill: white;"
			+ "-fx-font-size: 16px;"
			+ "-fx-background-radius: 8;"
			+ "-fx-effect: dropshadow(gaussian, " + hoverColor + ", 15, 0.5, 0, 0);" // glow viền
			+ "-fx-scale-x: 0.95;"
			+ "-fx-scale-y: 0.95;"
		));

    	btn.setOnMouseReleased(e -> btn.setStyle(
				"-fx-background-color: " + baseColor + ";"
			+ "-fx-text-fill: white;"
			+ "-fx-font-size: 16px;"
			+ "-fx-background-radius: 8;"
			+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0.2, 0, 1);"
			+ "-fx-scale-x: 1;"
			+ "-fx-scale-y: 1;"
		));
	}

	public static void toast(String msg) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setHeaderText(null);
    	alert.setContentText(msg);
    	alert.show();

    	// auto close sau 1.5s cho giống "toast"
    	new Thread(() -> {
        	try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        	Platform.runLater(alert::close);
    	}).start();
	}
}
