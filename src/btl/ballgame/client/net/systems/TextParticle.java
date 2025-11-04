package btl.ballgame.client.net.systems;

import btl.ballgame.shared.libs.Constants.DriftBehavior;
import btl.ballgame.shared.libs.Constants.ParticleType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TextParticle extends ParticleSystem.Particle {
	private final String text;
	private final int fadeInTicks;
	private final int persistentTicks;
	private final int fadeOutTicks;

	public TextParticle(String text, Color color,
		int fadeInTicks, int persistentTicks, int fadeOutTicks
	) {
		super(ParticleType.OVAL, DriftBehavior.NONE, 
			0, 0, 0, 0, 99, 
			fadeInTicks + persistentTicks + fadeOutTicks,
			color, null
		);
		this.text = text;
		this.fadeInTicks = fadeInTicks;
		this.persistentTicks = persistentTicks;
		this.fadeOutTicks = fadeOutTicks;;
	}

	@Override
	public void update() {
		super.update();
		int elapsed = initialLife - life;
		if (elapsed < fadeInTicks) {
			// fade in
			alpha = (double) elapsed / fadeInTicks;
		} else if (elapsed < fadeInTicks + persistentTicks) {
			// fully visible
			alpha = 1.0;
		} else {
			// fade out
			int fadeOutElapsed = elapsed - fadeInTicks - persistentTicks;
			alpha = 1.0 - ((double) fadeOutElapsed / fadeOutTicks);
		}
	}

	@Override
	public void render(GraphicsContext gc) {
        // TODO: do this later
	}
}
