package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.TextureAtlas;
import btl.ballgame.client.net.systems.CSInterpolatedEntity;
import btl.ballgame.client.net.systems.ParticleSystem;
import btl.ballgame.shared.libs.Constants.DriftBehavior;
import btl.ballgame.shared.libs.Constants.ParticlePriority;
import btl.ballgame.shared.libs.Constants.ParticleType;
import btl.ballgame.shared.libs.Location;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class CEntityAKBullet extends CSInterpolatedEntity {
	private Image bullet;
	
	public CEntityAKBullet() {
		this.bullet = atlas().getAsImage("other_entities", "bullet");
	}
	
	@Override
	public void onEntitySpawn() {
		Location l = getMutableServerLocation();
		int x = l.getX(), y = l.getY();
		float w = getBoundingBox().getWidth(), h = getBoundingBox().getHeight();
		
	    // Create a quick muzzle flash particle at bullet origin
	    ParticleSystem.Particle flash = new ParticleSystem.Particle(
	        ParticleType.OVAL, // bright circular flash
	        DriftBehavior.GROW_WHILE_DRIFTING,  // stays still
	        x + w / 2,
	        y + h / 2,
	        0, 0,                // no drift
	        (int) (15 + Math.random() * 6), // random size between 10–16px
	        2 + (int)(Math.random() * 2), // lasts 2–3 ticks
	        TextureAtlas.fromRgbInt(0xFFD33C), // bright yellow-orange
	        null
	    );

	    // Optionally spawn a secondary "spark" or two
	    for (int i = 0; i < 2; i++) {
	        ParticleSystem.Particle spark = new ParticleSystem.Particle(
	            ParticleType.RECTANGLE,
	            DriftBehavior.ROTATING_WHILE_DRIFTING,
	            x + w / 2,
	            y  + h / 2,
	            (Math.random() - 0.5) * 2.0, // small random velocity
	            (Math.random() - 0.5) * 2.0,
	            (int) (5 + Math.random() * 2),  // tiny sparks
	            6,                      // fade fast
	            TextureAtlas.fromRgbInt(0xFFF59E), // pale yellow
	            null
	        );
	        getWorld().particles().spawn(ParticlePriority.AFTER_ENTITIES, spark);
	    }

	    // Spawn flash itself
	    getWorld().particles().spawn(ParticlePriority.AFTER_ENTITIES, flash);
	}
	
	@Override
	public void render(GraphicsContext cv) {
		super.render(cv); // interpolation
		drawRotated(cv, bullet, 
			getRenderX(), getRenderY(), 
			getWidth(), getHeight(), 
			getRenderRotation() + 90
		);
		ParticleSystem.Particle trail = new ParticleSystem.Particle(ParticleType.RECTANGLE,
			DriftBehavior.ROTATING_WHILE_DRIFTING, 
			getRenderX() + getRenderWidth() / 2,
			getRenderY() + getRenderHeight() / 2,
			(Math.random() - 0.5) * .5, // small random horizontal drift
			(Math.random() - 0.5) * .5, // small random vertical drift
			4, // particle size
			8, // 8 ticks
			TextureAtlas.fromRgbInt(0x5e5e5e),
			null
		);
		getWorld().particles().spawn(ParticlePriority.BEFORE_ENTITIES, trail);
	}
}