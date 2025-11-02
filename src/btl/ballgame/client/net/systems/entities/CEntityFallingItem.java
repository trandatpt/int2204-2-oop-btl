package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.net.systems.CSInterpolatedEntity;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.ItemType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class CEntityFallingItem extends CSInterpolatedEntity {
	private static final int ANIM_FRAME_DELAY = 12;

	private int currentAnimStage = 0;
	private int frameCounter = 0;
	private boolean renderUpsideDown = false;

	@Override
	public void render(GraphicsContext cv) {
		super.render(cv); // interpolation
		Image image = null;

		if (getItemType() == ItemType.AK47_AMMO) {
			image = atlas().getAsImage("falling_item", "ammo_box");
		} else {
			image = atlas().getAsImage("falling_item", "other_anim_" + currentAnimStage);
		}
		
		// draw the ball
		cv.drawImage(image, 
			getRenderX(), getRenderY(), 
			getRenderWidth(), getRenderHeight()
		);
		
		if (++frameCounter >= ANIM_FRAME_DELAY) {
			frameCounter = 0;
			currentAnimStage = (currentAnimStage + 1) % 8;
		}
	}
	
	public ItemType getItemType() {
		return ItemType.of((int) getWatcher().get(Constants.ITEM_TYPE_META));
	}
	
	public boolean isUpsideDown() {
		return (boolean) getWatcher().get(Constants.RENDER_UPSIDEDOWN_META);
	}
}