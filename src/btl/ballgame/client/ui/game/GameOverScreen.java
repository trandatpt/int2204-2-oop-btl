package btl.ballgame.client.ui.game;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ui.menus.MenuUtils;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.protocol.packets.out.PacketPlayOutGameOver; // (NEW) Import packet
import btl.ballgame.shared.libs.Constants.GameOverType; // (NEW) Assume this exists
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * A simple overlay screen to show "VICTORY" or "DEFEAT".
 */
public class GameOverScreen extends Screen {

    // (MODIFIED) Store the entire packet
    private final PacketPlayOutGameOver packet;

    // (REMOVED) Old fields
    // private final boolean didWin;
    // private final int finalScore;

    /**
     * (MODIFIED) Creates the Game Over overlay using the data packet.
     * @param packet The PacketPlayOutGameOver received from the server.
     */
    public GameOverScreen(PacketPlayOutGameOver packet) {
        super("GameOverScreen");
        this.packet = packet;

        // Make the whole screen semi-transparent
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
    }

    @Override
    public void onInit() {
        StackPane root = new StackPane();
        root.setPadding(new Insets(20));

        // --- Set Background ---
        BackgroundSize mainBgSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundSize borderBgSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage mainBg = new BackgroundImage(
                CSAssets.OVERSCREEN,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                mainBgSize
        );
        BackgroundImage borderBg = new BackgroundImage(
                CSAssets.BORDER_BG,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                borderBgSize
        );
        root.setBackground(new Background(mainBg, borderBg));

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // 1. Logo
        ImageView logoView = new ImageView(CSAssets.LOGO);
        logoView.setFitHeight(250);
        logoView.setPreserveRatio(true);

        // 2. "GAME OVER" Text
        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setTextFill(Color.GHOSTWHITE);
        gameOverLabel.setStyle("-fx-font-size: 100px; -fx-font-weight: bold;");

        // Add Logo and GAME OVER label
        contentBox.getChildren().addAll(logoView, gameOverLabel);

        // --- (NEW) 3. Add UI based on Game Mode ---
        if (packet.isClassic()) {
            // --- SOLO MODE UI ---

            // 3a. Final Score (Solo)
            Label scoreLabel = new Label("Score: " + String.format("%016d", packet.getScore()));
            scoreLabel.setTextFill(Color.LIGHTCYAN);
            scoreLabel.setStyle("-fx-font-size: 30px; -fx-font-family: 'Monospaced';");

            // 3b. Level (Solo)
            Label levelLabel = new Label("Level: " + packet.getLevel());
            levelLabel.setTextFill(Color.WHITE);
            levelLabel.setStyle("-fx-font-size: 24px; -fx-font-family: 'Monospaced';");

            // 3c. High Score (Solo)
            Label highScoreLabel = new Label("High Score: " + String.format("%016d", packet.getHighScore()));
            highScoreLabel.setTextFill(Color.GOLD);
            highScoreLabel.setStyle("-fx-font-size: 24px; -fx-font-family: 'Monospaced';");

            contentBox.getChildren().addAll(scoreLabel, levelLabel, highScoreLabel);

        } else {
            // --- PVP MODE UI ---

            // 3a. Title (VICTORY or DEFEAT)
            // (Assuming GameOverType.VICTORY and GameOverType.DEFEAT exist)
            boolean didWin = packet.getType() == GameOverType.VERSUS_VICTORY;
            Label titleLabel = new Label(didWin ? "VICTORY" : "DEFEAT");
            titleLabel.setTextFill(didWin ? Color.LIGHTGOLDENRODYELLOW : Color.INDIANRED);
            titleLabel.setStyle("-fx-font-size: 64px; -fx-font-weight: bold;");

            // 3b. Final Score (PvP)
            Label scoreLabel = new Label(
                    String.format("Final Score: %02d : %02d", packet.getRedScore(), packet.getBlueScore())
            );
            scoreLabel.setTextFill(Color.LIGHTCYAN);
            scoreLabel.setStyle("-fx-font-size: 30px; -fx-font-family: 'Monospaced';");

            contentBox.getChildren().addAll(titleLabel, scoreLabel);
        }

        // 4. Buttons
        Button quitButton = new Button("Quit to Menu");
        MenuUtils.styleButton(quitButton, "#4d476e", "#353147");

        quitButton.setOnAction(e -> {
            ArkanoidGame.core().disconnect();
            MenuUtils.displayServerSelector();
        });

        HBox buttonBox = new HBox(quitButton);
        buttonBox.setAlignment(Pos.CENTER);

        // (MODIFIED) Add button at the end
        contentBox.getChildren().add(buttonBox);
        root.getChildren().add(contentBox);

        this.addElement("root", root);
    }

    @Override
    public void onRemove() {
    }
}