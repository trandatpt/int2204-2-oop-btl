package btl.ballgame.server.game.buffs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.server.game.WorldServer;
import btl.ballgame.server.game.entities.dynamic.EntityWreckingBall;
import btl.ballgame.shared.libs.Constants.EffectType;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

public class MultiBall extends BaseEffect {
	static final int BALLS_SPAWN = 3;
	
	private EntityWreckingBall root;
	private List<EntityWreckingBall> spawnedBalls = new ArrayList<>();
	
	public MultiBall(ArkaPlayer target) {
		super(target);
		target.getCurrentGame().getWorld().getEntities().forEach(e -> {
			if (e instanceof EntityWreckingBall wb && wb.isPrimaryBall() && wb.getOwner() == target) {
				this.root = wb;
			}
		});
	}

	@Override
	public void onEffectActivate() {
		var match = getTarget().getCurrentGame();
		if (root == null || root.isDead()) return;
		WorldServer world = match.getWorld();
		Random rand = world.random;
		Location rootLoc = root.getLocation();
		
		for (int i = 0; i < BALLS_SPAWN; i++) {
			float radAngle = (float) (rand.nextFloat() * Math.PI * 2);
			Vector2f newDir = Vector2f.fromTheta(radAngle);
	        Location spawnLoc = rootLoc.clone().add(
	        	newDir.normalize().multiply(root.getWidth())
	        ); // seperate the balls
	        spawnLoc.setDirection(newDir);
	        
	        // spawn the clones
	        EntityWreckingBall ball = new EntityWreckingBall(world.nextEntityId(), spawnLoc);
	        ball.setOwner(root.getOwner());
	        ball.setPrimaryBall(false);
	        spawnedBalls.add(ball);
	        
	        world.runNextTick(() -> world.addEntity(ball));
		}
	}
	
	@Override
	public void onEffectDeactivate() {
		spawnedBalls.forEach(WorldEntity::remove);
	}

	@Override
	public int getDuration() {
		return 12_000;
	}
	
	@Override
	public EffectType getType() {
		return EffectType.MULTI_BALL;
	}

	@Override
	public String getName() {
		return "Multi Balls";
	}
}
