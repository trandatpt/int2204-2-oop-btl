package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ClientPlayer;
import btl.ballgame.client.ui.game.GameRenderCanvas;
import btl.ballgame.client.net.systems.CSInterpolatedEntity;
import btl.ballgame.client.net.systems.ITickableCEntity;
import btl.ballgame.protocol.packets.in.PacketPlayInPaddleControl;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.DataWatcher;
import btl.ballgame.shared.libs.Location;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static btl.ballgame.shared.libs.Utils.clamp;

public class CEntityPaddleLocal extends CEntityPaddle implements ITickableCEntity {
	/// TEST ///
	public CEntityPaddleLocal() {
		GameRenderCanvas.local = this;
	}

	@Override
	public void render(GraphicsContext cv) {
		super.render(cv);
		cv.setFill(Color.GREEN);
		cv.fillRect(getRenderX(), getRenderY(), getWidth(), getHeight());
		cv.setFill(Color.BLACK);
	}
	

	@Override
	public void onTick() {
		
	}

	public static boolean isOwnedByThisClient(DataWatcher watcher) {
		ClientPlayer p = ArkanoidGame.core().getPlayer();
		return p.getUniqueId().getLeastSignificantBits() 
			== (long) watcher.get(Constants.PADDLE_OWNER_META)
		;
	}
}
