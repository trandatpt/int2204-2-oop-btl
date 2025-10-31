package btl.ballgame.client.ui.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.net.ClientNetworkManager;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.screen.Screen;

public class ServerSelector extends Screen {

	private final List<PredefinedServer> predefinedServers;

	public ServerSelector(PredefinedServer... predefinedServers) {
		super("Server Selector"); // bu
		this.predefinedServers = Arrays.asList(predefinedServers);
	}

	@Override
	public void onInit() {
		// background shit
		setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e1e, #2a2a2a);");

		// ARKANOID LOGO
		ImageView logo = this.createElement("logo", new ImageView(
			CSAssets.LOGO
		));

		SoundManager.playloop("MusicMenu");
		logo.setPreserveRatio(true);
		logo.setFitWidth(500);
		
		// SERVER DROPDOWN AND SELECTION
		ComboBox<Object> dropdown = this.createElement("serverDropdown", new ComboBox<>());
		dropdown.getItems().addAll(predefinedServers);
		dropdown.getItems().add("Custom Server"); // allow users to specify a custom server
		
		dropdown.setPromptText("Select a server...");
		
		dropdown.setMaxWidth(400);
		dropdown.setMinHeight(30);
		
		// CUSTOM SERVER ADDRESS BOX
		TextField customServerBox = this.createElement("customServerBox", new TextField());
		customServerBox.setPromptText("Enter custom server address...");
		customServerBox.setDisable(true);

		customServerBox.setMaxWidth(400);
		customServerBox.setMinHeight(30);
		
		// IF THE USER SELECT CUSTOM SERVER, ALLOW THEM TO SPECIFY ONE, OTHERWISE
		// PUT THE DEFINED ADDRESS IN THERE AND DONT LET THEM
		dropdown.setOnAction(e -> {
			Object selected = dropdown.getValue();
			if (selected instanceof PredefinedServer server) {
				customServerBox.setText(server.domain);
				customServerBox.setDisable(true);
			} else {
				customServerBox.clear();
				customServerBox.setDisable(false);
			}
		});

		// SYSTEM BUTTONS
		Button connectButton = this.createElement("connectButton", new Button("Join Server"));
		Button offlineButton = this.createElement("offlineButton", new Button("Play Offline"));
		Button settingsButton = this.createElement("settingsButton", new Button("Settings"));
		Button exitButton = this.createElement("exitButton", new Button("Quit Game"));

		connectButton.setPrefWidth(300);
		offlineButton.setPrefWidth(300);
		settingsButton.setPrefWidth(145);
		exitButton.setPrefWidth(145);

		MenuUtils.styleButton(connectButton, "#699456", "#4c6940"); // green
		MenuUtils.styleButton(offlineButton, "#4682b4", "#36648b"); // blue
		MenuUtils.styleButton(settingsButton, "#636363", "#454545"); // gray
		MenuUtils.styleButton(exitButton, "#b22222", "#8b1a1a"); // red
		
		// CLICK THE BUTTONS
		connectButton.setOnAction(e -> connectOnline());
		offlineButton.setOnAction(e -> goOffline());
		settingsButton.setOnAction(e -> goSettings());
		exitButton.setOnAction(e -> exitScreen());
		
		// horizontal box
		HBox bottomButtons = new HBox(10, settingsButton, exitButton);
		bottomButtons.setAlignment(Pos.CENTER);
		
		// DEFINE THE LAYOUT
		VBox serverBox = new VBox(10, 
			new Label("Play with Friends and Foes"),
			dropdown, 
			customServerBox, 
			connectButton,
			new Label(""), // evil layout hack
			new Label("Not interested in PvP?"),
			offlineButton,
			bottomButtons
		);
		((Label) serverBox.getChildren().get(0)).setTextFill(Color.WHITE);
		((Label) serverBox.getChildren().get(5)).setTextFill(Color.WHITE); // hacky lil shit
		serverBox.setAlignment(Pos.CENTER);

		// general layout
		VBox layout = this.createElement("menuRoot", new VBox(50, logo, serverBox));
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(40));
		StackPane.setAlignment(layout, Pos.CENTER);
		
		this.addElement(layout);
	}
	
	// called when the user is ready to be online
	private void connectOnline() {
		ComboBox<Object> serverDropdown = getElementById("serverDropdown");
		TextField customServerBox = getElementById("customServerBox");
		Object selected = serverDropdown.getValue();
		
		if (selected == null) {
			SoundManager.clickFalse();
			return;
		}

		String address;
		if (selected instanceof PredefinedServer server) {
			address = server.domain;
		} else {
			address = customServerBox.getText().trim();
			if (address.isEmpty()) {
				SoundManager.clickFalse();
				return;
			}
		}
		SoundManager.clickBottonMenu();
		ClientNetworkManager.connectToServer(address);
	}

	private void goOffline() {
		// SoundManager.ClickFalse();  false
		// SoundManager.ClickBottonMenu(); click successful
	}

	private void goSettings() {
		SoundManager.play("Confirm");
		SettingsScreen settings = new SettingsScreen();
		ArkanoidGame.manager().setScreen(settings);
	}

	private void exitScreen() {
		SoundManager.play("Confirm");
		SoundManager.stopAllSounds();
		System.exit(0);
	}
	
	@Override
	public void onRemove() {}

	//INNER CLASS
	public static class PredefinedServer {
		private final String displayName;
		private final String domain;

		public PredefinedServer(String displayName, String domain) {
			this.displayName = displayName;
			this.domain = domain;
		}
		
		@Override
		public String toString() {
			return this.displayName;
		}
	}
}
