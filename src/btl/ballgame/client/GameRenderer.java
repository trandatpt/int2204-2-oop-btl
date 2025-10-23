package btl.ballgame.client;

import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.ClientArkanoidMatch.CTeamInfo;
import btl.ballgame.client.ClientArkanoidMatch.CPlayerInfo;
import btl.ballgame.shared.libs.Constants.TeamColor;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Map;

public class GameRenderer extends BorderPane {

	private final ClientArkanoidMatch match;
	private final Canvas gameCanvas;
	private final VBox infoPanel;

	private double scaleX = 1.0;
	private double scaleY = 1.0;

	public GameRenderer(ClientArkanoidMatch match) {
		this.match = match;

		gameCanvas = new Canvas(800, 600); // default size
		StackPane canvasWrapper = new StackPane(gameCanvas);
		canvasWrapper.setStyle("-fx-background-color: black;");
		this.setLeft(canvasWrapper);

		infoPanel = new VBox(10);
		infoPanel.setPadding(new Insets(20));
		infoPanel.setAlignment(Pos.TOP_LEFT);
		infoPanel.setPrefWidth(300);
		infoPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #2a2a2a, #1e1e1e);");

		this.setRight(infoPanel);

		this.widthProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());
		this.heightProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());

		// ====== Animation Loop ======
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				render();
			}
		};
		timer.start();
	}

	private void resizeCanvas() {
		double availableWidth = this.getWidth() - infoPanel.getPrefWidth();
		double availableHeight = this.getHeight();

		if (match.getGameWorld() == null)
			return;

		int worldWidth = match.getGameWorld().getWidth();
		int worldHeight = match.getGameWorld().getHeight();

		scaleX = availableWidth / worldWidth;
		scaleY = availableHeight / worldHeight;

		gameCanvas.setWidth(availableWidth);
		gameCanvas.setHeight(availableHeight);
	}

	private void render() {
		if (match.getGameWorld() == null)
			return;

		GraphicsContext gc = gameCanvas.getGraphicsContext2D();

		// Clear canvas
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

		// Draw the game world
		drawWorld(gc);

		// Update info panel
		updateInfoPanel();
	}

	private void drawWorld(GraphicsContext gc) {
		var world = match.getGameWorld();

		world.getAllEntities().forEach(entity -> {
//			double x = obj.getRenderX() * scaleX;
//			double y = obj.getRenderY() * scaleY;
//			double w = obj.getWidth() * scaleX;
//			double h = obj.getHeight() * scaleY;
//
//			gc.setFill(obj.getColor());
//			gc.fillRect(x, y, w, h);
			entity.render(gc);
		});
	}

	private void updateInfoPanel() {
		infoPanel.getChildren().clear();

		Label phaseLabel = new Label("Phase: " + match.getPhase());
		phaseLabel.setTextFill(Color.WHITE);
		phaseLabel.setFont(Font.font("Consolas", 16));

		Label roundLabel = new Label("Round: " + match.getRoundIndex());
		roundLabel.setTextFill(Color.WHITE);
		roundLabel.setFont(Font.font("Consolas", 16));

		infoPanel.getChildren().addAll(phaseLabel, roundLabel);

		for (Map.Entry<TeamColor, CTeamInfo> entry : match.teams.entrySet()) {
			CTeamInfo team = entry.getValue();
			Label teamLabel = new Label(team.teamColor.name() + " - FT: " + team.ftScore + " Ark: " + team.arkScore);
			teamLabel.setTextFill(Color.LIGHTGRAY);
			teamLabel.setFont(Font.font("Consolas", 14));
			infoPanel.getChildren().add(teamLabel);

			if (team.players != null) {
				for (CPlayerInfo player : team.players) {
					Label playerLabel = new Label(
							"  " + player.name + " HP: " + player.health + " Bullets: " + player.bulletsLeft);
					playerLabel.setTextFill(Color.WHITE);
					playerLabel.setFont(Font.font("Consolas", 12));
					infoPanel.getChildren().add(playerLabel);
				}
			}
		}
	}
}
