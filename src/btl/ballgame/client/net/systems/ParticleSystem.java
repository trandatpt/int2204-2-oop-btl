package btl.ballgame.client.net.systems;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

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
		particles.forEach(particle -> {
			if (particle.isDead()) {
				return;
			}
			particle.update();
			this.renderParticle(cv, particle);
		});
		cv.setGlobalAlpha(1.0);
	}
	
	/**
	 * Renders a single particle
	 *
	 * @param gc canvas
	 * @param particle the particle to render
	 */
	private void renderParticle(GraphicsContext gc, Particle particle) {
		gc.setGlobalAlpha(particle.alpha);
		gc.save();

		// rotate
		if (particle.rotation != 0) {
			gc.translate(particle.x, particle.y); // center the rotation
			gc.rotate(particle.rotation);
			gc.translate(-particle.x, -particle.y);
		}
		
		double halfSize = particle.size / 2f;
		switch (particle.type) {
			case OVAL: {
				gc.setFill(particle.color);
				gc.fillOval(
					particle.x - halfSize, 
					particle.y - halfSize, 
					particle.size, particle.size
				);
				break;
			}
			case RECTANGLE: {
				gc.setFill(particle.color);
				gc.fillRect(
					particle.x - halfSize, 
					particle.y - halfSize, 
					particle.size, particle.size
				);
				break;
			}
			case SPRITE: {
				if (particle.sprite != null) {
					gc.drawImage(
						particle.sprite, 
						particle.x - halfSize, 
						particle.y - halfSize, 
						particle.size, particle.size
					);
				}
				break;
			}
		}
		gc.restore();
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
			this.sprite = null; // TODO DABSHDGASUIDHUYIASHUIDSAH
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
				this.alpha = Math.max(life / initialLife, 0);
				// apply driftint behavior
				switch (driftBehavior) {
				case ROTATING_WHILE_DRIFTING:
					rotation += rotationSpeed;
					break;
				case SHRINK_WHILE_DRIFTING:
					size *= 0.95; // shrink slowly
					break;
				case NONE:
				default:
					break;
				}
			}
		}
		
		public boolean isDead() {
			return life <= 0 || size <= 0;
		}
	}
}
