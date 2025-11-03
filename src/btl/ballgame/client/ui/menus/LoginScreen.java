package btl.ballgame.client.ui.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.screen.Screen;

public class LoginScreen extends Screen {
	private ArkanoidClientCore core;
	
	public LoginScreen() {
		super("Login to this Server");
		if ((this.core = ArkanoidGame.core()) == null) {
			throw new IllegalStateException("What the fuck??");
		}
	}

	@Override
	public void onInit() {

		SoundManager.playloop("MusicInGame");
		// background shit
		setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e1e, #2a2a2a);");

		// ARKANOID LOGO
		ImageView logo = this.createElement("logo", new ImageView(
			CSAssets.LOGO
		));

		logo.setPreserveRatio(true);
		logo.setFitWidth(500);
		
		// LOGIN: USERNAME + PASSWORD
		TextField username = this.createElement("usernameField", new TextField());
		username.setPromptText("Username");
		username.setMaxWidth(400);
		username.setMinHeight(30);

		PasswordField password = this.createElement("passwordField", new PasswordField());
		password.setPromptText("Password");
		password.setMaxWidth(400);
		password.setMinHeight(30);

		// SYSTEM BUTTONS
		Button logInButton = this.createElement("connectButton", new Button("Log In"));
		Button createAccountBtn = this.createElement("offlineButton", new Button("Create an Account"));
		Button disconnectButton = this.createElement("offlineButton", new Button("Disconnect from this server"));

		logInButton.setPrefWidth(300);
		createAccountBtn.setPrefWidth(400);
		disconnectButton.setPrefWidth(400);
		
		Label statusLabel = new Label("");
		MenuUtils.styleButton(logInButton, "#3b8a7c", "#2d695e"); // aqua
		MenuUtils.styleButton(createAccountBtn, "#4d476e", "#353147"); // purple
		MenuUtils.styleButton(disconnectButton, "#b22222", "#8b1a1a"); // red
		
		// CLICK THE BUTTONS
		logInButton.setOnAction(e -> {
			String user = username.getText().trim();
			String pass = password.getText().trim();

			if (user.isEmpty() || pass.isEmpty()) {
				statusLabel.setText("Please enter both username and password!");
				statusLabel.setTextFill(Color.ORANGE);
				SoundManager.clickFalse();
				return;
			}
			
			if (!user.matches("^[a-zA-Z0-9_]{3,16}$")) {
				statusLabel.setText("Username must be 3-16 characters and use only letters, numbers, or underscores!");
				statusLabel.setTextFill(Color.RED);
				SoundManager.clickFalse();
				return;
			}
			
			SoundManager.clickBottonLogin();
			core.login(user, pass);
			InformationalScreen loggingIn = new InformationalScreen(
				"Logging in...",
				"Logging in..."
			);
			loggingIn.addButton("Disconnect", () -> {
				core.disconnect();
				MenuUtils.displayServerSelector();
			});
			ArkanoidGame.manager().setScreen(loggingIn);
		});

		createAccountBtn.setOnAction(e -> createAccountScreen());
		disconnectButton.setOnAction(e -> {
			SoundManager.clickBottonLogin();
			core.disconnect();
			MenuUtils.displayServerSelector();
		});
				
		// DEFINE THE LAYOUT
		VBox loginBox = new VBox(10,
			new Label("Please log in or create an account to continue!"),
			username,
			password,
			statusLabel,
			logInButton,
			new Label(""), // evil layout hack
			new Label("Don't have an account?"),
			createAccountBtn,
			disconnectButton
		);
		((Label) loginBox.getChildren().get(0)).setTextFill(Color.WHITE);
		((Label) loginBox.getChildren().get(6)).setTextFill(Color.WHITE); // lil shit
		loginBox.setAlignment(Pos.CENTER);

		// general layout
		VBox layout = this.createElement("menuRoot", new VBox(50, logo, loginBox));
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(40));
		StackPane.setAlignment(layout, Pos.CENTER);
		
		this.addElement(layout);
	}
	
	// called when the user is ready to be online
	private void connectOnline() {
		
	}

	private void createAccountScreen() {
		SoundManager.clickSoundConfirm();;
		CreateAccountScreen createNewAccount = new CreateAccountScreen();
		ArkanoidGame.manager().setScreen(createNewAccount);
	}

	@Override
	public void onRemove() {}
}
