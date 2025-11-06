package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.net.systems.CSInterpolatedEntity;
import btl.ballgame.client.net.systems.ParticleSystem;
import btl.ballgame.shared.libs.Constants.ParticlePriority;
import btl.ballgame.shared.libs.Constants.ParticleType;
import btl.ballgame.shared.libs.Constants.TeamColor;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.DriftBehavior;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class CEntityWreckingBall extends CSInterpolatedEntity {
	private Image ballImage;
	private boolean primary;
	private boolean renderUpsideDown;
	
	public CEntityWreckingBall() {
		this.ballImage = atlas().getAsImage("ball", "ball_normal");
		this.renderUpsideDown = ArkanoidGame.core().getActiveMatch().getCurrentTeam() == TeamColor.BLUE;
	}
	
	@Override
	public void onEntitySpawn() {
		super.onEntitySpawn();
		this.primary = this.isPrimaryBall();
	}
	
	@Override
	public void render(GraphicsContext cv) {
		super.render(cv); // interpolation
		cv.save();
		if (renderUpsideDown) { // if the item falls up (blue), flip it
			// flip vertically around the item's center
			double centerY = getRenderY() + (getRenderHeight()) / 2.0;
			cv.translate(0, centerY); // center the flip
			cv.scale(1, -1);
			cv.translate(0, -centerY); // restore
		}
		
		// draw the ball
		cv.drawImage(ballImage, 
			getRenderX(), getRenderY(), 
			getRenderWidth(), getRenderHeight()
		);
		
		cv.restore();
		if (!primary) return;
		// spawn particles after the ball
		ParticleSystem.Particle trail = new ParticleSystem.Particle(ParticleType.RECTANGLE,
			DriftBehavior.ROTATING_WHILE_DRIFTING, 
			getRenderX() + getRenderWidth() / 2,
			getRenderY() + getRenderHeight() / 2,
			(Math.random() - 0.5) * 2, // small random horizontal drift
			(Math.random() - 0.5) * 2, // small random vertical drift
			9, // particle size
			10, // lifetime of this particle, 1/3s
			Color.rgb(250, 196, 0, 0.5), // firey orange
			null
		);
		for (int i = 0; i < 2; i++) {
			getWorld().particles().spawn(ParticlePriority.BEFORE_ENTITIES, trail);
		}
	}
	
	public boolean isPrimaryBall() {
		return (boolean) getWatcher().get(Constants.BALL_PRIMARY_META);
	}
}

