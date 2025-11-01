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

public class ForgotPasswordScreen extends Screen {
    private ArkanoidClientCore core;

    public ForgotPasswordScreen() {
        super("Forgot Password");
        if ((this.core = ArkanoidGame.core()) == null) {
            throw new IllegalStateException("Client core is null!");
        }
    }

    @Override
    public void onInit() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e1e, #2a2a2a);");

        // Logo
        ImageView logo = this.createElement("logo", new ImageView(CSAssets.LOGO));
        logo.setPreserveRatio(true);
        logo.setFitWidth(400);

        // Info
        Label info = new Label("Enter your username or email to reset your password:");
        info.setTextFill(Color.WHITE);

        TextField userField = this.createElement("userField", new TextField());
        userField.setPromptText("Username or Email");
        userField.setMaxWidth(400);
        userField.setMinHeight(30);

        Button sendBtn = this.createElement("sendButton", new Button("Send Reset Request"));
        Button backBtn = this.createElement("backButton", new Button("Return to Login Menu"));


        sendBtn.setPrefWidth(300);
        backBtn.setPrefWidth(300);

        Label status = new Label("");

        MenuUtils.styleButton(sendBtn, "#3b8a7c", "#2d695e"); // aqua
        MenuUtils.styleButton(backBtn, "#b22222", "#8b1a1a"); // red

        sendBtn.setOnAction(e -> {
            
        });

        backBtn.setOnAction(e -> back());

        VBox formBox = new VBox(15,
            info,
            userField,
            status,
            sendBtn,
            backBtn
        );
        formBox.setAlignment(Pos.CENTER);
        
        VBox layout = this.createElement("abx", new VBox(50, logo, formBox));
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        StackPane.setAlignment(layout, Pos.CENTER);

        this.addElement(layout);
    }



    private void back() {
        SoundManager.clickSoundConfirm();;
        MenuUtils.displayLoginScreen();
    }

    @Override
    public void onRemove() {}
}
