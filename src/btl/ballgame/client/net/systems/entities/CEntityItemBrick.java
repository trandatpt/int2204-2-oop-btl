package btl.ballgame.client.net.systems.entities;

import java.util.Random;

import btl.ballgame.client.TextureAtlas;
import btl.ballgame.client.net.systems.CSEntity;
import btl.ballgame.client.net.systems.ParticleSystem.Particle;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.DriftBehavior;
import btl.ballgame.shared.libs.Constants.ParticlePriority;
import btl.ballgame.shared.libs.Constants.ParticleType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class CEntityItemBrick extends CSEntity {
	private final Image itemBrick;
	private final Random rand = new Random();

	public CEntityItemBrick() {
		itemBrick = atlas().getAsImage("item_brick", "intact");
	}

	@Override
	public void render(GraphicsContext cv) {
		cv.drawImage(itemBrick, 
			getRenderX(), getRenderY(), 
			getRenderWidth(), getRenderHeight()
		);
	}
	
	@Override
	public void onEntityDespawn() {
		spawnDamageParticles(getMaxHealth());
	}
	
    private void spawnDamageParticles(int damageAmount) {
        int count = Math.min(8 + damageAmount * 2, 20); // spawn more JEWS for larger hits
        double centerX = getRenderX() + getRenderWidth() / 2.0;
        double centerY = getRenderY() + getRenderHeight() / 2.0;

        for (int i = 0; i < count; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            double speed = 0.5 + rand.nextDouble() * 2.0;
            double dx = Math.cos(angle) * speed;
            double dy = Math.sin(angle) * speed; // spew everywhere
            
			Particle particle = new Particle(
				ParticleType.RECTANGLE, 
				DriftBehavior.ROTATE_AND_SHRINK, 
				centerX, centerY,
				dx, dy, 
				24 + rand.nextInt(4), 
				30 + rand.nextInt(20), 
				Color.YELLOW,
				null
			);
			
            getWorld().particles().spawn(ParticlePriority.AFTER_ENTITIES, particle);
        }
    }

	public int getMaxHealth() {
		return (int) this.getWatcher().get(Constants.MAX_HP_META_KEY);
	}

	public int getHealth() {
		return (int) this.getWatcher().get(Constants.HP_META_KEY);
	}
}
