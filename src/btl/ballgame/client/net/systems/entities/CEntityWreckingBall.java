package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.net.systems.CSInterpolatedEntity;
import btl.ballgame.client.net.systems.ParticleSystem;
import btl.ballgame.shared.libs.Constants.ParticlePriority;
import btl.ballgame.shared.libs.Constants.ParticleType;
import btl.ballgame.shared.libs.Constants.DriftBehavior;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CEntityWreckingBall extends CSInterpolatedEntity {

    @Override
    public void render(GraphicsContext cv) {
        super.render(cv);

        double renderX = getRenderX();
        double renderY = getRenderY();
        double renderWidth = getRenderWidth();
        double renderHeight = getRenderHeight();
        double spread = 8; // max distance from center

        // Draw the ball
        // Spawn a particle trail
        ParticleSystem.Particle trail = new ParticleSystem.Particle(
            ParticleType.RECTANGLE,
            DriftBehavior.ROTATING_WHILE_DRIFTING,
            renderX + renderWidth / 2 + (Math.random() - 0.5) * spread * 2, // random X offset
            renderY + renderHeight / 2 + (Math.random() - 0.5) * spread * 2, // random Y offset
            (Math.random() - 0.5) * 2, // small random horizontal drift
            (Math.random() - 0.5) * 2, // small random vertical drift
            8,    // particle size
            30,   // lifetime in ticks
            Color.rgb(250, 196, 0, 0.5), // semi-transparent white
            null  // no sprite
        );
        
        getWorld().particles().spawn(ParticlePriority.AFTER_ENTITIES, trail);
        cv.drawImage(atlas().ball.getImage(), renderX, renderY, renderWidth, renderHeight);
    }
}
