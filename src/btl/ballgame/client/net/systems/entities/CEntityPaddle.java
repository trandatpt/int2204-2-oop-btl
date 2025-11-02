package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.net.systems.CSInterpolatedEntity;
import btl.ballgame.shared.libs.Constants;
import javafx.scene.canvas.GraphicsContext;

public class CEntityPaddle extends CSInterpolatedEntity {
	static final String NORMAL_NAMESPACE = "paddle_normal";
	static final String EXPANDED_NAMESPACE = "paddle_expanded";
	private static final int ANIM_FRAME_DELAY = 12;
	
	private int currentAnimStage = 0;
	private int frameCounter = 0;
	private String toUse = NORMAL_NAMESPACE;
	private boolean renderUpsideDown = false;
	
	@Override
	public void onEntitySpawn() {
		this.toUse = isExpanded() ? EXPANDED_NAMESPACE : NORMAL_NAMESPACE;
		this.renderUpsideDown = isUpsideDown();
	}
	
	@Override
	public void render(GraphicsContext cv) {
		super.render(cv);
		cv.save();
		if (renderUpsideDown) { // the blue team's paddle(s)
			// flip vertically around the paddle's center
			double centerY = getRenderY() + (getRenderHeight()) / 2.0;
			cv.translate(0, centerY); // center the flip
			cv.scale(1, -1);
			cv.translate(0, -centerY); // restore
		}
		cv.drawImage(atlas().getAsImage(this.toUse, "anim_" + currentAnimStage), 
			getRenderX(), getRenderY(),
			getRenderWidth(), getRenderHeight()
		);
		if (++frameCounter >= ANIM_FRAME_DELAY) {
			frameCounter = 0;
			currentAnimStage = (currentAnimStage + 1) % 3;
		}
		cv.restore();
	}

	
	@Override
	public void onAfterWatcherUpdate() {
		this.toUse = isExpanded() ? EXPANDED_NAMESPACE : NORMAL_NAMESPACE;
	}
	
	public boolean isExpanded() {
		return (boolean) getWatcher().get(Constants.PADDLE_EXPANDED_META);
	}
	
	public boolean isUpsideDown() {
		return (boolean) getWatcher().get(Constants.PADDLE_UPSIDEDOWN_META);
	}
}
