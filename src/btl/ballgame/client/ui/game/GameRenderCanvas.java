package btl.ballgame.client.ui.game;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.protocol.packets.in.PacketPlayInPaddleInput;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameRenderCanvas extends Screen {
	private CSWorld world;
	private ClientArkanoidMatch match;
	
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
		        case LEFT -> leftPressed = true;
		        case RIGHT -> rightPressed = true;
		        default -> {}
		    }
		    sendPaddleInput();
		});
		this.setOnKeyReleased(event -> {
		    switch (event.getCode()) {
		        case LEFT -> leftPressed = false;
		        case RIGHT -> rightPressed = false;
		        default -> {}
		    }
		});
	}
	
	private void sendPaddleInput() {
		ArkanoidGame.core().getConnection().sendPacket(
			new PacketPlayInPaddleInput(leftPressed, rightPressed)
		);
	}

	@Override
	public void onRemove() {
		
	}

}
