package btl.ballgame.client.net.systems;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import btl.ballgame.client.CSAssets;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.DriftBehavior;
import btl.ballgame.shared.libs.Constants.ParticlePriority;
import btl.ballgame.shared.libs.Constants.ParticleType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class ParticleSystem {
    /**
     * ParticleCompound is a compact data holder for spawning shit
     */
	private static record ParticleCompound(ParticlePriority priority, Particle particle) {}
	
	/**
	 * Map storing particles grouped by priority.
	 */
	private final Map<ParticlePriority, List<Particle>> particlesStorage = new ConcurrentHashMap<>();
	
	/**
	 * The queue of particles pending spawn. This is for spawning shit from another thread.
	 */
	private final Queue<ParticleCompound> spawnQueue = new ArrayDeque<>();
	
	/**
	 * Spawn a particle to the world.
	 *
	 * @param priority the layer at which the particle should be rendered
	 * @param p the particle to be spawned
	 */
	public void spawn(ParticlePriority priority, Particle p) {
		spawnQueue.add(new ParticleCompound(priority, p));
	}
	
	/**
	 * Flushes the spawn queue, adding all queued particles into the main storage.
	 * Should be called on the update thread before processing existing particles.
	 */
	public void flushQueue() {
		ParticleCompound compound;
		while ((compound = spawnQueue.poll()) != null) {
			particlesStorage.computeIfAbsent(
				compound.priority, 
				k -> new ArrayList<>())
			.add(compound.particle);
		}
	}
	
	/**
	 * Renders all particles of a given priority, this also updates the particles.
	 *
	 * @param priority the layer of particles to render
	 * @param cv       the canvas
	 */
	public void render(ParticlePriority priority, GraphicsContext cv) {
		var particles = particlesStorage.get(priority);
		if (particles == null) return;
		particles.removeIf(Particle::isDead);
		particles.forEach(particle -> {
			if (particle.isDead()) {
				return;
			}
			particle.update();
			particle.render(cv);
		});
		cv.setGlobalAlpha(1.0);
	}
    
	/**
	 * Represents a single client side particle in the system with various shit.
	 */
	public static class Particle {
		public ParticleType type;
		public DriftBehavior driftBehavior;
		public int initialLife;
		
		// positional and velocity data
		public double x, y, dx, dy;
		public double size;
		public double alpha; // opacity
		public int life; // remaining lifetime, in client ticks

		public double rotation; // current rotation
		public double rotationSpeed; // degrees per frame
				
		// K O L O R (for simple particles)
		public Color color;
		// sprite for more complex particles
		public Image sprite;
		
		private long lastUpdate = 0;
		
		/**
		 * Represent a single Particle
		 * @param type the type of the particle
		 * @param driftBehavior what should it do when it drifts
		 * @param x initial x
		 * @param y initial y
		 * @param dx velocity x
		 * @param dy velocity y 
		 * @param size how big it is (in pixels)
		 * @param life how long should it last
		 * @param color KOLOR
		 * @param sprite the sprite if in sprite mode (wtf)
		 */
		public Particle(
			ParticleType type, DriftBehavior driftBehavior,
			double x, double y, 
			double dx, double dy, 
			int size, int life, 
			Color color,
			String sprite
		) {
			this.x = x; this.y = y;
			this.dx = dx; this.dy = dy;
			this.size = size;
			this.life = this.initialLife = life;
			this.alpha = 1.0;
			this.color = color; this.type = type;
			this.driftBehavior = driftBehavior;
			if (sprite != null) {
				this.sprite = CSAssets.sprites.getAsImage("particle", sprite);
			}
			this.rotation = 0;
			this.rotationSpeed = (Math.random() - 0.5) * 5; // random rotation speed
		}
		
		public void update() {
			// only update every 1 tick
			if (System.currentTimeMillis() - lastUpdate >= Constants.MS_PER_TICK) {
				this.life--;
				lastUpdate = System.currentTimeMillis();
				
				// velocity and allat
				this.x += dx; this.y += dy;
				this.alpha = Math.max((float) life / initialLife, 0);
				// apply "while drifting" behavior
				if (driftBehavior.rotates) this.rotation += rotationSpeed;
				if (driftBehavior.shrinks) this.size *= 0.9;
				if (driftBehavior.grows) this.size *= 1.05;
			}
		}
		
		public boolean isDead() {
			return life <= 0 || size <= 0;
		}
		
		/**
		 * Renders this particle
		 *
		 * @param gc canvas
		 */
		public void render(GraphicsContext gc) {
			gc.setGlobalAlpha(this.alpha);
			gc.save();

			// rotate
			if (this.rotation != 0) {
				gc.translate(x, y); // center the rotation
				gc.rotate(this.rotation);
				gc.translate(-x, -y);
			}
			
			double halfSize = this.size / 2f;
			switch (this.type) {
				case OVAL: {
					gc.setFill(this.color);
					gc.fillOval(
						x - halfSize, 
						y - halfSize, 
						size, size
					);
					break;
				}
				case RECTANGLE: {
					gc.setFill(color);
					gc.fillRect(
						x - halfSize, 
						y - halfSize, 
						size, size
					);
					break;
				}
				case SPRITE: {
					if (sprite != null) {
						gc.drawImage(
							this.sprite, 
							x - halfSize, 
							y - halfSize, 
							size, size
						);
					}
					break;
				}
			}
			gc.restore();
		}
	}
}
