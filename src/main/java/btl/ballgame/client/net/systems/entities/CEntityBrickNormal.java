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

public class CEntityBrickNormal extends CSEntity {
	private final Image[] brickStages;
	private int lastHealth;
	private final Random rand = new Random();

	public CEntityBrickNormal() {
		brickStages = new Image[] { 
			atlas().getAsImage("normal_brick", "very_damaged"),
			atlas().getAsImage("normal_brick", "damaged"), 
			atlas().getAsImage("normal_brick", "lightly_cracked"),
			atlas().getAsImage("normal_brick", "intact") 
		};
	}

	@Override
	public void onEntitySpawn() {
		this.lastHealth = getMaxHealth();
	}

	@Override
	public void render(GraphicsContext cv) {
		float healthRatio = Math.max(0f, Math.min(1f, (float) getHealth() / getMaxHealth()));
		int stageIndex = (int) (healthRatio * 3);
		drawTinted(cv, brickStages[stageIndex], 
			getRenderX(), getRenderY(), 
			getRenderWidth(), getRenderHeight(),
			getTint()
		);
		
		// keep track of server-sent data
		int currentHealth = getHealth();
		if (currentHealth < this.lastHealth) {
			spawnDamageParticles(this.lastHealth - currentHealth);
		}
		this.lastHealth = currentHealth;
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
				DriftBehavior.BOTH_WHILE_DRIFTING, 
				centerX, centerY,
				dx, dy, 
				24 + rand.nextInt(4), 
				30 + rand.nextInt(20), 
				TextureAtlas.fromRgbInt(getTint()),
				null
			);
			
            getWorld().particles().spawn(ParticlePriority.AFTER_ENTITIES, particle);
        }
    }

	public int getTint() {
		return (int) this.getWatcher().get(Constants.BRICK_TINT_META);
	}

	public int getMaxHealth() {
		return (int) this.getWatcher().get(Constants.MAX_HP_META_KEY);
	}

	public int getHealth() {
		return (int) this.getWatcher().get(Constants.HP_META_KEY);
	}
}
