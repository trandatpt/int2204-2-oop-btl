package btl.ballgame.server.game.buffs;

import static btl.ballgame.shared.libs.Constants.*;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.entities.dynamic.EntityWreckingBall;
import btl.ballgame.shared.libs.Constants.EffectType;

public class EnlargedBall extends BaseEffect {
	private static final int EXPANDED_TIME = 8000;
	
	private EntityWreckingBall ball;
	public EnlargedBall(ArkaPlayer target) {
		super(target);
		target.getCurrentGame().getWorld().getEntities().forEach(e -> {
			if (e instanceof EntityWreckingBall wb && wb.isPrimaryBall() && wb.getOwner() == target) {
				this.ball = wb;
			}
		});
	}
	
	@Override
	public void onEffectActivate() {
		if (ball == null || ball.isDead()) return;
		ball.setBallScale(2);
		ball.getWatcher().watch(BALL_ENLARGED_META, true);
		ball.updateMetadata();
	}
	
	@Override
	public void onEffectDeactivate() {
		if (ball == null || ball.isDead()) return;
		ball.setBallScale(1);
		ball.getWatcher().unwatch(BALL_ENLARGED_META);
		ball.updateMetadata();
	}
	
	@Override
	public int getDuration() {
		return EXPANDED_TIME;
	}

	@Override
	public EffectType getType() {
		return EffectType.ENLARGED_BALL;
	}

	@Override
	public String getName() {
		return "Enlarged Ball";
	}
}
