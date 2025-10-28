package btl.ballgame.server.game.buffs;

import btl.ballgame.server.game.entities.dynamic.EntityWreckingBall;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;

public class MultiBall {

    public static void active(EntityWreckingBall ball) {
            EntityWreckingBall ballClone = new EntityWreckingBall(
                ball.getWorld().nextEntityId(),
                new Location(ball.getWorld(),
                            ball.getLocation().getX(),
                            ball.getLocation().getY(),
                            ball.getLocation().getDirection()
                )
            );

            ballClone.getLocation().setRotation(20);
            Vector2f cloneDirection = ballClone.getLocation().getDirection();
            ballClone.setDirection(cloneDirection);
            ballClone.setPrimaryBall(false);
    }
}
