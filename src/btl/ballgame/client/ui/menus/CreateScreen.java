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

public class CreateScreen extends Screen {
    private ArkanoidClientCore core;

    public CreateScreen() {
        super("Create a New Account");
        if ((this.core = ArkanoidGame.core()) == null) {
            throw new IllegalStateException("Client core is null!");
        }
    }

    @Override
    public void onInit() {
        // music login && create && match
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

        // Buttons
        Button createBtn = this.createElement("createButton", new Button("Create Account"));
        Button backBtn = this.createElement("backButton", new Button("Back to Login"));

        createBtn.setPrefWidth(300);
        backBtn.setPrefWidth(300);

        Label statusLabel = new Label("");

        MenuUtils.styleButton(createBtn, "#4d476e", "#353147"); // purple
        MenuUtils.styleButton(backBtn, "#538e91", "#3a6466"); // aqua

        // Button actions
        createBtn.setOnAction(e -> {
            String user = username.getText().trim();
            String pass = password.getText().trim();
            String repPass = repeatPassword.getText().trim();

            if (user.isEmpty() || pass.isEmpty() || repPass.isEmpty()) {
                statusLabel.setText("Please fill in all fields!");
                statusLabel.setTextFill(Color.ORANGE);
                SoundManager.ClickFalse();
                return;
            }

            if (!user.matches("^[a-zA-Z0-9_]{3,16}$")) {
                statusLabel.setText("Username must be 3-16 characters and use only letters, numbers, or underscores!");
                statusLabel.setTextFill(Color.RED);
                SoundManager.ClickFalse();
                return;
            }

            if (!pass.equals(repPass)) {
                statusLabel.setText("Passwords do not match!");
                statusLabel.setTextFill(Color.RED);
                SoundManager.ClickFalse();
                return;
            }

            // send registration packet
            core.registerUser(user, pass, repPass);

            InformationalScreen creatingScreen = new InformationalScreen(
                "Creating Account...",
                "Please wait while your account is being created..."
            );
            creatingScreen.addButton("Cancel", () -> {
                MenuUtils.displayLoginScreen();
            });
            ArkanoidGame.manager().setScreen(creatingScreen);
        });

        backBtn.setOnAction(e -> back());

        // Layout
        VBox formBox = new VBox(10,
                new Label("Create your account!"),
                username,
                password,
                repeatPassword,
                statusLabel,
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
        SoundManager.ClickBottonLogin();
        MenuUtils.displayLoginScreen();
    }
    @Override
    public void onRemove() {}
}
