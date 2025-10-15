package btl.ballgame.client.ui.window;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
/**
 * abstract class window có 1 nhiệm vụ duy nhất là tạo nội dung rồi đẩy vào scene
 */
public abstract class Window extends StackPane {
    private final Scene scene;

    protected Window() {
        this.scene = new Scene(this, 800, 600);
    }

    public Scene getWindowScene() {
        return this.scene;
    }

    public abstract void initUI();
}

