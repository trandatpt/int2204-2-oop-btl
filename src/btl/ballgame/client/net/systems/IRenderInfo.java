package btl.ballgame.client.net.systems;

import btl.ballgame.client.CSAssets;
import btl.ballgame.client.TextureAtlas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Supply information for rendering 
 */
public interface IRenderInfo {
	int getRenderX();
	int getRenderY();
	int getRenderRotation();
	int getRenderWidth();
	int getRenderHeight();
	
	default TextureAtlas atlas() {
		return CSAssets.sprites;
	}
	
	/**
	 * Draw a tinted image, SDL_SetTextureColorMod equivalent
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
		gc.setFill(Color.rgb( // rgb to jfx color
			(tint >> 16) & 0xFF, 
			(tint >> 8) & 0xFF, 
			tint & 0xFF)
		);
		gc.fillRect(x, y, w, h);
		// restore the previous state of the GC
		gc.restore();
	}
}
