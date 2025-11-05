package btl.ballgame.client.ui.game;

import btl.ballgame.client.ui.screen.Screen;
import javafx.scene.layout.StackPane;

public class GameCompositeScreen extends Screen {
    private Screen ui = null;
    private Screen grc = null;
    public GameCompositeScreen(Screen gameScreen, Screen gameRenderCanvas) {
        super("game");
        this.ui = gameScreen;
        this.grc = gameRenderCanvas;

        gameScreen.setMouseTransparent(true);
        gameScreen.setStyle("-fx-background-color: transparent;");
        
        StackPane root = new StackPane(grc, ui);
        this.getChildren().add(root);
    }

    @Override
    public void onInit() {
        if (grc != null) {
           grc.onInit();
        }
        if (ui != null) {
            ui.onInit();
        }
    }

    @Override
    public void onRemove() {

    }
}
