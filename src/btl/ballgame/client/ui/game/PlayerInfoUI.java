package btl.ballgame.client.ui.game;

import javafx.scene.control.Label;
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
    private final Label gunLabel; // Added this
    private final Label ammoCount;
    private final Label firingMode;
    private final Region healthBar;
    private final Region shieldBar;
    private final HBox buffBox;

    public PlayerInfoUI(VBox rootNode, Label playerName, Label playerTag, Label gunLabel, Label ammoCount,
                        Label firingMode, Region healthBar, Region shieldBar, HBox buffBox) {
        this.rootNode = rootNode;
        this.playerName = playerName;
        this.playerTag = playerTag;
        this.gunLabel = gunLabel; // Added this
        this.ammoCount = ammoCount;
        this.firingMode = firingMode;
        this.healthBar = healthBar;
        this.shieldBar = shieldBar;
        this.buffBox = buffBox;
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
     * @return The Label displaying the gun's name (e.g., "AK-47").
     */
    public Label getGunLabel() {
        return gunLabel; // Added this
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

    public Region getShieldBar() {
        return shieldBar;
    }

    public HBox getBuffBox() {
        return buffBox;
    }
}

