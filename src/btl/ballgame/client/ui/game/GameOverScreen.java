package btl.ballgame.client.ui.game;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ui.menus.MenuUtils;
import btl.ballgame.client.ui.screen.Screen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * A simple overlay screen to show "VICTORY" or "DEFEAT".
 */
public class GameOverScreen extends Screen {

    private final boolean didWin;
    private final int finalScore;

    /**
     * Creates the Game Over overlay.
     * @param didWin True if this player won, false if they lost.
     * @param finalScore The final score to display.
     */
    public GameOverScreen(boolean didWin, int finalScore) {
        super("GameOverScreen");
        this.didWin = didWin;
        this.finalScore = finalScore;

        // Make the whole screen semi-transparent
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
    }

    @Override
    public void onInit() {
        StackPane root = new StackPane();
        root.setPadding(new Insets(20));

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // 1. Title (VICTORY or DEFEAT)
        Label titleLabel = new Label(didWin ? "VICTORY" : "DEFEAT");
        titleLabel.setTextFill(didWin ? Color.GOLD : Color.RED);
        titleLabel.setStyle("-fx-font-size: 64px; -fx-font-weight: bold;");

        // 2. Final Score
        Label scoreLabel = new Label("Final Score: " + String.format("%016d", finalScore));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setStyle("-fx-font-size: 24px; -fx-font-family: 'Monospaced';");

        // 3. Buttons
        Button quitButton = new Button("Quit to Menu");
        MenuUtils.styleButton(quitButton, "#4d476e", "#353147"); // Copied from RoomScreen

        quitButton.setOnAction(e -> {
            ArkanoidGame.core().disconnect();
            MenuUtils.displayServerSelector();
        });

        HBox buttonBox = new HBox(quitButton);
        buttonBox.setAlignment(Pos.CENTER);

        contentBox.getChildren().addAll(titleLabel, scoreLabel, buttonBox);
        root.getChildren().add(contentBox);

        this.addElement("root", root);
    }

    @Override
    public void onRemove() {
    }
}