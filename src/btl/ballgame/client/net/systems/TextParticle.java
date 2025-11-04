package btl.ballgame.client.net.systems;

import btl.ballgame.shared.libs.Constants.DriftBehavior;
import btl.ballgame.shared.libs.Constants.ParticleType;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TextParticle extends ParticleSystem.Particle {
	private final String text;
	private final int fadeInTicks;
	private final int persistentTicks;
	private final int fadeOutTicks;
	private final int yOffset;

	// cached metrics
	private final Font font;
	private final Text layoutText;
	private final double textWidth;
	private final double textHeight;
	private final double baselineOffset;

	public TextParticle(String text, Color color, 
		int yOffset, boolean bold, boolean italic, boolean underline,
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
		this.yOffset = yOffset;

		FontWeight weight = bold ? FontWeight.BOLD : FontWeight.NORMAL;
		FontPosture posture = italic ? FontPosture.ITALIC : FontPosture.REGULAR;
		this.font = Font.font(null, weight, posture, 36);

		this.layoutText = new Text(text);
		this.layoutText.setFont(font);
		this.layoutText.setUnderline(underline);

		Bounds bounds = layoutText.getLayoutBounds();
		this.textWidth = bounds.getWidth();
		this.textHeight = bounds.getHeight();
		this.baselineOffset = layoutText.getBaselineOffset();
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
		gc.setGlobalAlpha(alpha);
		gc.setFont(font);
		gc.setFill(color);
		double cw = gc.getCanvas().getWidth();
		double ch = gc.getCanvas().getHeight();
		double centerX = (cw - textWidth) / 2.0;
		double baselineY = ch + (textHeight - baselineOffset) + yOffset;
		gc.fillText(text, centerX, baselineY);
		gc.restore();
	}
}
