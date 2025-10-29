package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.net.systems.CSInterpolatedEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CEntityPaddle extends CSInterpolatedEntity {
	@Override
	public void render(GraphicsContext cv) {
		super.render(cv);
		cv.setFill(Color.AQUA);
		cv.fillRect(getRenderX(), getRenderY(), getWidth(), getHeight());
		cv.setFill(Color.BLACK);
	}
}
