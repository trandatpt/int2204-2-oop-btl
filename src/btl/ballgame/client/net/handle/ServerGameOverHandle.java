package btl.ballgame.client.net.handle;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.net.CServerConnection;
import btl.ballgame.client.ui.game.GameOverScreen;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.packets.out.PacketPlayOutGameOver;
import javafx.application.Platform;

/**
 * Handles the {@link PacketPlayOutGameOver} packet sent from the server
 * when the match has concluded.
 * This handler is responsible for stopping the current game loop and displaying
 * the appropriate game over screen (Victory/Defeat or Solo Stats).
 */
public class ServerGameOverHandle implements PacketHandler<PacketPlayOutGameOver, CServerConnection> {

    /**
     * Handles the incoming game over packet.
     * This method must run on the JavaFX Application Thread via {@link Platform#runLater(Runnable)}.
     *
     * @param packet  The {@link PacketPlayOutGameOver} containing match results.
     * @param context The server connection context.
     */
    @Override
    public void handle(PacketPlayOutGameOver packet, CServerConnection context) {
        ArkanoidClientCore client = context.client;

        // Validation to ensure the client is in a valid state
        if (client.getActiveMatch() == null) {
            context.closeWithNotify("Invalid client-server synchronization state!");
            return;
        }

        // Switch to the JavaFX Application Thread to manipulate the UI
        Platform.runLater(() -> {
            try {
                // 1. Get the current active game screen (e.g., GameScreen or GameScreenSolo)
                Screen currentGameScreen = ArkanoidGame.manager().getCurrentScreen();

                if (currentGameScreen != null) {
                    // 2. Stop the game loop by calling onRemove()
                    // This will stop the AnimationTimer inside the screen.
                    currentGameScreen.onRemove();
                }

                // 3. Create the new GameOverScreen using the packet data
                GameOverScreen gameOverScreen = new GameOverScreen(packet);

                // 4. Add the GameOverScreen as an overlay on top of the (now frozen) game screen
                ArkanoidGame.manager().setScreen(gameOverScreen);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}