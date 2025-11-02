package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.net.systems.CSInterpolatedEntity;
import btl.ballgame.client.net.systems.ParticleSystem;
import btl.ballgame.shared.libs.Constants.ParticlePriority;
import btl.ballgame.shared.libs.Constants.ParticleType;
import btl.ballgame.shared.libs.Constants.DriftBehavior;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class CEntityWreckingBall extends CSInterpolatedEntity {
	private Image ballImage;
	
	public CEntityWreckingBall() {
		this.ballImage = atlas().getAsImage("ball", "ball_normal");
	}

	@Override
	public void render(GraphicsContext cv) {
		super.render(cv); // interpolation
		// draw the ball
		cv.drawImage(ballImage, 
			getRenderX(), getRenderY(), 
			getRenderWidth(), getRenderHeight()
		);
		
		// spawn particles after the ball
		ParticleSystem.Particle trail = new ParticleSystem.Particle(ParticleType.RECTANGLE,
			DriftBehavior.ROTATING_WHILE_DRIFTING, 
			getRenderX() + getRenderWidth() / 2,
			getRenderY() + getRenderHeight() / 2,
			(Math.random() - 0.5) * 2, // small random horizontal drift
			(Math.random() - 0.5) * 2, // small random vertical drift
			8, // particle size
			10, // lifetime of this particle, 1/3s
			Color.rgb(250, 196, 0, 0.5), // firey orange
			null
		);

		getWorld().particles().spawn(ParticlePriority.BEFORE_ENTITIES, trail);
	}
}

