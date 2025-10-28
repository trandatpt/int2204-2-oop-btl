package btl.ballgame.client.ui.game;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.client.net.systems.entities.CEntityPaddleLocal;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.protocol.packets.in.PacketPlayInPaddleControl;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameRenderCanvas extends Screen {
	private CSWorld world;
	private ClientArkanoidMatch match;
	
	public static CEntityPaddleLocal local;
	
	public GameRenderCanvas() {
		super("game");
		this.match = ArkanoidGame.core().getActiveMatch();
		this.world = match.getGameWorld();
	}
	
	private boolean leftPressed = false;
	private boolean rightPressed = false;
	@Override
	public void onInit() {
		Canvas canvas = addElement("gameCanvas", new Canvas(
			world.getWidth(), world.getHeight()
		));

		GraphicsContext ctx = canvas.getGraphicsContext2D();
		ctx.setFill(Color.BLACK);
		ctx.fillRect(0,0,world.getWidth(), world.getHeight());
		
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				ctx.setFill(Color.BLACK);
				ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
				
				world.getAllEntities().forEach(e -> e.render(ctx));
			}
		}.start();
		
		this.listenToKeys();
	}
	
	private void listenToKeys() {
		this.setFocusTraversable(true);
		this.setOnKeyPressed(event -> {
		    switch (event.getCode()) {
		        case LEFT -> local.setLeftPressed(leftPressed = true);
		        case RIGHT -> local.setRightPressed(rightPressed = true);
		        default -> {}
		    }
		});
		this.setOnKeyReleased(event -> {
		    switch (event.getCode()) {
		        case LEFT -> local.setLeftPressed(leftPressed = false);
		        case RIGHT -> local.setRightPressed(rightPressed = false);
		        default -> {}
		    }
		});
	}
	
	@Override
	public void onRemove() {
		
	}

}
