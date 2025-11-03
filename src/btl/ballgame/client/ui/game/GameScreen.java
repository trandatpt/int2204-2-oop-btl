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

public class GameScreen extends Screen {
    private ClientArkanoidMatch match;
    private GameRenderCanvas gameRenderCanvas;

    private AnimationTimer gameLoop;
    private long lastTick;

    // --- UI Node References for RED Team (LEFT) ---
    private Label scoreValueLeft;
    private HBox heartsLeft;
    private VBox playerBoxLeft;

    // --- UI Node References for BLUE Team (RIGHT) ---
    private Label scoreValueRight;
    private HBox heartsRight;
    private VBox playerBoxRight;

    // --- UI Node References for Top-Center Scoreboard ---
    private Label roundScoreLabel;
    private Label timeLabel;

    private static final Image logoTeam_1 = CSAssets.sprites.__get("logo/logoTeam_1.png");
    private static final Image logoTeam_2 = CSAssets.sprites.__get("logo/logoTeam_2.png");
    private static final Image RIFLE_IMAGE = CSAssets.sprites.__get("item/AK47-Tiles-01.png");
    private static final Image heartImage = CSAssets.sprites.__get("item/Heart-Tiles-01.png");

    // Map of player UUID to PlayerInfoUI for reuse
    private final Map<String, PlayerInfoUI> playerUIMap = new HashMap<>();

    public GameScreen(GameRenderCanvas gameRenderCanvas) {
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

        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                CSAssets.VS_BACKGROUND,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        root.setBackground(new Background(backgroundImage));

        // --- Center canvas ---
        if (this.gameRenderCanvas != null) {
            this.gameRenderCanvas.onInit();
            root.setCenter(this.gameRenderCanvas);
        }

        // --- TOP-CENTER SCOREBOARD ---
        VBox topCenterBox = new VBox(-5);
        topCenterBox.setPrefWidth(600);
        topCenterBox.setAlignment(Pos.CENTER);
        topCenterBox.setPadding(new Insets(10, 0, 0, 0));

        // Round Score Label
        roundScoreLabel = new Label("00 : 00");
        roundScoreLabel.setTextFill(Color.WHITE);
        roundScoreLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");

        // Time Label
        timeLabel = new Label("TIME 00:00");
        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");

        topCenterBox.getChildren().addAll(roundScoreLabel, timeLabel);
        root.setTop(topCenterBox);
        BorderPane.setAlignment(topCenterBox, Pos.CENTER);

        if (this.gameRenderCanvas != null) {
            BorderPane.setMargin(this.gameRenderCanvas, new Insets(10, 0, 0, 0));
        }

        // --- BLUE TEAM (RIGHT) ---
        VBox infoPaneRight = new VBox(10);
        infoPaneRight.setPrefWidth(400);
        infoPaneRight.setMaxHeight(600);
        infoPaneRight.setPadding(new Insets(15));
        infoPaneRight.setStyle("-fx-background-color: rgba(34,34,34,0.4); -fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        infoPaneRight.setAlignment(Pos.TOP_CENTER);

        HBox teamBoxRight = new HBox(8);
        teamBoxRight.setAlignment(Pos.TOP_CENTER);
        teamBoxRight.setPadding(new Insets(5));
        teamBoxRight.setStyle("-fx-background-color: rgba(34,34,34,0.6); -fx-border-color: white; -fx-border-radius: 10;");

        // Team Logo and Label for BLUE Team
        ImageView logoViewRight = new ImageView(logoTeam_1);
        logoViewRight.setFitWidth(40);
        logoViewRight.setFitHeight(40);
        StackPane colorBoxRight = new StackPane(logoViewRight);
        colorBoxRight.setPrefSize(40, 40);
        Label teamLabelRight = new Label("BLUE");
        teamLabelRight.setTextFill(Color.BLUE);
        teamLabelRight.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        teamBoxRight.getChildren().addAll(colorBoxRight, teamLabelRight);

        Label teamLivesLabelRight = new Label("Team Lives");
        teamLivesLabelRight.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        // Hearts Container for BLUE Team
        heartsRight = new HBox(5);
        heartsRight.setAlignment(Pos.TOP_CENTER);

        // Score Box for BLUE Team
        HBox scoreBoxRight = new HBox(10);
        scoreBoxRight.setAlignment(Pos.CENTER);
        Label scoreLabelRight = new Label("Score:");
        scoreLabelRight.setTextFill(Color.WHITE);
        scoreLabelRight.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        scoreValueRight = new Label("0000000000000000");
        scoreValueRight.setTextFill(Color.WHITE);
        scoreValueRight.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");
        scoreBoxRight.getChildren().addAll(scoreLabelRight, scoreValueRight);

        // Player Box for BLUE Team
        playerBoxRight = new VBox(10);
        playerBoxRight.setAlignment(Pos.CENTER_RIGHT);
        playerBoxRight.setId("playerBoxRight");

        infoPaneRight.getChildren().addAll(teamBoxRight, teamLivesLabelRight, heartsRight, scoreBoxRight, playerBoxRight);
        VBox.setMargin(playerBoxRight, new Insets(15, 0, 0, 0));

        StackPane rightContainer = new StackPane(infoPaneRight);
        rightContainer.setPadding(new Insets(0, 100, 0, 100));
        BorderPane.setAlignment(rightContainer, Pos.CENTER);

        // --- RED TEAM (LEFT) ---
        VBox infoPaneLeft = new VBox(10);
        infoPaneLeft.setPrefWidth(400);
        infoPaneLeft.setMaxHeight(600);
        infoPaneLeft.setPadding(new Insets(15));
        infoPaneLeft.setStyle("-fx-background-color: rgba(34,34,34,0.4); -fx-border-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        infoPaneLeft.setAlignment(Pos.TOP_CENTER);

        HBox teamBoxLeft = new HBox(8);
        teamBoxLeft.setAlignment(Pos.TOP_CENTER);
        teamBoxLeft.setPadding(new Insets(5));
        teamBoxLeft.setStyle("-fx-background-color: rgba(34,34,34,0.6); -fx-border-color: white; -fx-border-radius: 10;");

        // Team Logo and Label for RED Team
        ImageView logoViewLeft = new ImageView(logoTeam_2);
        logoViewLeft.setFitWidth(40);
        logoViewLeft.setFitHeight(40);
        StackPane colorBoxLeft = new StackPane(logoViewLeft);
        colorBoxLeft.setPrefSize(40, 40);
        Label teamLabelLeft = new Label("RED");
        teamLabelLeft.setTextFill(Color.RED);
        teamLabelLeft.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        teamBoxLeft.getChildren().addAll(colorBoxLeft, teamLabelLeft);

        Label teamLivesLabelLeft = new Label("Team Lives");
        teamLivesLabelLeft.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        // Hearts Container for RED Team
        heartsLeft = new HBox(5);
        heartsLeft.setAlignment(Pos.TOP_CENTER);

        // Score Box for RED Team
        HBox scoreBoxLeft = new HBox(10);
        scoreBoxLeft.setAlignment(Pos.CENTER);
        Label scoreLabelLeft = new Label("Score:");
        scoreLabelLeft.setTextFill(Color.WHITE);
        scoreLabelLeft.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        scoreValueLeft = new Label("0000000000000000");
        scoreValueLeft.setTextFill(Color.WHITE);
        scoreValueLeft.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");
        scoreBoxLeft.getChildren().addAll(scoreLabelLeft, scoreValueLeft);

        // Player Box for RED Team
        playerBoxLeft = new VBox(10);
        playerBoxLeft.setAlignment(Pos.CENTER_LEFT);
        playerBoxLeft.setId("playerBoxLeft");

        infoPaneLeft.getChildren().addAll(teamBoxLeft, teamLivesLabelLeft, heartsLeft, scoreBoxLeft, playerBoxLeft);
        VBox.setMargin(playerBoxLeft, new Insets(15, 0, 0, 0));

        StackPane leftContainer = new StackPane(infoPaneLeft);
        leftContainer.setPadding(new Insets(0, 100, 0, 100));
        BorderPane.setAlignment(leftContainer, Pos.CENTER);

        root.setRight(rightContainer);
        root.setLeft(leftContainer);
        this.addElement("root", root);

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

        CTeamInfo redTeam = match.getTeams().get(TeamColor.RED);
        CTeamInfo blueTeam = match.getTeams().get(TeamColor.BLUE);

        if (redTeam != null) updateTeamUI(redTeam, scoreValueLeft, heartsLeft, playerBoxLeft, Color.RED);
        if (blueTeam != null) updateTeamUI(blueTeam, scoreValueRight, heartsRight, playerBoxRight, Color.BLUE);

        if (redTeam != null && blueTeam != null) {
            String text = String.format("%02d : %02d", redTeam.ftScore, blueTeam.ftScore);
            if (!roundScoreLabel.getText().equals(text)) roundScoreLabel.setText(text);
        }
    }

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
                        k -> PlayerInfoBuilder.createPlayerInfoBox(player.getName(), teamColor == Color.RED ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT)
                );
                updatePlayerUI(ui, player);
                playerBox.getChildren().add(ui.getRootNode());
            }
        }
    }

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