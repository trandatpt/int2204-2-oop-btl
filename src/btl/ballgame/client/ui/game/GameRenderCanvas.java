package btl.ballgame.client.ui.game;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.shared.libs.Constants.ParticlePriority;
import btl.ballgame.shared.libs.Constants.TeamColor;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GameRenderCanvas extends Screen {
    private CSWorld world;
    private ClientArkanoidMatch match;
    private Canvas canvas;
    private GraphicsContext ctx;

    public GameRenderCanvas() {
        super("game");
        this.match = ArkanoidGame.core().getActiveMatch();
        this.world = match.getGameWorld();
    }

    @Override
    public void onInit() {
        setStyle("-fx-background-color: transparent;");

        StackPane centerPane = new StackPane();
        centerPane.setAlignment(Pos.CENTER);

        canvas = new Canvas(world.getWidth(), world.getHeight());
        canvas.setStyle("-fx-background-color: transparent;");

        ImageView borderView = new ImageView(CSAssets.BORDER_GAME);

        double borderWidth = world.getWidth() + 60;
        double borderHeight = world.getHeight() + 45;

        borderView.setFitWidth(borderWidth);
        borderView.setFitHeight(borderHeight);
        borderView.setPreserveRatio(false);

        centerPane.getChildren().addAll(borderView, canvas);

        this.addElement("centerPane", centerPane);

        ctx = canvas.getGraphicsContext2D();
        ctx.setImageSmoothing(true);

        this.listenToKeys();
    }

    /**
     * (NEW) This function is now called by the central game loop
     * in GameCompositeScreen.
     */
	public void doRender() {
		if (ctx == null) {
			return;
		}
		
		// fill the background
		ctx.setFill(Color.GRAY);
		ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		world.particles().flushQueue();

		// flip the view vertically for blue team players,
		// so their paddles always appear at the bottom of the screen
		// regardless of the server's coordinate system 
		boolean shouldFlip = match.getCurrentTeam() == TeamColor.BLUE;
		if (shouldFlip) {
			ctx.save();
			ctx.translate(0, canvas.getHeight());
			ctx.scale(1, -1);
		}

		world.particles().render(ParticlePriority.BEFORE_ENTITIES, ctx);
		world.getAllEntities().forEach(e -> e.render(ctx));
		world.particles().render(ParticlePriority.AFTER_ENTITIES, ctx);

		if (shouldFlip) ctx.restore();
		
		ctx.setGlobalAlpha(1.0);
		// render particles that are upright REGARDLESS
		world.particles().render(ParticlePriority.LATEST_IGNORE_FLIP, ctx);
	}

    private void listenToKeys() {
        ArkanoidClientCore core = ArkanoidGame.core();
        this.setFocusTraversable(true);
        this.setOnKeyPressed(event -> {
            var paddle = core.getPaddle();
            if (paddle == null) return;
            switch (event.getCode()) {
                case LEFT -> paddle.setMoveLeft(true);
                case RIGHT -> paddle.setMoveRight(true);
                case SPACE -> paddle.setShooting(true);
                default -> {}
            }
        });
        this.setOnKeyReleased(event -> {
            var paddle = core.getPaddle();
            if (paddle == null) return;
            switch (event.getCode()) {
                case LEFT -> paddle.setMoveLeft(false);
                case RIGHT -> paddle.setMoveRight(false);
                case SPACE -> paddle.setShooting(false);
                default -> {}
            }
        });
    }

    @Override
    public void onRemove() {

    }

}

