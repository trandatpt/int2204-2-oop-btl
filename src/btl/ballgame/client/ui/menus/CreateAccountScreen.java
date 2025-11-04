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

public class CreateAccountScreen extends Screen {
    private ArkanoidClientCore core;

    public CreateAccountScreen() {
        super("Create a New Account");
        if ((this.core = ArkanoidGame.core()) == null) {
            throw new IllegalStateException("Client core is null!");
        }
    }

    @Override
    public void onInit() {
        SoundManager.playloop("MusicInGame");

        setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e1e, #2a2a2a);");

        // Logo
        ImageView logo = this.createElement("logo", new ImageView(CSAssets.LOGO));
        logo.setPreserveRatio(true);
        logo.setFitWidth(500);

        // Fields
        TextField username = this.createElement("usernameField", new TextField());
        username.setPromptText("Username");
        username.setMaxWidth(400);
        username.setMinHeight(30);

        PasswordField password = this.createElement("passwordField", new PasswordField());
        password.setPromptText("Password");
        password.setMaxWidth(400);
        password.setMinHeight(30);

        PasswordField repeatPassword = this.createElement("repeatPasswordField", new PasswordField());
        repeatPassword.setPromptText("Repeat Password");
        repeatPassword.setMaxWidth(400);
        repeatPassword.setMinHeight(30);
        
        // The stupid button
		CheckBox eulaCheckBox = this.createElement("eulaCheckBox", new CheckBox("I have read and agree to the End User License Agreement (EULA)"));
		eulaCheckBox.setTextFill(Color.WHITE);
		eulaCheckBox.setAlignment(Pos.CENTER);
		eulaCheckBox.setWrapText(true);
		eulaCheckBox.setMaxWidth(600);
		eulaCheckBox.setStyle("-fx-font-size: 14px;");

        // Buttons
        Button createBtn = this.createElement("createButton", new Button("Create Account"));
        Button backBtn = this.createElement("backButton", new Button("Return to Login Menu"));

        createBtn.setPrefWidth(300);
        backBtn.setPrefWidth(300);

        Label statusLabel = new Label("");

		MenuUtils.styleButton(createBtn, "#3b8a7c", "#2d695e"); // aqua
        MenuUtils.styleButton(backBtn, "#b22222", "#8b1a1a"); // red

        // Button actions
        createBtn.setOnAction(e -> {
            String user = username.getText().trim();
            String pass = password.getText().trim();
            String repPass = repeatPassword.getText().trim();

            if (user.isEmpty() || pass.isEmpty() || repPass.isEmpty()) {
                statusLabel.setText("Please fill in all fields!");
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

            if (!pass.equals(repPass)) {
                statusLabel.setText("Passwords do not match!");
                statusLabel.setTextFill(Color.RED);
                SoundManager.clickFalse();
                return;
            }
            
			if (!eulaCheckBox.isSelected()) {
				statusLabel.setText("You must agree to the End User License Agreement before proceeding.");
				statusLabel.setTextFill(Color.RED);
				SoundManager.clickFalse();
				return;
			}

            // send registration packet
            SoundManager.clickBottonLogin();
            core.registerUser(user, pass, repPass);

            InformationalScreen creatingScreen = new InformationalScreen(
                "Creating Account...",
                "Signing up and logging in..."
            );
            creatingScreen.addButton("Disconnect", () -> {
				core.disconnect();
				MenuUtils.displayServerSelector();
			});
            ArkanoidGame.manager().setScreen(creatingScreen);
        });

        backBtn.setOnAction(e -> back());
        
        VBox small = new VBox(10, eulaCheckBox, statusLabel);
        small.setAlignment(Pos.CENTER);
        small.setPadding(new Insets(20));
        
        // Layout
        VBox formBox = new VBox(10,
                new Label("Create your account!"),
                username,
                password,
                repeatPassword,
                small,
                createBtn,
                backBtn
        );
        ((Label) formBox.getChildren().get(0)).setTextFill(Color.WHITE);
        formBox.setAlignment(Pos.CENTER);

        VBox layout = this.createElement("menuRoot", new VBox(50, logo, formBox));
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        StackPane.setAlignment(layout, Pos.CENTER);

        this.addElement(layout);
    }

    private void back() {
        SoundManager.clickBottonLogin();
        MenuUtils.displayLoginScreen();
    }
    @Override
    public void onRemove() {}
}
