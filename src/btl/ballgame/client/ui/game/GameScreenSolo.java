package btl.ballgame.client.ui.game;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.ClientArkanoidMatch.CPlayerInfo;
import btl.ballgame.client.ClientArkanoidMatch.CTeamInfo;
import btl.ballgame.client.ui.menus.InformationalScreen;
import btl.ballgame.client.ui.menus.MenuUtils;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.protocol.packets.in.PacketPlayInPauseGame;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.TeamColor;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

import static btl.ballgame.client.ui.game.GameUtils.*; 

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

    // Map of player UUID to PlayerInfoUI for reuse
    private final Map<String, PlayerInfoUI> playerUIMap = new HashMap<>();
    
    private boolean gamePaused;

    public GameScreenSolo(GameRenderCanvas gameRenderCanvas) {
        super("Singleplayer (Arkanoid Classic)");
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

        // --- Set Background Images ---
        BackgroundSize mainBgSize = new BackgroundSize(100, 100, true, true, false, true);

        BackgroundSize borderBgSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage mainBg = new BackgroundImage(
                CSAssets.VS_BACKGROUND2,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                mainBgSize
        );

        BackgroundImage borderBg = new BackgroundImage(
                CSAssets.BORDER_BG,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                borderBgSize
        );
        root.setBackground(new Background(mainBg, borderBg));

        // --- Center: Game Canvas ---
        if (this.gameRenderCanvas != null) {
            this.gameRenderCanvas.onInit();
            root.setCenter(this.gameRenderCanvas);
            BorderPane.setMargin(this.gameRenderCanvas, new Insets(10, 10, 40, 200));
        }

        // --- TOP PANE (for LEVEL) ---
        levelLabel = new Label("LEVEL 1"); // Placeholder
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.setStyle("-fx-font-size: 36px; " +
                "-fx-font-weight: bold; " +
                "-fx-font-family: 'Arial'; " +
                "-fx-text-fill: white; " +
                "-fx-stroke: red; " +
                "-fx-stroke-width: 2;"
        );
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


        // --- SOLO INFO PANE ---
        VBox infoPaneSolo = new VBox(10);
        infoPaneSolo.setPrefWidth(400);
        infoPaneSolo.setMaxHeight(400);
        infoPaneSolo.setPadding(new Insets(30, 15, 15, 15));
        infoPaneSolo.setStyle("-fx-background-color: rgba(34,34,34,0.7); " +
                "-fx-border-color: white; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10;"
        );
        infoPaneSolo.setAlignment(Pos.TOP_CENTER);

        // Team Box
        HBox teamBoxSolo = new HBox(8);
        teamBoxSolo.setAlignment(Pos.TOP_CENTER);
        teamBoxSolo.setPadding(new Insets(5));
        teamBoxSolo.setStyle("-fx-background-color: rgba(34,34,34,0.6); " +
                "-fx-border-color: white; " +
                "-fx-border-radius: 10;"
        );

        // Team Logo and Label
        ImageView logoViewSolo = new ImageView(BLUE_TEAM_ICON);
        logoViewSolo.setFitWidth(40);
        logoViewSolo.setFitHeight(40);
        StackPane colorBoxSolo = new StackPane(logoViewSolo);
        colorBoxSolo.setPrefSize(40, 40);
        Label teamLabelSolo = new Label("SOLO");
        teamLabelSolo.setTextFill(Color.LIGHTBLUE);
        teamLabelSolo.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        teamBoxSolo.getChildren().addAll(colorBoxSolo, teamLabelSolo);

        // Lives Label
        Label teamLivesLabelSolo = new Label("Lives");
        teamLivesLabelSolo.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        // Hearts HBox
        heartsSolo = new HBox(5);
        heartsSolo.setAlignment(Pos.TOP_CENTER);

        // Score HBox
        HBox scoreBoxSolo = new HBox(10);
        scoreBoxSolo.setAlignment(Pos.CENTER);
        Label scoreLabelSolo = new Label("Score:");
        scoreLabelSolo.setTextFill(Color.WHITE);
        scoreLabelSolo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        scoreValueSolo = new Label("0000000000000000");
        scoreValueSolo.setTextFill(Color.WHITE);
        scoreValueSolo.setStyle("-fx-font-size: 20px; " +
                "-fx-font-weight: bold; " +
                "-fx-font-family: 'Monospaced';"
        );
        scoreBoxSolo.getChildren().addAll(scoreLabelSolo, scoreValueSolo);

        // Player
        playerBoxSolo = new VBox(10);
        playerBoxSolo.setAlignment(Pos.CENTER_RIGHT);
        playerBoxSolo.setId("playerBoxSolo");

        infoPaneSolo.getChildren().addAll(teamBoxSolo, teamLivesLabelSolo,
                heartsSolo, scoreBoxSolo, playerBoxSolo);
        VBox.setMargin(playerBoxSolo, new Insets(15, 0, 0, 0));

        // Border Image
        ImageView borderViewSolo = new ImageView(CSAssets.BORDER_BLUE);
        borderViewSolo.setFitWidth(400);
        borderViewSolo.setFitHeight(400);
        borderViewSolo.setPreserveRatio(false);
        borderViewSolo.setMouseTransparent(true);

        // Container for padding/alignment
        StackPane rightContainer = new StackPane(infoPaneSolo, borderViewSolo);
        rightContainer.setPadding(new Insets(0, 200, 100, 200));
        BorderPane.setAlignment(rightContainer, Pos.CENTER);

        root.setRight(rightContainer);
        this.addElement("root", root);

        // --- Game Loop ---
        lastTick = System.nanoTime();

        final long MAX_DELTA_TIME_NS = Constants.NS_PER_TICK * 5;
        final float fixedTpf = 1.0f / Constants.TICKS_PER_SECOND;

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
            	if (gamePaused) return;
                long elapsedTime = now - lastTick;
                lastTick = now;

                if (elapsedTime > MAX_DELTA_TIME_NS) {
                    elapsedTime = MAX_DELTA_TIME_NS;
                }

                delta += elapsedTime;

                while (delta >= Constants.NS_PER_TICK) {
                    onUpdate(fixedTpf);
                    delta -= Constants.NS_PER_TICK;
                }

                if (gameRenderCanvas != null) {
                    gameRenderCanvas.doRender();
                }
            }
        };
        gameLoop.start();
        createPauseScreen();
    }

    public void onUpdate(float tpf) {
        if (match == null) return;

        // Level, bruh
        String levelText = String.format("LEVEL %d", match.getRoundIndex() + 1);
        if (levelLabel != null && !levelLabel.getText().equals(levelText)) {
            levelLabel.setText(levelText);
        }

        if (match.getTeams() == null) return;

        // Update SOLO Team Info
        CTeamInfo soloTeam = match.getTeams().get(TeamColor.RED);
        if (soloTeam != null) {
            updateTeamUI(soloTeam, scoreValueSolo, heartsSolo, playerBoxSolo, Color.RED);
        }
    }

    /**
     * (Copied from GameScreen)
     * Helper method to update all UI components for a single team.
     */
    private void updateTeamUI(CTeamInfo teamData, Label scoreLabel,
                              HBox heartsBox, VBox playerBox, Color teamColor) {
        if (teamData == null) return;

        String scoreText = String.format("%016d", teamData.arkScore);
        if (!scoreLabel.getText().equals(scoreText)) scoreLabel.setText(scoreText);

        if (heartsBox.getChildren().size() != teamData.livesRemaining) {
            heartsBox.getChildren().clear();
            for (int i = 0; i < teamData.livesRemaining; i++) {
                ImageView heartView = new ImageView(HEART_ICON);
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
                        k -> PlayerInfoBuilder.createPlayerInfoBox("P", Pos.CENTER_RIGHT)
                );
                updatePlayerUI(ui, player);
                playerBox.getChildren().add(ui.getRootNode());
            }
        }
    }
    
	// SINGLEPLAYER PAUSE (will send a pause siggnal to the server)
	public StackPane pauseOverlay;
	private long lastPauseToggle = 0; // mms
	private static final long PAUSE_COOLDOWN_MS = 500; // prevent the server from shitting itself

	public void createPauseScreen() {
		InformationalScreen pauseScreen = new InformationalScreen("PAUSED", 
			"Game Menu", null,
		false);

		// Example buttons
		pauseScreen.addButton("Back To Game", () -> {
			pauseOverlay.setVisible(false);
			updatePause();
		});
		pauseScreen.addButton("Quit to Title", () -> {
			ArkanoidGame.core().disconnect();
			MenuUtils.displayServerSelector();
		});

		pauseScreen.onInit();

		pauseOverlay = new StackPane();
		pauseOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
		pauseOverlay.getChildren().add(pauseScreen);
		pauseOverlay.setVisible(false);

		this.addElement("pauseScreen", pauseOverlay);

		setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				long now = System.currentTimeMillis();
				if (now - lastPauseToggle >= PAUSE_COOLDOWN_MS) {
					lastPauseToggle = now;
					pauseOverlay.setVisible(!pauseOverlay.isVisible());
					updatePause();
				}
			}
		});
	}
	
	private void updatePause() {
		this.gamePaused = pauseOverlay.isVisible();
		ArkanoidGame.core().getConnection().sendPacket(
			new PacketPlayInPauseGame(gamePaused)
		);
	}

    @Override
    public void onRemove() {
        if (gameLoop != null) gameLoop.stop();
    }
}

