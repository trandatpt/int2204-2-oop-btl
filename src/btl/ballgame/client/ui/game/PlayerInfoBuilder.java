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
    static Image bulletImage = CSAssets.sprites.__get("item/Bullet-Tiles-01.png");
    static Image ak47Image = CSAssets.sprites.__get("item/kalashnikov.png");

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
        playerBox.setAlignment(Pos.CENTER);

        // --- Player Name and Tag ---
        boolean isLeft = alignment == Pos.CENTER_LEFT;
        HBox nameBox = new HBox(8);
        nameBox.setAlignment(alignment);
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

        // --- Health Bars ---
        StackPane healthStack = new StackPane();
        healthStack.setPrefHeight(20);
        healthStack.setMaxHeight(20);
        healthStack.setPrefWidth(PLAYER_INFO_WIDTH);
        healthStack.setMaxWidth(PLAYER_INFO_WIDTH);
        healthStack.setStyle("-fx-background-color: #555; -fx-background-radius: 5;"); // (MODIFIED) Health background is now gray

        Region healthBar = new Region();
        healthBar.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 5;");

        // (REMOVED) Shield Bar

        Label healthLabel = new Label("100/100"); // (NEW) HP text label
        healthLabel.setTextFill(Color.WHITE);
        healthLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // (MODIFIED) Order of layers: healthBg (gray) -> healthBar (red) -> healthLabel (text)
        healthStack.getChildren().addAll(healthBar, healthLabel); // (MODIFIED) Removed shieldBar
        StackPane.setAlignment(healthBar, Pos.CENTER_LEFT); // (MODIFIED) Health bar always aligns left
        StackPane.setAlignment(healthLabel, Pos.CENTER); // (MODIFIED) HP text always in center

        // --- Buffs ---
        // (NEW) Create buff timers (placeholders)
        Label buffTimer1 = createBuffTimerLabel();
        Label buffTimer2 = createBuffTimerLabel();
        Label buffTimer3 = createBuffTimerLabel();

        // (NEW) Create buff slots (VBox containing icon + timer)
        VBox buffSlot1 = createBuffSlot(buffTimer1);
        VBox buffSlot2 = createBuffSlot(buffTimer2);
        VBox buffSlot3 = createBuffSlot(buffTimer3);

        // (NEW) Create the HBox for buffs
        HBox buffBox = new HBox(5); // 5px spacing between VBoxes
        buffBox.getChildren().addAll(buffSlot1, buffSlot2, buffSlot3);
        buffBox.setMaxWidth(Region.USE_PREF_SIZE);
        buffBox.setMaxHeight(Region.USE_PREF_SIZE);

        // --- Gun ---
        ImageView gunImageView = new ImageView(ak47Image);
        gunImageView.setFitHeight(40); // Set height to match buff slots
        gunImageView.setPreserveRatio(true);

        // --- A container for the gun image to give it a background ---
        StackPane gunImageContainer = new StackPane(gunImageView);
        gunImageContainer.setPadding(new Insets(2));
        gunImageContainer.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.3);" +
                        "-fx-background-radius: 3;"
        );
        gunImageContainer.setMaxWidth(Region.USE_PREF_SIZE);
        //gunImageContainer.setMaxHeight(Region.USE_PREF_SIZE);

        // --- Build Ammo Box ---
        HBox ammoBox = new HBox();
        ammoBox.setPadding(new Insets(5));
        ammoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); " +
                "-fx-border-color: white; -fx-border-width: 2; " +
                "-fx-background-radius: 5; -fx-border-radius: 5;");
        ammoBox.setMaxWidth(Region.USE_PREF_SIZE);
        ammoBox.setMaxHeight(Region.USE_PREF_SIZE);

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


        // --- New 3-Item Row (Gun, Ammo, Buffs) ---
        HBox bottomRow = new HBox(10);
        bottomRow.setPrefWidth(PLAYER_INFO_WIDTH);
        bottomRow.setMaxWidth(PLAYER_INFO_WIDTH);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        // --- Create Spacer to push items apart ---
        Region buffSpacer = new Region();
        HBox.setHgrow(buffSpacer, Priority.ALWAYS);

        // --- Create a new HBox for [Gun] + [Ammo] ---
        HBox gunAndAmmoBox = new HBox(5);
        gunAndAmmoBox.setAlignment(Pos.CENTER_LEFT);
        gunAndAmmoBox.setMaxHeight(Region.USE_PREF_SIZE);

        // Add children based on alignment
        if (isLeft) {
            // Red Team (Left Side): [Gun] [Ammo] ... [Buffs]
            gunAndAmmoBox.getChildren().addAll(gunImageContainer, ammoBox);
            bottomRow.getChildren().addAll(gunAndAmmoBox, buffSpacer, buffBox);
        } else {
            // Blue Team (Right Side): [Buffs] ... [Ammo] [Gun]
            gunAndAmmoBox.getChildren().addAll(ammoBox, gunImageContainer);
            bottomRow.getChildren().addAll(buffBox, buffSpacer, gunAndAmmoBox);
        }


        // --- Add all to main VBox (3 rows) ---
        playerBox.getChildren().clear(); // Clear previous items
        playerBox.getChildren().addAll(nameBox, healthStack, bottomRow);

        // --- Create and return the wrapper ---
        // (MODIFIED) Removed shieldBar, added 3 buff timers
        return new PlayerInfoUI(playerBox, nameLabel, tagLabel, gunImageView, ammoLabel,
                fireModeLabel, healthBar, buffBox, healthLabel,
                buffTimer1, buffTimer2, buffTimer3);
    }

    /**
     * (REMOVED) The old createBuffBox method is gone.
     */
    // private static HBox createBuffBox() { ... }

    /**
     * (NEW) Helper method to create a single buff slot (Icon + Timer).
     * @param timerLabel The label to place under the icon.
     * @return A VBox layout for one buff slot.
     */
    private static VBox createBuffSlot(Label timerLabel) {
        VBox slotVBox = new VBox(2); // 2px spacing between icon and timer
        slotVBox.setAlignment(Pos.CENTER);

        // This is the buff icon slot
        StackPane buffIcon = new StackPane();
        buffIcon.setPrefSize(32, 32);
        buffIcon.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); " +
                "-fx-border-color: gray; -fx-border-radius: 3; -fx-background-radius: 3;");

        slotVBox.getChildren().addAll(buffIcon, timerLabel);
        return slotVBox;
    }

    /**
     * (NEW) Helper method to create and style a buff timer label.
     */
    private static Label createBuffTimerLabel() {
        Label timerLabel = new Label("3s"); // Placeholder text
        timerLabel.setTextFill(Color.WHITE);
        timerLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        timerLabel.setVisible(true); // Hide by default, show when buff is active
        return timerLabel;
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