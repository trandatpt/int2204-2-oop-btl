package btl.ballgame.client.net.systems;

import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Location;
import javafx.scene.canvas.GraphicsContext;

import static btl.ballgame.shared.libs.Utils.*;

public abstract class CSInterpolatedEntity extends CSEntity {
	
	// ---- LOCATION UPDATE LERP (LINEAR INTERPOLATION) ----
	private int oldX, oldY, oldRot;
	private long lastPosUpdateNanos;
	
	// use these to render (x,y is CENTERED, NOT TOP LEFT!!!!!)
	private int renderX, renderY, renderRot;
	
	@Override
	public void onBeforeLocationUpdate() {
		float alpha = getAlpha(lastPosUpdateNanos);
		Location loc = getServerLocation();
		// comp. for the A -> B (interrupted) -> C issue!
		// if a new server update comes in mid-lerp (INTERRUPT), we don't want to jump 
		// straight to B, instead, we grab the current interpolated position
		// and start the next lerp from there
		this.oldX = intLerp(oldX, loc.getX(), alpha);
		this.oldY = intLerp(oldY, loc.getY(), alpha);
		this.oldRot = intLerp(oldRot, loc.getRotation(), alpha);
		this.lastPosUpdateNanos = System.nanoTime();
	}

	// ---- DIMENSION UPDATE LERP (LINEAR INTERPOLATION) ----
	private int oldWidth, oldHeight;
	private long lastDimUpdateNanos;
	
	// use these to render (w,h FROM TOP LEFT)
	private int renderWidth, renderHeight;
	
	@Override
	public void onBeforeBBSizeUpdate() {
		float alpha = getAlpha(lastDimUpdateNanos);
		// same as before
		this.oldWidth = intLerp(oldWidth, getWidth(), alpha);
		this.oldHeight = intLerp(oldHeight, getHeight(), alpha);
		this.lastDimUpdateNanos = System.nanoTime();
	}
	
	// ---- RENDER LOOP ----
	protected final void computeLerps() {
		float posAlpha = getAlpha(lastPosUpdateNanos);
		Location loc = getMutableServerLocation(); // faster, prevent object creation
		this.renderX = intLerp(oldX, loc.getX(), posAlpha);
		this.renderY = intLerp(oldY, loc.getY(), posAlpha);
		this.renderRot = intLerp(oldRot, loc.getRotation(), posAlpha);
		
		float dimAlpha = getAlpha(lastDimUpdateNanos);
		// same as before
		this.renderWidth = intLerp(oldWidth, getWidth(), dimAlpha);
		this.renderHeight = intLerp(oldHeight, getHeight(), dimAlpha);
	}
	
	// helper
	protected float getAlpha(long last) {
		return Math.min(1.0f, 
			(System.nanoTime() - last) / (Constants.NS_PER_TICK * 1.f)
		);
	}
	
	// --- HIER CLASS SIGNATURE ---
	// supply the renderer with easy-to-access LERP'ed data
	// for rendering smooth visuals (standardized)
	@Override
	public int getRenderX() {
		return this.renderX - (this.renderWidth >> 1);
	}
	
	@Override
	public int getRenderY() {
		return this.renderY - (this.renderHeight >> 1);
	}
	
	@Override
	public int getRenderRotation() {
		return this.renderRot;
	}
	
	@Override
	public int getRenderWidth() {
		return this.renderWidth;
	}
	
	@Override
	public int getRenderHeight() {
		return this.renderHeight;
	}
	
	@Override
	public void render(GraphicsContext cv) {
		this.computeLerps();
	}
}
