package btl.ballgame.client.ui.window;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public abstract class Window extends StackPane {
    private String title;
    private String id;
    private final Scene scene;

    protected Window() {
        this.title = "";
        this.id = "";
        this.scene = new Scene(this, 1280, 720);
    }

    public Scene getWindowScene() {
        return this.scene;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
    
    public void setWindowId(String id) {
        this.id = id;
    }

    public String getWindowId() {
        return this.id;
    }

    public void delay(double seconds, Runnable action) {
        javafx.animation.PauseTransition d =
            new javafx.animation.PauseTransition(javafx.util.Duration.seconds(seconds));
        d.setOnFinished(e -> action.run());
        d.play();
    }
    
    public abstract void initUI();
}

