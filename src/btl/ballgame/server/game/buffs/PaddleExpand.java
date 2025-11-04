package btl.ballgame.server.game.buffs;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.entities.dynamic.EntityPaddle;
import static btl.ballgame.shared.libs.Constants.*;

public class PaddleExpand extends BaseEffect {
	private static final int EXPANDED_TIME = 7000;
	private static final int EXPANDED_WIDTH = 15;
	
	private EntityPaddle paddle;
	public PaddleExpand(ArkaPlayer target) {
		super(target);
		this.paddle = target.getCurrentGame().paddleOf(target);
	}
	
	@Override
	public void onEffectActivate() {
		if (paddle == null || paddle.isDead()) return;
		paddle.setBoundingBox(PADDLE_WIDTH + EXPANDED_WIDTH, PADDLE_HEIGHT);
		paddle.getWatcher().watch(PADDLE_EXPANDED_META, true);
		paddle.updateMetadata();
	}
	
	@Override
	public void onEffectDeactivate() {
		if (paddle == null || paddle.isDead()) return;
		paddle.setBoundingBox(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.getWatcher().unwatch(PADDLE_EXPANDED_META);
		paddle.updateMetadata();
	}
	
	@Override
	public int getDuration() {
		return EXPANDED_TIME;
	}

	@Override
	public EffectType getType() {
		return EffectType.PADDLE_EXPAND;
	}
}
