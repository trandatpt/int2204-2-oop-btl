package btl.ballgame.client.net.systems.entities;

import java.util.Random;

import btl.ballgame.client.net.systems.CSInterpolatedEntity;
import btl.ballgame.client.net.systems.ParticleSystem.Particle;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.DriftBehavior;
import btl.ballgame.shared.libs.Constants.ItemType;
import btl.ballgame.shared.libs.Constants.ParticlePriority;
import btl.ballgame.shared.libs.Constants.ParticleType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class CEntityFallingItem extends CSInterpolatedEntity {
	private static final int ANIM_FRAME_DELAY = 12;

	private int currentAnimStage = 0;
	private int frameCounter = 0;
	private boolean renderUpsideDown = false;
	private Random rand = new Random();
	
	@Override
	public void onEntitySpawn() {
		renderUpsideDown = isUpsideDown();
	}

	@Override
	public void render(GraphicsContext cv) {
		super.render(cv); // interpolation
		Image image = null;
		if (getItemType() == ItemType.AK47_AMMO) {
			image = atlas().getAsImage("falling_item", "ammo_box");
		} else if (getItemType() == ItemType.HEART) {
			// really?
			image = atlas().getAsImage("ui_component", "heart");
		} else {
			image = atlas().getAsImage("falling_item", "other_anim_" + currentAnimStage);
		}
		cv.save();
		if (renderUpsideDown) { // if the item falls up (blue), flip it
			// flip vertically around the item's center
			double centerY = getRenderY() + (getRenderHeight()) / 2.0;
			cv.translate(0, centerY); // center the flip
			cv.scale(1, -1);
			cv.translate(0, -centerY); // restore
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
		cv.restore();
	}
	
	@Override
	public void onEntityDespawn() {
        double centerX = getRenderX() + getRenderWidth() / 2.0;
        double centerY = getRenderY() + getRenderHeight() / 2.0;

        for (int i = 0; i < 10; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            double speed = 0.5 + rand.nextDouble() * 5.0;
            double dx = Math.cos(angle) * speed;
            double dy = Math.sin(angle) * speed; // spew everywhere
            
			Particle particle = new Particle(
				ParticleType.RECTANGLE, 
				DriftBehavior.ROTATE_AND_SHRINK, 
				centerX, centerY,
				dx, dy, 
				24 + rand.nextInt(4), 
				30 + rand.nextInt(20), 
				getItemType() == ItemType.AK47_AMMO ? Color.AQUA : Color.GREEN,
				null
			);
			
            getWorld().particles().spawn(ParticlePriority.AFTER_ENTITIES, particle);
        }
    }
	
	public ItemType getItemType() {
		return ItemType.of((int) getWatcher().get(Constants.ITEM_TYPE_META));
	}
	
	public boolean isUpsideDown() {
		return (boolean) getWatcher().get(Constants.RENDER_UPSIDEDOWN_META);
	}
}