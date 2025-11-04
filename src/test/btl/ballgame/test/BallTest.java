package test.btl.ballgame.test;

import static org.junit.Assert.*;

import org.junit.Test;

import btl.ballgame.server.game.entities.dynamic.EntityWreckingBall;
import btl.ballgame.shared.libs.Location;

public class BallTest {
    private EntityWreckingBall ball;
    
    @Test
    public void testPrimaryBall() {
        ball = new EntityWreckingBall(1, new Location(100, 100, 0));
        // check secondaryBall == true
        assertEquals(ball.isPrimaryBall(), false);

        // check secondaryBall == false
        ball.setPrimaryBall(true);
        assertEquals(ball.isPrimaryBall(), true);

        // check secondaryBall == true
        ball.setPrimaryBall(false);
        assertEquals(ball.isPrimaryBall(), false);
    }

}
