package btl.ballgame.client.net.systems;

import btl.ballgame.client.CSAssets;
import btl.ballgame.client.TextureAtlas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Supply information for rendering 
 */
public interface IRenderable {
	int getRenderX();
	int getRenderY();
	int getRenderRotation();
	int getRenderWidth();
	int getRenderHeight();
	
	default TextureAtlas atlas() {
		return CSAssets.sprites;
	}
	
	/**
	 * Draw a rotated image, SDL_RenderCopyEx equivalent
	 */
	default void drawRotated(GraphicsContext gc, Image img, 
		double x, double y, 
		double w, double h,
		int rotation
	) {
		// save the state of the canvas so we can return later
		gc.save();
		// flip vertically around the target's center
		double centerX = x + (w / 2.0);
		double centerY = y + (h / 2.0);
		gc.translate(centerX, centerY); // center the rotation
		gc.rotate(rotation);
		gc.translate(-centerX, -centerY); // restore
		// draw the image
		gc.drawImage(img, x, y, w, h);
		gc.restore();
	}
	
	/**
	 * Draw a tinted image, SDL_SetTextureColorMod (half baked) equivalent
	 */
	default void drawTinted(GraphicsContext gc, Image img, 
		double x, double y, 
		double w, double h, 
		int tint
	) {
		// save the state of the canvas so we can return later
		gc.save();
		// render the image first
		gc.setGlobalBlendMode(BlendMode.SRC_OVER); // normal mode
		gc.drawImage(img, x, y, w, h);
		// set the mode to multiply so that we can tint it by
		// drawing a rectangle over it z(pixC) = pixC * tint
		gc.setGlobalBlendMode(BlendMode.MULTIPLY);
		gc.setFill(TextureAtlas.fromRgbInt(tint));
		gc.fillRect(x, y, w, h);
		// restore the previous state of the GC
		gc.restore();
	}
}
