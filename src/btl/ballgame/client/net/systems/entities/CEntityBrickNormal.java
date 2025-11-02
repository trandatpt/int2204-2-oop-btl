package btl.ballgame.client.net.systems.entities;

import java.util.Random;

import btl.ballgame.client.CSAssets;
import btl.ballgame.client.net.systems.CSEntity;
import btl.ballgame.shared.libs.Constants;
import javafx.scene.canvas.GraphicsContext;

public class CEntityBrickNormal extends CSEntity {
	@Override
	public void render(GraphicsContext cv) {
		drawTinted(cv, CSAssets.ATLAS.brickStages[(int)(((float) getHealth() / (float) getMaxHealth()) * 3f)].getImage(), 
			getRenderX(), getRenderY(), 
			getRenderWidth(), getRenderHeight(), 
		new Random().nextInt());
	}
	
	public int getTint() {
		return (int) this.getWatcher().get(Constants.BRICK_TINT_META);
	}
	
	public int getMaxHealth() {
		return (int) this.getWatcher().get(Constants.MAX_HP_META_KEY);
	}
	
	public int getHealth() {
		return (int) this.getWatcher().get(Constants.HP_META_KEY);
	}
}
