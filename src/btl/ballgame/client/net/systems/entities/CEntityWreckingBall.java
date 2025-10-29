package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.net.systems.CSInterpolatedEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CEntityWreckingBall extends CSInterpolatedEntity {
	@Override
	public void render(GraphicsContext cv) {
		super.render(cv);
		
		cv.setFill(Color.RED);
		cv.fillRect(getRenderX(), getRenderY(), getWidth(), getHeight());
		cv.setFill(Color.BLACK);
	}
}
