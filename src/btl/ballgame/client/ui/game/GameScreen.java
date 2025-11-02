package btl.ballgame.client.ui.game;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.ClientArkanoidMatch.CPlayerInfo;
import btl.ballgame.client.ClientArkanoidMatch.CTeamInfo;
// import btl.ballgame.client.net.systems.CSWorld; // (REMOVED)
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.shared.libs.Constants; // (Import Constants)
import btl.ballgame.shared.libs.Constants.TeamColor;
import javafx.animation.AnimationTimer; // (NEW) Import Game Loop
import javafx.geometry.Insets;
import javafx.geometry.Pos;
// import javafx.scene.canvas.Canvas; // (REMOVED)
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class GameScreen extends Screen {
    // private CSWorld world; // (REMOVED)
    private ClientArkanoidMatch match;

    // (NEW) Reference to the actual game renderer
    private GameRenderCanvas gameRenderCanvas;

    // (NEW) The central game loop
    private AnimationTimer gameLoop;
    private long lastTick;

    // --- UI Node References for RED Team (LEFT) ---
    private Label scoreValueLeft;
    private HBox heartsLeft;
    private PlayerInfoUI player1InfoUILeft;
    private PlayerInfoUI player2InfoUILeft;

    // --- UI Node References for BLUE Team (RIGHT) ---
    private Label scoreValueRight;
    private HBox heartsRight;
    private PlayerInfoUI player1InfoUIRight;
    private PlayerInfoUI player2InfoUIRight;

    // --- NEW: UI Node References for Top-Center Scoreboard ---
    private Label roundScoreLabel; // Shows "00 : 00"
    private Label timeLabel;       // Shows "TIME 00:00"

    // --- (REMOVED) Team Data Cache ---
    // This cache caused the UI "freezing" bug.
    // private CTeamInfo redTeam;
    // private CTeamInfo blueTeam;

    static Image logoTeam_1 = CSAssets.sprites.__get("logo/logoTeam_1.png");
    static Image logoTeam_2 = CSAssets.sprites.__get("logo/logoTeam_2.png");
    private static final Image RIFLE_IMAGE = CSAssets.sprites.__get("item/AK47-Tiles-01.png");

    /**
     * (NEW) Constructor now accepts the GameRenderCanvas
     * @param gameRenderCanvas The pre-initialized game renderer
     */
    public GameScreen(GameRenderCanvas gameRenderCanvas) {
        super("game");
        this.gameRenderCanvas = gameRenderCanvas; // (NEW) Store reference
        this.match = ArkanoidGame.core().getActiveMatch();
        // this.world = match.getGameWorld(); // (REMOVED)
    }

    /**
     * (NEW) Call this after setting the screen to ensure
     * the game canvas receives key events.
     */
    public void requestGameFocus() {
        if (gameRenderCanvas != null) {
            gameRenderCanvas.requestFocus();
        }
    }

    @Override
    public void onInit() {
        BorderPane root = new BorderPane();

        // Background setup (KEPT AS REQUESTED)
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                CSAssets.VS_BACKGROUND,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        root.setBackground(new Background(backgroundImage));

        // --- (FIXED) Center canvas ---
        // (REMOVED) Fake StackPane/Canvas
        // (NEW) Add the ACTUAL GameRenderCanvas to the center
        if (this.gameRenderCanvas != null) {
            this.gameRenderCanvas.onInit(); // Init the game canvas
            root.setCenter(this.gameRenderCanvas);
        }

        // --- (NEW) TOP-CENTER SCOREBOARD ---
        VBox topCenterBox = new VBox(-5); // Negative spacing to pull them closer
        topCenterBox.setPrefWidth(600);
        topCenterBox.setAlignment(Pos.CENTER);
        topCenterBox.setPadding(new Insets(10, 0, 0, 0)); // Padding from the top edge

        // Round Score (e.g., "01 : 00")
        roundScoreLabel = new Label("00 : 00");
        roundScoreLabel.setTextFill(Color.WHITE);
        roundScoreLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");

        // Time (e.g., "TIME 02:30")
        timeLabel = new Label("TIME 00:00");
        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");

        topCenterBox.getChildren().addAll(roundScoreLabel, timeLabel);
        root.setTop(topCenterBox);
        BorderPane.setAlignment(topCenterBox, Pos.CENTER);

        // --- (MODIFIED) Adjust Center Pane ---
        // Add a top margin to the canvas pane so it doesn't go under the scoreboard
        if (this.gameRenderCanvas != null) {
            BorderPane.setMargin(this.gameRenderCanvas, new Insets(10, 0, 0, 0));
        }


        // --- (BLUE TEAM - RIGHT) ---
        VBox infoPaneRight = new VBox(10);
        infoPaneRight.setPrefWidth(400);
        infoPaneRight.setMaxHeight(600);
        infoPaneRight.setPadding(new Insets(15));
        infoPaneRight.setStyle("-fx-background-color: rgba(34, 34, 34, 0.4); " +
                "-fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        infoPaneRight.setAlignment(Pos.TOP_CENTER);

        // Team Label Box
        HBox teamBoxRight = new HBox(8);
        teamBoxRight.setAlignment(Pos.TOP_CENTER);
        teamBoxRight.setStyle("-fx-background-color: rgba(34, 34, 34, 0.6); " +
                "-fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        teamBoxRight.setPadding(new Insets(5));

        // Team Color Box and Label
        ImageView logoViewRight = new ImageView(logoTeam_1);
        logoViewRight.setFitWidth(40);
        logoViewRight.setFitHeight(40);
        logoViewRight.setPreserveRatio(true);
        StackPane colorBoxRight = new StackPane(logoViewRight);
        colorBoxRight.setPrefSize(40, 40);
        colorBoxRight.setStyle("-fx-background-color: rgba(255, 255, 255, 0.4); -fx-border-color: white;");
        Label teamLabelRight = new Label("BLUE");
        teamLabelRight.setTextFill(Color.BLUE);
        teamLabelRight.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        teamBoxRight.getChildren().addAll(colorBoxRight, teamLabelRight);

        // Team Lives Label
        Label teamLivesLabelRight = new Label("Team Lives");
        teamLivesLabelRight.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        // Hearts Container
        heartsRight = new HBox(5);
        heartsRight.setAlignment(Pos.TOP_CENTER);

        // Score Box
        HBox scoreBoxRight = new HBox(10);
        scoreBoxRight.setAlignment(Pos.CENTER);
        Label scoreLabelRight = new Label("Score:");
        scoreLabelRight.setTextFill(Color.WHITE);
        scoreLabelRight.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        scoreValueRight = new Label("0000000000000000");
        scoreValueRight.setTextFill(Color.WHITE);
        scoreValueRight.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");
        scoreBoxRight.getChildren().addAll(scoreLabelRight, scoreValueRight);

        // Player Info UIs
        player1InfoUIRight = PlayerInfoBuilder.createPlayerInfoBox("P1", Pos.CENTER_RIGHT);
        player2InfoUIRight = PlayerInfoBuilder.createPlayerInfoBox("P2", Pos.CENTER_RIGHT);

        infoPaneRight.getChildren().addAll(
                teamBoxRight,
                teamLivesLabelRight,
                heartsRight,
                scoreBoxRight,
                player1InfoUIRight.getRootNode(),
                player2InfoUIRight.getRootNode()
        );
        VBox.setMargin(player1InfoUIRight.getRootNode(), new Insets(15, 0, 0, 0));
        VBox.setMargin(player2InfoUIRight.getRootNode(), new Insets(10, 0, 0, 0));

        StackPane rightContainer = new StackPane(infoPaneRight);
        rightContainer.setPadding(new Insets(0, 100, 0, 100));
        // (FIX) Align the container vertically center
        BorderPane.setAlignment(rightContainer, Pos.CENTER);


        // --- (RED TEAM - LEFT) ---
        VBox infoPaneLeft = new VBox(10);
        infoPaneLeft.setPrefWidth(400);
        infoPaneLeft.setMaxHeight(600);
        infoPaneLeft.setPadding(new Insets(15));
        infoPaneLeft.setStyle("-fx-background-color: rgba(34, 34, 34, 0.4); " +
                "-fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        infoPaneLeft.setAlignment(Pos.TOP_CENTER);

        // Team Label Box
        HBox teamBoxLeft = new HBox(8);
        teamBoxLeft.setAlignment(Pos.TOP_CENTER);
        teamBoxLeft.setStyle("-fx-background-color: rgba(34, 34, 34, 0.6); " +
                "-fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        teamBoxLeft.setPadding(new Insets(5));

        // Team Color Box and Label
        ImageView logoViewLeft = new ImageView(logoTeam_2);
        logoViewLeft.setFitWidth(40);
        logoViewLeft.setFitHeight(40);
        logoViewLeft.setPreserveRatio(true);
        StackPane colorBoxLeft = new StackPane(logoViewLeft);
        colorBoxLeft.setPrefSize(40, 40);
        colorBoxLeft.setStyle("-fx-background-color: rgba(255, 255, 255, 0.4); -fx-border-color: white;");
        Label teamLabelLeft = new Label("RED");
        teamLabelLeft.setTextFill(Color.RED);
        teamLabelLeft.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        teamBoxLeft.getChildren().addAll(colorBoxLeft, teamLabelLeft);

        // Team Lives Label
        Label teamLivesLabelLeft = new Label("Team Lives");
        teamLivesLabelLeft.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        // Hearts Container
        heartsLeft = new HBox(5);
        heartsLeft.setAlignment(Pos.TOP_CENTER);

        // Score Box
        HBox scoreBoxLeft = new HBox(10);
        scoreBoxLeft.setAlignment(Pos.CENTER);
        Label scoreLabelLeft = new Label("Score:");
        scoreLabelLeft.setTextFill(Color.WHITE);
        scoreLabelLeft.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        scoreValueLeft = new Label("0000000000000000");
        scoreValueLeft.setTextFill(Color.WHITE);
        scoreValueLeft.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");
        scoreBoxLeft.getChildren().addAll(scoreLabelLeft, scoreValueLeft);

        // Player Info UIs
        player1InfoUILeft = PlayerInfoBuilder.createPlayerInfoBox("P1", Pos.CENTER_LEFT);
        player2InfoUILeft = PlayerInfoBuilder.createPlayerInfoBox("P2", Pos.CENTER_LEFT);

        infoPaneLeft.getChildren().addAll(
                teamBoxLeft,
                teamLivesLabelLeft,
                heartsLeft,
                scoreBoxLeft,
                player1InfoUILeft.getRootNode(),
                player2InfoUILeft.getRootNode()
        );
        VBox.setMargin(player1InfoUILeft.getRootNode(), new Insets(15, 0, 0, 0));
        VBox.setMargin(player2InfoUILeft.getRootNode(), new Insets(10, 0, 0, 0));

        StackPane leftContainer = new StackPane(infoPaneLeft);
        leftContainer.setPadding(new Insets(0, 100, 0, 100));
        // (FIX) Align the container vertically center
        BorderPane.setAlignment(leftContainer, Pos.CENTER);


        root.setRight(rightContainer);
        root.setLeft(leftContainer);
        this.addElement("root", root);

        // --- (NEW) Create the central game loop ---
        lastTick = System.nanoTime();
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Calculate time-per-frame (tpf)
                float tpf = (float) ((now - lastTick) / 1_000_000_000.0);
                lastTick = now;

                // 1. Update this UI (GameScreen)
                onUpdate(tpf);

                // 2. Render the Game (GameRenderCanvas)
                if (gameRenderCanvas != null) {
                    gameRenderCanvas.doRender();
                }
            }
        };
        gameLoop.start();
    }

    /**
     * This method is called every game tick or frame.
     * It reads data from the ClientArkanoidMatch and updates the UI nodes.
     * @param tpf Time per frame (not used here, but good practice)
     */
    // (FIXED) Removed @Override
    public void onUpdate(float tpf) {
        if (match == null || match.getTeams() == null) {
            return; // No data yet
        }

        // Fetch team info
        CTeamInfo newRedTeam = match.getTeams().get(TeamColor.RED);
        CTeamInfo newBlueTeam = match.getTeams().get(TeamColor.BLUE);

        // Update Red Team UI (Left)
        // (FIXED) Removed 'if (this.redTeam != newRedTeam)'
        if (newRedTeam != null) {
            updateTeamUI(newRedTeam, scoreValueLeft, heartsLeft, player1InfoUILeft, player2InfoUILeft, Color.RED);
            // this.redTeam = newRedTeam; // (REMOVED)
        }

        // Update Blue Team UI (Right)
        // (FIXED) Removed 'if (this.blueTeam != newBlueTeam)'
        if (newBlueTeam != null) {
            updateTeamUI(newBlueTeam, scoreValueRight, heartsRight, player1InfoUIRight, player2InfoUIRight, Color.BLUE);
            // this.blueTeam = newBlueTeam; // (REMOVED)
        }

        // --- (NEW) Update Top-Center Scoreboard ---
        if (newRedTeam != null && newBlueTeam != null) {
            // (CONNECTED) ftScore
            String roundScoreText = String.format("%02d : %02d", newRedTeam.ftScore, newBlueTeam.ftScore);
            if (!roundScoreLabel.getText().equals(roundScoreText)) {
                roundScoreLabel.setText(roundScoreText);
            }
        }

        // TODO: Update timeLabel when time data is available
        // timeLabel.setText(String.format("TIME %02d:%02d", minutes, seconds));
    }

    /**
     * Helper method to update all UI components for a single team.
     */
    private void updateTeamUI(CTeamInfo teamData, Label scoreLabel, HBox heartsBox,
                              PlayerInfoUI p1ui, PlayerInfoUI p2ui, Color teamColor) {

        // (CONNECTED) arkScore
        String scoreText = String.format("%016d", teamData.arkScore);
        if (!scoreLabel.getText().equals(scoreText)) {
            scoreLabel.setText(scoreText);
        }

        // (CONNECTED) livesRemaining
        if (heartsBox.getChildren().size() != teamData.livesRemaining) {
            heartsBox.getChildren().clear();
            for (int i = 0; i < teamData.livesRemaining; i++) {
                // (FIXED) Correct heart symbol
                Label heart = new Label("❤");
                heart.setStyle("-fx-font-size: 24px;");
                heart.setTextFill(teamColor);
                heartsBox.getChildren().add(heart);
            }
        }

        // Update players
        if (teamData.players != null) {
            if (teamData.players.length > 0) {
                updatePlayerUI(p1ui, teamData.players[0]);
            }
            if (teamData.players.length > 1) {
                updatePlayerUI(p2ui, teamData.players[1]);
            }
        }
    }

    /**
     * Helper method to update a single player's UI panel.
     */
    private void updatePlayerUI(PlayerInfoUI playerUI, CPlayerInfo playerData) {
        if (playerUI == null || playerData == null) {
            return;
        }

        // (CONNECTED) Name
        if (playerData.getName() != null && !playerUI.getPlayerName().getText().equals(playerData.getName())) {
            playerUI.getPlayerName().setText(playerData.getName());
        }

        // (CONNECTED) health
        // Calculate health percentage based on PADDLE_MAX_HEALTH
        // (FIXED) Use the PlayerInfoBuilder constant
        double healthPercent = (double)playerData.health / (double)Constants.PADDLE_MAX_HEALTH;
        double barWidth = PlayerInfoBuilder.PLAYER_INFO_WIDTH * healthPercent;
        playerUI.getHealthBar().setPrefWidth(barWidth);


        // TODO: Add shield data
        // playerUI.getShieldBar().setPrefWidth(0); // Hide shield for now

        // (CONNECTED) Gun Image
        // TODO: This logic needs to be expanded when you have more guns
        Image currentGunImage = RIFLE_IMAGE; // Default to RIFLE
        if (playerUI.getGunImageView().getImage() != currentGunImage) {
            playerUI.getGunImageView().setImage(currentGunImage);
        }

        // (CONNECTED) bulletsLeft
        // Use AK_47_MAG_SIZE from Constants
        String ammoText = String.format("%d / %d",
                playerData.bulletsLeft,
                Constants.AK_47_MAG_SIZE);
        if (!playerUI.getAmmoCount().getText().equals(ammoText)) {
            playerUI.getAmmoCount().setText(ammoText);
        }

        // (CONNECTED) firingMode
        if (playerData.firingMode != null) {
            String fireModeText = playerData.firingMode.name();
            if (!playerUI.getFiringMode().getText().equals(fireModeText)) {
                playerUI.getFiringMode().setText(fireModeText);
            }
        }

        // TODO: Update Buffs
    }

    @Override
    public void onRemove() {
        // (NEW) Stop the game loop
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
}

