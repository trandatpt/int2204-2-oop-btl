package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.net.systems.CSEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CEntityBrickNormal extends CSEntity {
	@Override
	public void render(GraphicsContext cv) {
		cv.setFill(Color.BLUEVIOLET);
		cv.fillRect(getRenderX(), getRenderY(), getWidth(), getHeight());
		cv.setFill(Color.BLACK);
	}
}
