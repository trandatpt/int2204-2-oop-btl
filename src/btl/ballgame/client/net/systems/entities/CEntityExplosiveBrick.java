package btl.ballgame.client.net.systems.entities;

import java.util.Random;

import btl.ballgame.client.TextureAtlas;
import btl.ballgame.client.net.systems.CSEntity;
import btl.ballgame.client.net.systems.ParticleSystem.Particle;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.DriftBehavior;
import btl.ballgame.shared.libs.Constants.ParticlePriority;
import btl.ballgame.shared.libs.Constants.ParticleType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class CEntityExplosiveBrick extends CSEntity {
	private final Image tnt;
	private long lastBeepTime = 0;

	public CEntityExplosiveBrick() {
		tnt = atlas().getAsImage("explosive_brick", "intact");
	}

	@Override
	public void render(GraphicsContext cv) {
		int x = getRenderX(), y = getRenderY();
		int w = getRenderWidth(), h = getRenderHeight();
		// draw the base texture
		cv.drawImage(tnt, x, y, w, h);
		if (isPrimed()) {
			// flash every ~250ms
			long time = System.currentTimeMillis();
			if (((time / 250L) % 2) == 0) {
				cv.setFill(Color.rgb(255, 255, 255, 0.6)); // translucent white overlay
				cv.fillRect(x, y, w, h);
			}
			if (time - lastBeepTime > 350) {
				SoundManager.play("TimeBomb");
				lastBeepTime = time;
			}
		}
	}

	@Override
	public void onEntityDespawn() {
		SoundManager.bomb();
		Random rand = new Random();
		double centerX = getRenderX() + getRenderWidth() / 2.0;
		double centerY = getRenderY() + getRenderHeight() / 2.0;
		
		// firey particles
		for (int i = 0; i < 40; i++) {
			double angle = rand.nextDouble() * Math.PI * 2;
			double speed = 2.0 + rand.nextDouble() * 4;
			double dx = Math.cos(angle) * speed;
			double dy = Math.sin(angle) * speed;

			Particle p = new Particle(
				ParticleType.RECTANGLE,
				DriftBehavior.ROTATE_AND_SHRINK,
				centerX, centerY,
				dx, dy,
				20 + rand.nextInt(6), // size
				10 + rand.nextInt(20), // life
				Color.hsb(rand.nextDouble() * 30, 1.0, 1.0),
				null
			);
			getWorld().particles().spawn(ParticlePriority.AFTER_ENTITIES, p);
		}
		
		// smoke
		for (int i = 0; i < 25; i++) {
			double angle = rand.nextDouble() * Math.PI * 2;
			double speed = 0.5 + rand.nextDouble() * 2;
			double dx = Math.cos(angle) * speed;
			double dy = Math.sin(angle) * speed;
			int gray = (int) (100 + rand.nextDouble() * 100);

			Particle smoke = new Particle(
				ParticleType.OVAL,
				DriftBehavior.NONE,
				centerX, centerY,
				dx, dy,
				30 + rand.nextInt(10), // size
				30 + rand.nextInt(30), // life
				Color.rgb(gray, gray, gray),
				null
			);
			getWorld().particles().spawn(ParticlePriority.AFTER_ENTITIES, smoke);
		}
	}

	public boolean isPrimed() {
		return (boolean) getWatcher().get(Constants.EXPLOSIVE_PRIMED_META);
	}
}
