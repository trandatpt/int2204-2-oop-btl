package btl.ballgame.client.ui.game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * A utility class to build the player information UI box.
 * Updated to reflect the new ammo box layout (gun left, details right).
 */
public class PlayerInfoBuilder {

    /**
     * Creates a player information box (VBox) containing name, health, buffs, and ammo.
     *
     * @param playerTag   "P1" or "P2"
     * @param alignment   The alignment for child elements (e.g., Pos.CENTER_LEFT)
     * @return A {@link PlayerInfoUI} object containing the layout and references to dynamic nodes.
     */
    public static PlayerInfoUI createPlayerInfoBox(String playerTag, Pos alignment) {
        VBox playerBox = new VBox(5);
        playerBox.setAlignment(alignment);

        // --- Player Name and Tag ---
        boolean isLeft = alignment == Pos.CENTER_LEFT;
        HBox nameBox = new HBox(8);
        nameBox.setAlignment(alignment);

        Label tagLabel = createPlayerLabel(playerTag, isLeft ? Color.RED : Color.BLUE);
        Label nameLabel = new Label("Player Name"); // Placeholder
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        if (isLeft) {
            nameBox.getChildren().addAll(tagLabel, nameLabel);
        } else {
            nameBox.getChildren().addAll(nameLabel, tagLabel);
        }

        // --- Health and Shield Bars ---
        StackPane healthStack = new StackPane();
        healthStack.setPrefHeight(20);
        healthStack.setMaxHeight(20);

        Region healthBg = new Region();
        healthBg.setStyle("-fx-background-color: #555; -fx-background-radius: 5;");

        Region shieldBar = new Region();
        shieldBar.setStyle("-fx-background-color: #3498db; -fx-background-radius: 5;");

        Region healthBar = new Region();
        healthBar.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 5;");

        healthStack.getChildren().addAll(healthBg, shieldBar, healthBar);
        StackPane.setAlignment(healthBar, isLeft ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        StackPane.setAlignment(shieldBar, isLeft ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        // --- Buffs ---
        HBox buffBox = createBuffBox();
        buffBox.setAlignment(alignment);

        // --- Gun Name (Moved up, as it's on the buff row) ---
        Label gunLabel = new Label("AK-47"); // Placeholder
        gunLabel.setTextFill(Color.WHITE);
        gunLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // --- NEW: Buff and Gun Row ---
        HBox buffAndGunRow = new HBox(10); // 10px spacing
        buffAndGunRow.setAlignment(Pos.CENTER_LEFT); // Vertically center children (FIXED from CENTER_VERTICAL)
        Region buffSpacer = new Region();
        HBox.setHgrow(buffSpacer, Priority.ALWAYS);

        // Add children based on alignment for symmetry
        if (isLeft) {
            // Red Team (Left Side): Gun | Spacer | Buffs
            buffAndGunRow.getChildren().addAll(gunLabel, buffSpacer, buffBox);
        } else {
            // Blue Team (Right Side): Buffs | Spacer | Gun
            buffAndGunRow.getChildren().addAll(buffBox, buffSpacer, gunLabel);
        }


        // --- NEW Ammo Box Layout (HBox) ---
        // This box now *only* contains the ammo details
        HBox ammoBox = new HBox();
        // ammoBox.setAlignment(Pos.CENTER_LEFT); // Alignment is set by container
        ammoBox.setPadding(new Insets(5));
        ammoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); " +
                "-fx-border-color: white; -fx-border-width: 2; " +
                "-fx-background-radius: 5; -fx-border-radius: 5;");
        // Remove fixed width to allow shrink-to-fit
        // ammoBox.setPrefWidth(200);
        // ammoBox.setMaxWidth(200);


        // Spacer (REMOVED from ammoBox)
        // Region spacer = new Region();
        // HBox.setHgrow(spacer, Priority.ALWAYS);

        // Details (Right) - VBox
        VBox ammoDetailsVBox = new VBox(2);
        ammoDetailsVBox.setPadding(new Insets(2));
        ammoDetailsVBox.setAlignment(isLeft ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        // Bullet Count + Icon
        HBox bulletBox = new HBox(4);
        // Added style from your snippet
        bulletBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.4); " +
                "-fx-border-color: white;"
        );
        bulletBox.setAlignment(isLeft ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        Label ammoLabel = new Label("30 / 30"); // Placeholder
        ammoLabel.setTextFill(Color.WHITE);
        ammoLabel.setStyle("-fx-font-size: 14px;");

        Label bulletIcon = new Label("▮"); // Bullet icon
        bulletIcon.setTextFill(Color.YELLOW);
        bulletIcon.setStyle("-fx-font-size: 14px;");

        if (isLeft) {
            bulletBox.getChildren().addAll(bulletIcon, ammoLabel);
        } else {
            bulletBox.getChildren().addAll(ammoLabel, bulletIcon);
        }

        // Firing Mode
        Label fireModeLabel = new Label("FULL AUTO"); // Placeholder
        fireModeLabel.setTextFill(Color.LIGHTGRAY);
        fireModeLabel.setStyle("-fx-font-size: 12px;");

        ammoDetailsVBox.getChildren().addAll(bulletBox, fireModeLabel);

        // Add all to the new HBox
        // The ammoBox *only* contains the details now
        ammoBox.getChildren().add(ammoDetailsVBox);

        // We need a container to align the ammoBox
        HBox ammoStatsContainer = new HBox(ammoBox);
        ammoStatsContainer.setAlignment(alignment);


        // --- Add all to main VBox ---
        playerBox.getChildren().clear(); // Clear previous items
        playerBox.getChildren().addAll(nameBox, healthStack, buffAndGunRow, ammoStatsContainer);

        // --- Create and return the wrapper ---
        return new PlayerInfoUI(playerBox, nameLabel, tagLabel, gunLabel, ammoLabel,
                fireModeLabel, healthBar, shieldBar, buffBox);
    }

    /**
     * Creates a simple HBox for displaying 3 buff slots.
     */
    private static HBox createBuffBox() {
        HBox buffBox = new HBox(5);
        for (int i = 0; i < 3; i++) {
            StackPane buffSlot = new StackPane();
            buffSlot.setPrefSize(32, 32);
            buffSlot.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); " +
                    "-fx-border-color: gray; -fx-border-radius: 3; -fx-background-radius: 3;");
            buffBox.getChildren().add(buffSlot);
        }
        return buffBox;
    }

    /**
     * Creates a styled label for the player tag (P1/P2).
     */
    private static Label createPlayerLabel(String text, Color color) {
        Label label = new Label(text);
        label.setPrefSize(35, 35);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(color);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: white; -fx-background-radius: 5;");
        return label;
    }
}

