package btl.ballgame.client.net.systems;

import btl.ballgame.shared.libs.Constants.DriftBehavior;
import btl.ballgame.shared.libs.Constants.ParticleType;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
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
	
	private double opacity;

	public TextParticle(String text, Color color, int size, 
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
		this.fadeOutTicks = fadeOutTicks;
		this.yOffset = yOffset;

		FontWeight weight = bold ? FontWeight.BOLD : FontWeight.NORMAL;
		FontPosture posture = italic ? FontPosture.ITALIC : FontPosture.REGULAR;
		this.font = Font.font("Segoe UI Symbol, Noto Sans Symbols, System", weight, posture, size);

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
			this.opacity = fadeInTicks > 0 ? (double) elapsed / fadeInTicks : 1.0;
		} else if (elapsed < fadeInTicks + persistentTicks) {
			this.opacity = 1.0;
		} else {
			int fadeOutElapsed = elapsed - fadeInTicks - persistentTicks;
			this.opacity = fadeOutTicks > 0 ? 1.0 - ((double) fadeOutElapsed / fadeOutTicks) : 0.0;
		}
		this.opacity = Math.max(0.0, Math.min(1.0, this.opacity));
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.save();
		gc.setGlobalAlpha(opacity);
		gc.setFont(font);
		
		DropShadow ds = new DropShadow();
		ds.setRadius(5);
		ds.setOffsetX(2);
		ds.setOffsetY(2);
		ds.setColor(javafx.scene.paint.Color.color(0, 0, 0, 0.8));
		gc.setEffect(ds);
		gc.setFill(color);
		
		double cw = gc.getCanvas().getWidth();
		double ch = gc.getCanvas().getHeight();
		double centerX = (cw - textWidth) / 2.0;
		double baselineY = ch / 2.0 + (textHeight / 2.0 - baselineOffset) + yOffset;
		gc.fillText(text, centerX, baselineY);
		gc.setEffect(null);
		
		double darkFactor = 0.65; // 60% brightness
		Color strokeColor = new Color(
			color.getRed() * darkFactor, 
			color.getGreen() * darkFactor,
			color.getBlue() * darkFactor, 
			1
		);
		gc.setLineWidth(2); 
		gc.setStroke(strokeColor);
		gc.strokeText(text, centerX, baselineY);
		
		gc.restore();
	}
	
	public int getyOffset() {
		return yOffset;
	}
	
	public String getText() {
		return text;
	}
}
