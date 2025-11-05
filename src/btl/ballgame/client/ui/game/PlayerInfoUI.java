package btl.ballgame.client.ui.game;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * A wrapper class that holds both the JavaFX layout (VBox) for a player's info
 * panel and direct references to the dynamic UI nodes (Labels, Regions) inside it.
 * This allows for easy updates from the main GameScreen.
 */
public class PlayerInfoUI {

    private final VBox rootNode;
    private final Label playerName;
    private final Label playerTag;
    private final ImageView gunImageView;
    private final Label ammoCount;
    private final Label firingMode;
    private final Region healthBar;
    private final HBox buffBox;
    private final Label healthLabel;

    // References to the 3 timer labels below the buff icons
    private final Label buffTimer1;
    private final Label buffTimer2;
    private final Label buffTimer3;

    public PlayerInfoUI(VBox rootNode, Label playerName, Label playerTag, ImageView gunImageView, Label ammoCount,
                        Label firingMode, Region healthBar, HBox buffBox, Label healthLabel,
                        Label buffTimer1, Label buffTimer2, Label buffTimer3) {
        this.rootNode = rootNode;
        this.playerName = playerName;
        this.playerTag = playerTag;
        this.gunImageView = gunImageView;
        this.ammoCount = ammoCount;
        this.firingMode = firingMode;
        this.healthBar = healthBar;
        this.buffBox = buffBox;
        this.healthLabel = healthLabel;

        // Assign new timers
        this.buffTimer1 = buffTimer1;
        this.buffTimer2 = buffTimer2;
        this.buffTimer3 = buffTimer3;
    }

    /**
     * @return The root VBox layout for this player's UI.
     */
    public VBox getRootNode() {
        return rootNode;
    }

    public Label getPlayerName() {
        return playerName;
    }

    public Label getPlayerTag() {
        return playerTag;
    }

    /**
     * @return The ImageView displaying the gun's image.
     */
    public ImageView getGunImageView() {
        return gunImageView;
    }

    public Label getAmmoCount() {
        return ammoCount;
    }

    public Label getFiringMode() {
        return firingMode;
    }

    public Region getHealthBar() {
        return healthBar;
    }

    public HBox getBuffBox() {
        return buffBox;
    }

    public Label getHealthLabel() {
        return healthLabel;
    }

    public Label getBuffTimer1() {
        return buffTimer1;
    }

    public Label getBuffTimer2() {
        return buffTimer2;
    }

    public Label getBuffTimer3() {
        return buffTimer3;
    }
}