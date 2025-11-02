package btl.ballgame.client.ui.game;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.ClientArkanoidMatch.CPlayerInfo;
import btl.ballgame.client.ClientArkanoidMatch.CTeamInfo;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.shared.libs.Constants.TeamColor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class GameScreen extends Screen {
    private CSWorld world;
    private ClientArkanoidMatch match;

    // --- UI Node References for RED Team (LEFT) ---
    private Label scoreValue_L;
    private HBox hearts_L;
    private PlayerInfoUI player1InfoUI_L;
    private PlayerInfoUI player2InfoUI_L;

    // --- UI Node References for BLUE Team (RIGHT) ---
    private Label scoreValue_R;
    private HBox hearts_R;
    private PlayerInfoUI player1InfoUI_R;
    private PlayerInfoUI player2InfoUI_R;

    // --- NEW: UI Node References for Top-Center Scoreboard ---
    private Label roundScoreLabel; // Shows "00 : 00"
    private Label timeLabel;       // Shows "TIME 00:00"

    // --- Team Data Cache (to avoid re-creating nodes) ---
    private CTeamInfo redTeam;
    private CTeamInfo blueTeam;

    static Image logoTeam_1 = CSAssets.sprites.get("logo/logoTeam_1.png");
    static Image logoTeam_2 = CSAssets.sprites.get("logo/logoTeam_2.png");
    private static final Image RIFLE_IMAGE = CSAssets.sprites.get("item/AK47-Tiles-01.png");

    public GameScreen() {
        super("game");
        this.match = ArkanoidGame.core().getActiveMatch();
        this.world = match.getGameWorld();
    }

    @Override
    public void onInit() {
        BorderPane root = new BorderPane();

        // Background setup
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                CSAssets.VS_BACKGROUND,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        root.setBackground(new Background(backgroundImage));

        // Center canvas
        StackPane centerPane = new StackPane(new Canvas(world.getWidth(), world.getHeight()));
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setStyle("-fx-background-color: transparent;");
        root.setCenter(centerPane);

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
        BorderPane.setMargin(centerPane, new Insets(10, 0, 0, 0));


        // --- (BLUE TEAM - RIGHT) ---
        VBox infoPane_R = new VBox(10);
        infoPane_R.setPrefWidth(400);
        infoPane_R.setMaxHeight(600);
        infoPane_R.setPadding(new Insets(15));
        infoPane_R.setStyle("-fx-background-color: rgba(34, 34, 34, 0.4); " +
                "-fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        infoPane_R.setAlignment(Pos.TOP_CENTER);

        // Team Label Box
        HBox teamBox_R = new HBox(8);
        teamBox_R.setAlignment(Pos.TOP_CENTER);
        teamBox_R.setStyle("-fx-background-color: rgba(34, 34, 34, 0.6); " +
                "-fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        teamBox_R.setPadding(new Insets(5));

        // Team Color Box and Label
        ImageView logoView_R = new ImageView(logoTeam_1);
        logoView_R.setFitWidth(40);
        logoView_R.setFitHeight(40);
        logoView_R.setPreserveRatio(true);
        StackPane colorBox_R = new StackPane(logoView_R);
        colorBox_R.setPrefSize(40, 40);
        colorBox_R.setStyle("-fx-background-color: rgba(255, 255, 255, 0.4); -fx-border-color: white;");
        Label teamLabel_R = new Label("BLUE");
        teamLabel_R.setTextFill(Color.BLUE);
        teamLabel_R.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        teamBox_R.getChildren().addAll(colorBox_R, teamLabel_R);

        // Team Lives Label
        Label teamLivesLabel_R = new Label("Team Lives");
        teamLivesLabel_R.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        // Hearts Container
        hearts_R = new HBox(5);
        hearts_R.setAlignment(Pos.TOP_CENTER);

        // Score Box
        HBox scoreBox_R = new HBox(10);
        scoreBox_R.setAlignment(Pos.CENTER);
        Label scoreLabel_R = new Label("Score:");
        scoreLabel_R.setTextFill(Color.WHITE);
        scoreLabel_R.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        scoreValue_R = new Label("0000000000000000");
        scoreValue_R.setTextFill(Color.WHITE);
        scoreValue_R.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");
        scoreBox_R.getChildren().addAll(scoreLabel_R, scoreValue_R);

        // Player Info UIs
        player1InfoUI_R = PlayerInfoBuilder.createPlayerInfoBox("P1", Pos.CENTER_RIGHT);
        player2InfoUI_R = PlayerInfoBuilder.createPlayerInfoBox("P2", Pos.CENTER_RIGHT);

        infoPane_R.getChildren().addAll(
                teamBox_R,
                teamLivesLabel_R,
                hearts_R,
                scoreBox_R,
                player1InfoUI_R.getRootNode(),
                player2InfoUI_R.getRootNode()
        );
        VBox.setMargin(player1InfoUI_R.getRootNode(), new Insets(15, 0, 0, 0));
        VBox.setMargin(player2InfoUI_R.getRootNode(), new Insets(10, 0, 0, 0));

        StackPane rightContainer = new StackPane(infoPane_R);
        rightContainer.setPadding(new Insets(0, 100, 0, 100));


        // --- (RED TEAM - LEFT) ---
        VBox infoPane_L = new VBox(10);
        infoPane_L.setPrefWidth(400);
        infoPane_L.setMaxHeight(600);
        infoPane_L.setPadding(new Insets(15));
        infoPane_L.setStyle("-fx-background-color: rgba(34, 34, 34, 0.4); " +
                "-fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        infoPane_L.setAlignment(Pos.TOP_CENTER);

        // Team Label Box
        HBox teamBox_L = new HBox(8);
        teamBox_L.setAlignment(Pos.TOP_CENTER);
        teamBox_L.setStyle("-fx-background-color: rgba(34, 34, 34, 0.6); " +
                "-fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        teamBox_L.setPadding(new Insets(5));

        // Team Color Box and Label
        ImageView logoView_L = new ImageView(logoTeam_2);
        logoView_L.setFitWidth(40);
        logoView_L.setFitHeight(40);
        logoView_L.setPreserveRatio(true);
        StackPane colorBox_L = new StackPane(logoView_L);
        colorBox_L.setPrefSize(40, 40);
        colorBox_L.setStyle("-fx-background-color: rgba(255, 255, 255, 0.4); -fx-border-color: white;");
        Label teamLabel_L = new Label("RED");
        teamLabel_L.setTextFill(Color.RED);
        teamLabel_L.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        teamBox_L.getChildren().addAll(colorBox_L, teamLabel_L);

        // Team Lives Label
        Label teamLivesLabel_L = new Label("Team Lives");
        teamLivesLabel_L.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        // Hearts Container
        hearts_L = new HBox(5);
        hearts_L.setAlignment(Pos.TOP_CENTER);

        // Score Box
        HBox scoreBox_L = new HBox(10);
        scoreBox_L.setAlignment(Pos.CENTER);
        Label scoreLabel_L = new Label("Score:");
        scoreLabel_L.setTextFill(Color.WHITE);
        scoreLabel_L.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        scoreValue_L = new Label("0000000000000000");
        scoreValue_L.setTextFill(Color.WHITE);
        scoreValue_L.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");
        scoreBox_L.getChildren().addAll(scoreLabel_L, scoreValue_L);

        // Player Info UIs
        player1InfoUI_L = PlayerInfoBuilder.createPlayerInfoBox("P1", Pos.CENTER_LEFT);
        player2InfoUI_L = PlayerInfoBuilder.createPlayerInfoBox("P2", Pos.CENTER_LEFT);

        infoPane_L.getChildren().addAll(
                teamBox_L,
                teamLivesLabel_L,
                hearts_L,
                scoreBox_L,
                player1InfoUI_L.getRootNode(),
                player2InfoUI_L.getRootNode()
        );
        VBox.setMargin(player1InfoUI_L.getRootNode(), new Insets(15, 0, 0, 0));
        VBox.setMargin(player2InfoUI_L.getRootNode(), new Insets(10, 0, 0, 0));

        StackPane leftContainer = new StackPane(infoPane_L);
        leftContainer.setPadding(new Insets(0, 100, 0, 100));

        root.setRight(rightContainer);
        root.setLeft(leftContainer);
        this.addElement("root", root);
    }

    /**
     * This method should be called every game tick or frame.
     * It reads data from the ClientArkanoidMatch and updates the UI nodes.
     * @param tpf Time per frame (not used here, but good practice)
     */
    public void onUpdate(float tpf) {
        if (match == null || match.getTeams() == null) {
            return; // No data yet
        }

        // Fetch team info
        CTeamInfo newRedTeam = match.getTeams().get(TeamColor.RED);
        CTeamInfo newBlueTeam = match.getTeams().get(TeamColor.BLUE);

        // Update Red Team UI (Left)
        if (newRedTeam != null) {
            // Only update if data has changed to avoid unnecessary UI redraws
            // NOTE: This simple object comparison might not be enough if the
            // CTeamInfo object is modified instead of replaced.
            // For forced updates, remove the 'if (this.redTeam != newRedTeam)' check.
            if (this.redTeam != newRedTeam) {
                updateTeamUI(newRedTeam, scoreValue_L, hearts_L, player1InfoUI_L, player2InfoUI_L, Color.RED);
                this.redTeam = newRedTeam;
            }
        }

        // Update Blue Team UI (Right)
        if (newBlueTeam != null) {
            if (this.blueTeam != newBlueTeam) {
                updateTeamUI(newBlueTeam, scoreValue_R, hearts_R, player1InfoUI_R, player2InfoUI_R, Color.BLUE);
                this.blueTeam = newBlueTeam;
            }
        }

        // --- (NEW) Update Top-Center Scoreboard ---
        if (newRedTeam != null && newBlueTeam != null) {
            // Update round score "00 : 00"
            roundScoreLabel.setText(String.format("%02d : %02d", newRedTeam.ftScore, newBlueTeam.ftScore));
        }

        // TODO: Update timeLabel when time data is available
        // timeLabel.setText(String.format("TIME %02d:%02d", minutes, seconds));
    }

    /**
     * Helper method to update all UI components for a single team.
     */
    private void updateTeamUI(CTeamInfo teamData, Label scoreLabel, HBox heartsBox,
                              PlayerInfoUI p1ui, PlayerInfoUI p2ui, Color teamColor) {

        scoreLabel.setText(String.format("%016d", teamData.arkScore));

        if (heartsBox.getChildren().size() != teamData.livesRemaining) {
            heartsBox.getChildren().clear();
            for (int i = 0; i < teamData.livesRemaining; i++) {
                Label heart = new Label("â�¤");
                heart.setStyle("-fx-font-size: 24px;");
                heart.setTextFill(teamColor);
                heartsBox.getChildren().add(heart);
            }
        }

        if (teamData.players != null && teamData.players.length > 0) {
            updatePlayerUI(p1ui, teamData.players[0]);
        }

        if (teamData.players != null && teamData.players.length > 1) {
            updatePlayerUI(p2ui, teamData.players[1]);
        }
    }

    /**
     * Helper method to update a single player's UI panel.
     */
    private void updatePlayerUI(PlayerInfoUI playerUI, CPlayerInfo playerData) {
        if (playerUI == null || playerData == null) {
            return;
        }

        // (OPTIMIZED) Only set text if it's different
        if (playerData.getName() != null && !playerUI.getPlayerName().getText().equals(playerData.getName())) {
            playerUI.getPlayerName().setText(playerData.getName());
        }

        // Update Health (assuming max 100)
// ... (health/shield TODOs) ...
        // TODO: This logic is flawed, health bar width should be proportional
        // double healthWidth = (playerData.health / 100.0) * (playerUI.getRootNode().getWidth() - 10); // Example
        // playerUI.getHealthBar().setPrefWidth(healthWidth);

        // TODO: Add shield data
        // playerUI.getShieldBar().setPrefWidth(playerData.shield * 2.5);

        // --- (MODIFIED) Update Gun Image ---
        // TODO: Get *which* gun image from data
        Image currentGunImage = RIFLE_IMAGE; // Placeholder
        if (playerUI.getGunImageView().getImage() != currentGunImage) {
            playerUI.getGunImageView().setImage(currentGunImage);
        }

        // Update Ammo
        // (OPTIMIZED) Only set text if it's different
        // TODO: Get max ammo from data
        String ammoText = String.format("%d / 30", playerData.bulletsLeft);
        if (!playerUI.getAmmoCount().getText().equals(ammoText)) {
            playerUI.getAmmoCount().setText(ammoText);
        }

        // (OPTIMIZED) Only set text if it's different
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
    }
}

