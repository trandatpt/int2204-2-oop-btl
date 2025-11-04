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
 * Creates a Solo Game Screen layout with info on the right
 * and the game canvas filling the center/left area.
 */
public class GameScreenSolo extends Screen {
    private ClientArkanoidMatch match;
    private GameRenderCanvas gameRenderCanvas;

    private AnimationTimer gameLoop;
    private long lastTick;
    private long delta = 0;

    // --- UI Node References for SOLO (RIGHT) ---
    private Label scoreValueSolo;
    private HBox heartsSolo;
    private VBox playerBoxSolo;
    private Label levelLabel; // (NEW) For "LEVEL X" text

    // --- Image Assets ---
    private static final Image logoTeam_1 = CSAssets.sprites.__get("logo/logoTeam_1.png");
    private static final Image RIFLE_IMAGE = CSAssets.sprites.__get("item/kalashnikov.png");
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

        // Background
        BackgroundSize mainBgSize = new BackgroundSize(100, 100, true, true, false, true);

        BackgroundSize borderBgSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage mainBg = new BackgroundImage(
                CSAssets.VS_BACKGROUND2, // Using GIF background
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                mainBgSize
        );

        BackgroundImage borderBg = new BackgroundImage(
                CSAssets.BORDER_BG, // Your new border image
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                borderBgSize // Also stretch to fit
        );
        root.setBackground(new Background(mainBg, borderBg));

        // --- Center: Game Canvas (The "left side" of the layout) ---
        if (this.gameRenderCanvas != null) {
            this.gameRenderCanvas.onInit();
            root.setCenter(this.gameRenderCanvas);
            BorderPane.setMargin(this.gameRenderCanvas, new Insets(10, 10, 40, 200)); // Adjusted padding
        }

        // --- TOP PANE (for LEVEL) ---
        levelLabel = new Label("LEVEL 1"); // Placeholder
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Arial'; -fx-text-fill: white; -fx-stroke: red; -fx-stroke-width: 2;");
        levelLabel.setPadding(new Insets(5));

        StackPane levelBox = new StackPane(levelLabel);
        levelBox.setStyle(
                "-fx-background-color: rgba(34, 34, 34, 0.6); " +
                        "-fx-border-color: white; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;"
        );
        levelBox.setMaxWidth(Region.USE_PREF_SIZE);

        StackPane topPane = new StackPane(levelBox);
        topPane.setPadding(new Insets(60, 0, 0, 580));
        topPane.setAlignment(Pos.TOP_LEFT);
        root.setTop(topPane);


        // --- SOLO INFO PANE (RIGHT) ---
        VBox infoPaneSolo = new VBox(10);
        infoPaneSolo.setPrefWidth(400);
        infoPaneSolo.setMaxHeight(400);
        infoPaneSolo.setPadding(new Insets(15));
        infoPaneSolo.setStyle("-fx-background-color: rgba(34,34,34,0.7); -fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        infoPaneSolo.setAlignment(Pos.TOP_CENTER);

        // Team Box
        HBox teamBoxSolo = new HBox(8);
        teamBoxSolo.setAlignment(Pos.TOP_CENTER);
        teamBoxSolo.setPadding(new Insets(5));
        teamBoxSolo.setStyle("-fx-background-color: rgba(34,34,34,0.6); -fx-border-color: white; -fx-border-radius: 10;");

        ImageView logoViewSolo = new ImageView(logoTeam_1);
        logoViewSolo.setFitWidth(40);
        logoViewSolo.setFitHeight(40);
        StackPane colorBoxSolo = new StackPane(logoViewSolo);
        colorBoxSolo.setPrefSize(40, 40);
        Label teamLabelSolo = new Label("SOLO");
        teamLabelSolo.setTextFill(Color.WHITE);
        teamLabelSolo.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        teamBoxSolo.getChildren().addAll(colorBoxSolo, teamLabelSolo);

        Label teamLivesLabelSolo = new Label("Lives");
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
        playerBoxSolo.setAlignment(Pos.CENTER_RIGHT); // Align right
        playerBoxSolo.setId("playerBoxSolo");

        infoPaneSolo.getChildren().addAll(teamBoxSolo, teamLivesLabelSolo, heartsSolo, scoreBoxSolo, playerBoxSolo);
        VBox.setMargin(playerBoxSolo, new Insets(15, 0, 0, 0));

        // Container for padding/alignment
        StackPane rightContainer = new StackPane(infoPaneSolo);
        rightContainer.setPadding(new Insets(0, 200, 100, 200)); // Adjusted padding
        BorderPane.setAlignment(rightContainer, Pos.CENTER);

        root.setRight(rightContainer); // Set to RIGHT
        this.addElement("root", root);

        // --- Game Loop (Fixed Timestep) ---
        lastTick = System.nanoTime();

        final long MAX_DELTA_TIME_NS = Constants.NS_PER_TICK * 5;
        final float fixedTpf = 1.0f / Constants.TICKS_PER_SECOND;

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsedTime = now - lastTick;
                lastTick = now;

                if (elapsedTime > MAX_DELTA_TIME_NS) {
                    elapsedTime = MAX_DELTA_TIME_NS;
                }

                delta += elapsedTime;

                while (delta >= Constants.NS_PER_TICK) {
                    onUpdate(fixedTpf); // <-- Use fixed tpf
                    delta -= Constants.NS_PER_TICK;
                }

                if (gameRenderCanvas != null) {
                    gameRenderCanvas.doRender();
                }
            }
        };
        gameLoop.start();
    }

    public void onUpdate(float tpf) {
        if (match == null) return;

        // (NEW) Update Level Counter (using getRoundIndex())
        String levelText = String.format("LEVEL %d", match.getRoundIndex());
        if (levelLabel != null && !levelLabel.getText().equals(levelText)) {
            levelLabel.setText(levelText);
        }

        if (match.getTeams() == null) return;

        // --- (MODIFIED) Only update one team ---
        // (Assuming RED team for solo, as per last file)
        CTeamInfo soloTeam = match.getTeams().get(TeamColor.RED);
        if (soloTeam != null) {
            updateTeamUI(soloTeam, scoreValueSolo, heartsSolo, playerBoxSolo, Color.RED);
        }
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
                PlayerInfoUI ui = playerUIMap.computeIfAbsent(
                        player.uuid.toString(),
                        // (FIXED) Use "P" as the tag, AND align RIGHT
                        k -> PlayerInfoBuilder.createPlayerInfoBox("P", Pos.CENTER_RIGHT)
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

        String healthText = String.format("%d / %d", playerData.health, Constants.PADDLE_MAX_HEALTH);
        if (!playerUI.getHealthLabel().getText().equals(healthText)) {
            playerUI.getHealthLabel().setText(healthText);
        }

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

