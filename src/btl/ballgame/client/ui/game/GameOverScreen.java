package btl.ballgame.client.ui.game;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ui.menus.MenuUtils;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.protocol.packets.out.PacketPlayOutGameOver;
import btl.ballgame.shared.libs.Constants.GameOverType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * A polished, cinematic overlay shown when the game ends.
 */
public class GameOverScreen extends Screen {

    private final PacketPlayOutGameOver packet;

    public GameOverScreen(PacketPlayOutGameOver packet) {
        super("GameOverScreen");
        this.packet = packet;
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
    }

    @Override
    public void onInit() {
        StackPane root = new StackPane();
        root.setPadding(new Insets(40));

        // --- Background with blended textures ---
        BackgroundImage mainBg = new BackgroundImage(
                CSAssets.OVERSCREEN,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        BackgroundImage borderBg = new BackgroundImage(
                CSAssets.BORDER_BG,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        root.setBackground(new Background(mainBg, borderBg));

        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);

        // --- LOGO ---
        ImageView logoView = new ImageView(CSAssets.LOGO);
        logoView.setFitHeight(220);
        logoView.setPreserveRatio(true);
        logoView.setOpacity(0.9);
        logoView.setEffect(new DropShadow(40, Color.color(0, 0, 0, 0.8)));

        // --- “GAME OVER” Header ---
        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setTextFill(Color.WHITE);
        gameOverLabel.setFont(Font.font("Orbitron", FontWeight.EXTRA_BOLD, 80));
        gameOverLabel.setEffect(new DropShadow(25, Color.color(0.3, 0.3, 1.0, 0.7)));

        content.getChildren().addAll(logoView, gameOverLabel);

        // --- MODE-SPECIFIC CONTENT ---
        if (packet.isClassic()) {
            // --- SOLO MODE ---
            Label scoreLabel = makeMonoLabel("Score: " + String.format("%016d", packet.getScore()), Color.LIGHTCYAN, 32);
            Label levelLabel = makeMonoLabel("Level: " + packet.getLevel(), Color.WHITE, 26);
            Label highScoreLabel = makeMonoLabel("High Score: " + String.format("%016d", packet.getHighScore()), Color.GOLD, 26);

            content.getChildren().addAll(scoreLabel, levelLabel, highScoreLabel);

            // --- QUIT BUTTON ---
            Button quitButton = new Button("Quit to Menu");
            MenuUtils.styleButton(quitButton, "#4d476e", "#353147");
            quitButton.setFont(Font.font("Orbitron", FontWeight.SEMI_BOLD, 20));
            quitButton.setTextFill(Color.WHITE);
            quitButton.setEffect(new DropShadow(10, Color.color(0.2, 0.2, 0.6, 0.8)));
            quitButton.setPrefWidth(250);
            quitButton.setPrefHeight(60);

            quitButton.setOnAction(e -> {
                ArkanoidGame.core().disconnect();
                MenuUtils.displayServerSelector();
            });

            VBox.setMargin(quitButton, new Insets(40, 0, 0, 0));
            content.getChildren().add(quitButton);
        } else {
            // --- PVP MODE ---
            boolean didWin = packet.getType() == GameOverType.VERSUS_VICTORY;

            Label resultLabel = new Label(didWin ? "VICTORY" : "DEFEAT");
            resultLabel.setFont(Font.font("Orbitron", FontWeight.BOLD, 70));
            resultLabel.setTextFill(didWin ? Color.LIGHTGOLDENRODYELLOW : Color.INDIANRED);
            resultLabel.setEffect(new DropShadow(30, didWin ? Color.GOLD : Color.CORNFLOWERBLUE));

            Label scoreLabel = makeMonoLabel(
                    String.format("Final Score: %02d : %02d", packet.getRedScore(), packet.getBlueScore()),
                    Color.LIGHTCYAN,
                    30
            );

            content.getChildren().addAll(resultLabel, scoreLabel);

            // --- QUIT BUTTON ---
            Button quitButton = new Button("Quit to Lobby");
            MenuUtils.styleButton(quitButton, "#4d476e", "#353147");
            quitButton.setFont(Font.font("Orbitron", FontWeight.SEMI_BOLD, 20));
            quitButton.setTextFill(Color.WHITE);
            quitButton.setEffect(new DropShadow(10, Color.color(0.2, 0.2, 0.6, 0.8)));
            quitButton.setPrefWidth(250);
            quitButton.setPrefHeight(60);

            quitButton.setOnAction(e -> {
                ArkanoidGame.core().leaveContext();
                MenuUtils.displayLobbyScreen();
            });

            VBox.setMargin(quitButton, new Insets(40, 0, 0, 0));
            content.getChildren().add(quitButton);
        }

        root.getChildren().add(content);
        this.addElement("root", root);
    }

    private Label makeMonoLabel(String text, Color color, int size) {
        Label label = new Label(text);
        label.setTextFill(color);
        label.setFont(Font.font("Segoe UI Symbol, Noto Sans Symbols, System", FontWeight.BOLD, size));
        label.setEffect(new DropShadow(10, Color.color(0, 0, 0, 0.6)));
        return label;
    }

    @Override
    public void onRemove() { }
}
