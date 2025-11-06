package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.TextureAtlas;
import btl.ballgame.client.net.systems.CSInterpolatedEntity;
import btl.ballgame.shared.libs.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CEntityPaddle extends CSInterpolatedEntity {
	static final String NORMAL_NAMESPACE = "paddle_normal";
	static final String EXPANDED_NAMESPACE = "paddle_expanded";
	private static final int ANIM_FRAME_DELAY = 12;
	private static final int FLASH_DURATION = 10; // frames per flash
	private static final int FLASH_COUNT = 3; // flash 3 times
	
	private int currentAnimStage = 0;
	private int frameCounter = 0;
	private String toUse = NORMAL_NAMESPACE;
	private boolean renderUpsideDown = false;

	// on damaged flashing
	private boolean flashing = false;
	private int flashTimer = 0;
	private int flashCounter = 0;
	
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
		
		Color tint = Color.WHITE; // white = no tint
		if (flashing) {
			tint = (flashTimer / FLASH_DURATION) % 2 == 0 ? Color.RED : Color.WHITE;
			flashTimer++;
			if (flashTimer >= FLASH_DURATION * 2) { // one red n white cycle completed
				flashTimer = 0;
				if (++flashCounter >= FLASH_COUNT) {
					flashing = false; // stop flashing
					flashCounter = 0;
				}
			}
		}
		
		drawTinted(cv, atlas().getAsImage(this.toUse, "anim_" + currentAnimStage), 
			getRenderX(), getRenderY(),
			getRenderWidth(), getRenderHeight(),
			TextureAtlas.fromFXColor(tint)
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
	
	@Override
	public void onEntityEffectDamaged() {
		// trigger red flash effect
		flashing = true;
		flashTimer = 0;
		flashCounter = 0;
	}
	
	public boolean isExpanded() {
		return (boolean) getWatcher().getOrDefault(Constants.PADDLE_EXPANDED_META, false);
	}
	
	public boolean isUpsideDown() {
		return (boolean) getWatcher().get(Constants.RENDER_UPSIDEDOWN_META);
	}
}
