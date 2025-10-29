package btl.ballgame.server.game.buffs;

import java.util.Timer;
import java.util.TimerTask;

import btl.ballgame.server.game.entities.dynamic.EntityPaddle;

public class PaddleExpand {
    private static int EXPANDED_WIDTH = 160;
    private static int EXPANDED_HEIGHT = 18;
    private static int EXPANDED_TIME = 7000;

    public static void active(EntityPaddle paddle) {
        paddle.setBoundingBox(EXPANDED_WIDTH, EXPANDED_HEIGHT);
        System.out.println("[Buff] Paddle Expand Team " + paddle.getTeam());
        
        new Timer().schedule(
            new TimerTask() {
                @Override
                public void run() {
                    paddle.setBoundingBox(72, 18);
                    System.out.println("[Buff] Paddle restored to normal size.");
                }
        }, EXPANDED_TIME);
    }
}
