package btl.ballgame.client.ui.game;

import btl.ballgame.client.CSAssets;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * A utility class to build the player information UI box.
 * (MODIFIED) Gun is now an ImageView with a background.
 */
public class PlayerInfoBuilder {
    // (MODIFIED) Load both images
    static Image bulletImage = CSAssets.sprites.get("item/Bullet-Tiles-01.png");
    static Image ak47Image = CSAssets.sprites.get("item/AK47-Tiles-01.png");

    // (FIX) Must be public static so GameScreen can read it for health bar math
    public static final double PLAYER_INFO_WIDTH = 400.0;

    /**
     * Creates a player information box (VBox) containing name, health, buffs, and ammo.
     *
     * @param playerTag   "P1" or "P2"
     * @param alignment   The alignment for child elements (e.g., Pos.CENTER_LEFT)
     * @return A {@link PlayerInfoUI} object containing the layout and references to dynamic nodes.
     */
    public static PlayerInfoUI createPlayerInfoBox(String playerTag, Pos alignment) {
        VBox playerBox = new VBox(5);
        playerBox.setAlignment(Pos.CENTER); // (MODIFIED) Align all children center

        // --- Player Name and Tag ---
        boolean isLeft = alignment == Pos.CENTER_LEFT;
        HBox nameBox = new HBox(8);
        nameBox.setAlignment(alignment);
        // (MODIFIED) Set fixed width for balance
        nameBox.setPrefWidth(PLAYER_INFO_WIDTH);
        nameBox.setMaxWidth(PLAYER_INFO_WIDTH);


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
        // (MODIFIED) Set fixed width for balance
        healthStack.setPrefWidth(PLAYER_INFO_WIDTH);
        healthStack.setMaxWidth(PLAYER_INFO_WIDTH);

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
        buffBox.setMaxWidth(Region.USE_PREF_SIZE);
        buffBox.setMaxHeight(Region.USE_PREF_SIZE); // (FIX) Tell buffBox not to stretch

        // --- (MODIFIED) Gun is now an ImageView ---
        ImageView gunImageView = new ImageView(ak47Image);
        gunImageView.setFitHeight(32); // Set height to match buff slots
        gunImageView.setPreserveRatio(true);

        // --- (NEW) Create a container for the gun image to give it a background ---
        StackPane gunImageContainer = new StackPane(gunImageView);
        gunImageContainer.setPadding(new Insets(2)); // Add some padding around the gun
        gunImageContainer.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.3);" +
                        "-fx-background-radius: 3;"
        );
        gunImageContainer.setMaxWidth(Region.USE_PREF_SIZE);
        gunImageContainer.setMaxHeight(Region.USE_PREF_SIZE); // (FIX) Tell gun container not to stretch

        // --- (MODIFIED) Build Ammo Box ---
        HBox ammoBox = new HBox();
        ammoBox.setPadding(new Insets(5));
        ammoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); " +
                "-fx-border-color: white; -fx-border-width: 2; " +
                "-fx-background-radius: 5; -fx-border-radius: 5;");
        ammoBox.setMaxWidth(Region.USE_PREF_SIZE);
        ammoBox.setMaxHeight(Region.USE_PREF_SIZE); // (FIX) Tell ammoBox not to stretch

        // Details (Right) - VBox
        VBox ammoDetailsVBox = new VBox(2);
        ammoDetailsVBox.setPadding(new Insets(2));
        ammoDetailsVBox.setAlignment(isLeft ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        // Bullet Count + Icon
        HBox bulletBox = new HBox(4);
        bulletBox.setPadding(new Insets(2, 5, 2, 5));
        bulletBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.4); " +
                "-fx-border-color: white;"
        );
        bulletBox.setAlignment(isLeft ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        Label ammoLabel = new Label("30 / 30"); // Placeholder
        ammoLabel.setTextFill(Color.WHITE);
        ammoLabel.setStyle("-fx-font-size: 14px;");

        ImageView bulletIcon = new ImageView(bulletImage);
        bulletIcon.setFitHeight(20);
        bulletIcon.setPreserveRatio(true);

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
        ammoBox.getChildren().add(ammoDetailsVBox);


        // --- (FIX) New 3-Item Row (Gun, Ammo, Buffs) ---
        HBox bottomRow = new HBox(10); // 10px spacing between items
        // (FIX) Set fixed width for balance
        bottomRow.setPrefWidth(PLAYER_INFO_WIDTH);
        bottomRow.setMaxWidth(PLAYER_INFO_WIDTH);
        // (FIX) Set alignment to center children vertically
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        // --- (FIX) Create Spacer to push items apart ---
        Region buffSpacer = new Region();
        HBox.setHgrow(buffSpacer, Priority.ALWAYS);

        // --- (FIX) Create a new HBox for [Gun] + [Ammo] ---
        HBox gunAndAmmoBox = new HBox(5); // 5px spacing between gun and ammo
        gunAndAmmoBox.setAlignment(Pos.CENTER_LEFT);
        gunAndAmmoBox.setMaxHeight(Region.USE_PREF_SIZE); // Don't stretch this new box

        // Add children based on alignment (layout from image_436ae3.png)
        if (isLeft) {
            // Red Team (Left Side): [Gun] [Ammo] ... [Buffs]
            gunAndAmmoBox.getChildren().addAll(gunImageContainer, ammoBox);
            bottomRow.getChildren().addAll(gunAndAmmoBox, buffSpacer, buffBox);
        } else {
            // Blue Team (Right Side): [Buffs] ... [Ammo] [Gun]
            // (Note: Order is reversed for symmetry)
            gunAndAmmoBox.getChildren().addAll(ammoBox, gunImageContainer);
            bottomRow.getChildren().addAll(buffBox, buffSpacer, gunAndAmmoBox);
        }


        // --- (FIXED) Add all to main VBox (3 rows) ---
        playerBox.getChildren().clear(); // Clear previous items
        playerBox.getChildren().addAll(nameBox, healthStack, bottomRow);

        // --- Create and return the wrapper ---
        return new PlayerInfoUI(playerBox, nameLabel, tagLabel, gunImageView, ammoLabel,
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

