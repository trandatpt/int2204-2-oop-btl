package btl.ballgame.client.ui.game;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.shared.libs.Constants.ParticlePriority;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GameRenderCanvas extends Screen {
	private CSWorld world;
	private ClientArkanoidMatch match;
	
	public GameRenderCanvas() {
		super("game");
		this.match = ArkanoidGame.core().getActiveMatch();
		this.world = match.getGameWorld();
	}
	
	@Override
	public void onInit() {
		setStyle(
			"-fx-background-image: url('" + CSAssets.VS_BACKGROUND + "');" + 
			"-fx-background-size: cover;" + 
			"-fx-background-position: center center;" + 
			"-fx-background-repeat: no-repeat;"
		);

        StackPane centerPane = new StackPane();
        centerPane.setAlignment(Pos.CENTER);

        Canvas canvas = new Canvas(world.getWidth(), world.getHeight());
        centerPane.getChildren().add(canvas);

        this.addElement("centerPane", centerPane);

		GraphicsContext ctx = canvas.getGraphicsContext2D();
		ctx.setImageSmoothing(true);
		ctx.setFill(Color.BLACK);
		ctx.fillRect(0,0,world.getWidth(), world.getHeight());
		
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				ctx.setFill(Color.BLACK);
				ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
				world.particles().flushQueue();
				world.particles().render(ParticlePriority.BEFORE_ENTITIES, ctx);
				world.getAllEntities().forEach(e -> e.render(ctx));
				world.particles().render(ParticlePriority.AFTER_ENTITIES, ctx);
			}
		}.start();
		
		this.listenToKeys();
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
		        default -> {}
		    }
		});
		this.setOnKeyReleased(event -> {
			var paddle = core.getPaddle();
			if (paddle == null) return;
		    switch (event.getCode()) {
		        case LEFT -> paddle.setMoveLeft(false);
		        case RIGHT -> paddle.setMoveRight(false);
		        default -> {}
		    }
		});
	}
	
	@Override
	public void onRemove() {
		
	}

}
