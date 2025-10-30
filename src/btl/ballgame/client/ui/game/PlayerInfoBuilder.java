package btl.ballgame.client.ui.game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Utility class for building player information UI components for the GameScreen.
 *
 */
public class PlayerInfoBuilder {

    /**
     * Creates a VBox containing all information for a single player (name, HP, buffs, weapon).
     * @param playerLabel "P1" or "P2"
     * @param playerName The player's name (e.g., "Dat09")
     * @param fireMode The weapon's fire mode (e.g., "SEMI AUTO")
     * @param alignment The alignment for the HP bar (CENTER_LEFT or CENTER_RIGHT)
     * @return A VBox containing the player's information
     */
    public static VBox createPlayerInfoBox(String playerLabel, String playerName, String fireMode, Pos alignment) {
        VBox playerBox = new VBox(5);
        playerBox.setAlignment(alignment == Pos.CENTER_LEFT ? Pos.TOP_LEFT : Pos.TOP_RIGHT);

        // --- Name and Hp ---
        VBox nameAndHpBox = new VBox(5);
        nameAndHpBox.setAlignment(alignment == Pos.CENTER_LEFT ? Pos.TOP_LEFT : Pos.TOP_RIGHT);

        Label nameLabel = new Label(playerName);
        nameLabel.setStyle("-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white;"
        );

        HBox hpBox = new HBox(5);
        hpBox.setAlignment(alignment);

        StackPane pLabel = createPlayerLabel(playerLabel);

        Region hpBar = new Region();
        hpBar.setPrefHeight(20);
        hpBar.setMaxHeight(20);
        hpBar.setStyle("-fx-background-color: LIGHTGRAY; " +
                "-fx-border-color: WHITE; " +
                "-fx-background-radius: 3; " +
                "-fx-border-radius: 3;"
        );

        if (alignment == Pos.CENTER_LEFT) {
            hpBox.getChildren().addAll(pLabel, hpBar);
        } else {
            // Reverse the order for the right side (HP bar first, P1 later)
            hpBox.getChildren().addAll(hpBar, pLabel);
        }
        HBox.setHgrow(hpBar, Priority.ALWAYS);

        nameAndHpBox.getChildren().addAll(nameLabel, hpBox);


        // --- Buffs and weapons ---
        HBox detailsBox = new HBox(10);
        detailsBox.setAlignment(Pos.CENTER);

        // HBox for 3 buff
        HBox buffBox = new HBox(5);
        buffBox.getChildren().addAll(createBuffBox(), createBuffBox(), createBuffBox());

        // VBox for bullet info
        VBox ammoBox = new VBox(3);
        ammoBox.setAlignment(Pos.CENTER);

        // Ammo info box (ammo count + icon)
        HBox ammoInfoBox = new HBox(8);
        ammoInfoBox.setAlignment(Pos.CENTER);
        ammoInfoBox.setPrefSize(110, 40);
        ammoInfoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5;"
        );
        ammoInfoBox.setPadding(new Insets(5));

        Label ammoCount = new Label("30/30");
        ammoCount.setStyle("-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white;"
        );

        Label bulletIcon = new Label("▮");
        bulletIcon.setStyle("-fx-font-size: 20px; " +
                "-fx-text-fill: #FFD700;"
        );

        ammoInfoBox.getChildren().addAll(ammoCount, bulletIcon);

        // Label for firing mode
        Label fireModeLabel = new Label(fireMode);
        fireModeLabel.setStyle("-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white;"
        );
        fireModeLabel.setAlignment(Pos.CENTER);

        ammoBox.getChildren().addAll(ammoInfoBox, fireModeLabel);

        // Add buffs and weapons to detailsBox (reverse order depending on side)
        if (alignment == Pos.CENTER_LEFT) {
            detailsBox.getChildren().addAll(buffBox, ammoBox);
        } else {
            detailsBox.getChildren().addAll(ammoBox, buffBox);
        }

        // Add all to the player's main VBox
        playerBox.getChildren().addAll(nameAndHpBox, detailsBox);
        return playerBox;
    }

    /**
     * Creates a 40x40 StackPane placeholder for a Buff.
     */
    public static StackPane createBuffBox() {
        StackPane buffBox = new StackPane();
        buffBox.setPrefSize(40, 40);
        buffBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5;"
        );

        Label buffLabel = new Label("Buff");
        buffLabel.setStyle("-fx-font-size: 12px; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold;"
        );

        buffBox.getChildren().add(buffLabel);
        return buffBox;
    }

    /**
     * Creates a square (30x30) StackPane containing text (P1, P2).
     */
    public static StackPane createPlayerLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white;"
        );
        StackPane pane = new StackPane(label);
        pane.setPrefSize(30, 30);
        pane.setStyle("-fx-background-color: #333; " +
                "-fx-border-color: white; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5;"
        );
        return pane;
    }
}
