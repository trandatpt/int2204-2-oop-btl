package btl.ballgame.client.ui.game;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.ClientArkanoidMatch.CPlayerInfo;
import btl.ballgame.client.ClientArkanoidMatch.CTeamInfo;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.TeamColor;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a Solo Game Screen layout with info on the left
 * and the game canvas filling the center/right area.
 * Based on GameScreen.java and the user's solo layout image.
 */
public class GameScreenSolo extends Screen {
    private ClientArkanoidMatch match;
    private GameRenderCanvas gameRenderCanvas;

    private AnimationTimer gameLoop;
    private long lastTick;

    // --- UI Node References for SOLO (LEFT) ---
    private Label scoreValueSolo;
    private HBox heartsSolo;
    private VBox playerBoxSolo; // NEW: dynamic player container

    // --- Image Assets ---
    // Using logoTeam_1 (Blue) as seen in the solo layout image
    private static final Image logoTeam_1 = CSAssets.sprites.__get("logo/logoTeam_1.png");
    private static final Image RIFLE_IMAGE = CSAssets.sprites.__get("item/AK47-Tiles-01.png");
    private static final Image heartImage = CSAssets.sprites.__get("item/Heart-Tiles-01.png");

    // Map of player UUID to PlayerInfoUI for reuse
    private final Map<String, PlayerInfoUI> playerUIMap = new HashMap<>();

    public GameScreenSolo(GameRenderCanvas gameRenderCanvas) {
        super("game");
        this.gameRenderCanvas = gameRenderCanvas;
        this.match = ArkanoidGame.core().getActiveMatch();
    }

    public void requestGameFocus() {
        if (gameRenderCanvas != null) {
            gameRenderCanvas.requestFocus();
        }
    }

    @Override
    public void onInit() {
        BorderPane root = new BorderPane();

        // Background (copied from GameScreen)
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                CSAssets.VS_BACKGROUND,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        root.setBackground(new Background(backgroundImage));

        // --- Center: Game Canvas (The "right side" of the layout) ---
        if (this.gameRenderCanvas != null) {
            this.gameRenderCanvas.onInit();
            root.setCenter(this.gameRenderCanvas);
            // Add margin to push it away from the left panel and top
            BorderPane.setMargin(this.gameRenderCanvas, new Insets(10, 20, 10, 10));
        }


        // --- SOLO INFO PANE ---
        VBox infoPaneSolo = new VBox(10);
        infoPaneSolo.setPrefWidth(400); // Same width as GameScreen
        infoPaneSolo.setMaxHeight(600); // Same height
        infoPaneSolo.setPadding(new Insets(15));
        infoPaneSolo.setStyle("-fx-background-color: rgba(34,34,34,0.4); -fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        infoPaneSolo.setAlignment(Pos.TOP_CENTER);

        // Team Box (Using Blue logo from image, but "SOLO" text)
        HBox teamBoxSolo = new HBox(8);
        teamBoxSolo.setAlignment(Pos.TOP_CENTER);
        teamBoxSolo.setPadding(new Insets(5));
        teamBoxSolo.setStyle("-fx-background-color: rgba(34,34,34,0.6); -fx-border-color: white; -fx-border-radius: 10;");

        ImageView logoViewSolo = new ImageView(logoTeam_1); // Blue logo
        logoViewSolo.setFitWidth(40);
        logoViewSolo.setFitHeight(40);
        StackPane colorBoxSolo = new StackPane(logoViewSolo);
        colorBoxSolo.setPrefSize(40, 40);
        Label teamLabelSolo = new Label("SOLO"); // Changed text
        teamLabelSolo.setTextFill(Color.WHITE); // Changed color
        teamLabelSolo.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        teamBoxSolo.getChildren().addAll(colorBoxSolo, teamLabelSolo);

        Label teamLivesLabelSolo = new Label("Lives"); // Changed text
        teamLivesLabelSolo.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        heartsSolo = new HBox(5);
        heartsSolo.setAlignment(Pos.TOP_CENTER);

        HBox scoreBoxSolo = new HBox(10);
        scoreBoxSolo.setAlignment(Pos.CENTER);
        Label scoreLabelSolo = new Label("Score:");
        scoreLabelSolo.setTextFill(Color.WHITE);
        scoreLabelSolo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        scoreValueSolo = new Label("0000000000000000");
        scoreValueSolo.setTextFill(Color.WHITE);
        scoreValueSolo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");
        scoreBoxSolo.getChildren().addAll(scoreLabelSolo, scoreValueSolo);

        playerBoxSolo = new VBox(10);
        playerBoxSolo.setAlignment(Pos.CENTER_LEFT); // Solo is on the left
        playerBoxSolo.setId("playerBoxSolo");

        infoPaneSolo.getChildren().addAll(teamBoxSolo, teamLivesLabelSolo, heartsSolo, scoreBoxSolo, playerBoxSolo);
        VBox.setMargin(playerBoxSolo, new Insets(15, 0, 0, 0));

        // Container for padding/alignment (copied from GameScreen)
        StackPane leftContainer = new StackPane(infoPaneSolo);
        leftContainer.setPadding(new Insets(0, 10, 0, 20)); // Adjusted padding
        BorderPane.setAlignment(leftContainer, Pos.CENTER);

        root.setLeft(leftContainer);
        this.addElement("root", root);

        // --- Game Loop (copied from GameScreen) ---
        lastTick = System.nanoTime();
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                float tpf = (float) ((now - lastTick) / 1_000_000_000.0);
                lastTick = now;
                onUpdate(tpf);
                if (gameRenderCanvas != null) {
                    gameRenderCanvas.doRender();
                }
            }
        };
        gameLoop.start();
    }

    public void onUpdate(float tpf) {
        if (match == null || match.getTeams() == null) return;

        // --- (MODIFIED) Only update one team ---

        CTeamInfo soloTeam = match.getTeams().get(TeamColor.BLUE);
        if (soloTeam != null) {
            updateTeamUI(soloTeam, scoreValueSolo, heartsSolo, playerBoxSolo, Color.BLUE);
        }

        // --- NO Top Scoreboard update ---
    }

    /**
     * (Copied from GameScreen)
     * Helper method to update all UI components for a single team.
     */
    private void updateTeamUI(CTeamInfo teamData, Label scoreLabel, HBox heartsBox, VBox playerBox, Color teamColor) {
        if (teamData == null) return;

        String scoreText = String.format("%016d", teamData.arkScore);
        if (!scoreLabel.getText().equals(scoreText)) scoreLabel.setText(scoreText);

        if (heartsBox.getChildren().size() != teamData.livesRemaining) {
            heartsBox.getChildren().clear();
            for (int i = 0; i < teamData.livesRemaining; i++) {
                ImageView heartView = new ImageView(heartImage);
                heartView.setFitWidth(24);
                heartView.setFitHeight(24);
                heartView.setPreserveRatio(true);
                heartsBox.getChildren().add(heartView);
            }
        }

        playerBox.getChildren().clear();
        if (teamData.players != null) {
            for (CPlayerInfo player : teamData.players) {
                // (MODIFIED) Always align left for solo
                PlayerInfoUI ui = playerUIMap.computeIfAbsent(
                        player.uuid.toString(),
                        k -> PlayerInfoBuilder.createPlayerInfoBox(player.getName(), Pos.CENTER_LEFT)
                );
                updatePlayerUI(ui, player);
                playerBox.getChildren().add(ui.getRootNode());
            }
        }
    }

    /**
     * (Copied from GameScreen)
     * Helper method to update a single player's UI panel.
     */
    private void updatePlayerUI(PlayerInfoUI playerUI, CPlayerInfo playerData) {
        if (playerUI == null || playerData == null) return;

        if (playerData.getName() != null && !playerUI.getPlayerName().getText().equals(playerData.getName())) {
            playerUI.getPlayerName().setText(playerData.getName());
        }

        double healthPercent = (double) playerData.health / (double) Constants.PADDLE_MAX_HEALTH;
        playerUI.getHealthBar().setPrefWidth(PlayerInfoBuilder.PLAYER_INFO_WIDTH * healthPercent);

        Image currentGunImage = RIFLE_IMAGE;
        if (playerUI.getGunImageView().getImage() != currentGunImage) {
            playerUI.getGunImageView().setImage(currentGunImage);
        }

        String ammoText = String.format("%d / %d", playerData.bulletsLeft, Constants.AK_47_MAG_SIZE);
        if (!playerUI.getAmmoCount().getText().equals(ammoText)) {
            playerUI.getAmmoCount().setText(ammoText);
        }

        if (playerData.firingMode != null) {
            String fireModeText = playerData.firingMode.name();
            if (!playerUI.getFiringMode().getText().equals(fireModeText)) {
                playerUI.getFiringMode().setText(fireModeText);
            }
        }
    }

    @Override
    public void onRemove() {
        if (gameLoop != null) gameLoop.stop();
    }
}
