package btl.ballgame.client.ui.window;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
/**
 * abstract class window có 1 nhiệm vụ duy nhất là tạo nội dung rồi đẩy vào scene
 */
public abstract class Window extends StackPane {
    private String id;
    private final Scene scene;

    protected Window() {
        this.id = "";
        this.scene = new Scene(this, 800, 600);
    }

    /**
     *lấy Scene(nội dung trong window)
     * @return
     */
    public Scene getWindowScene() {
        return this.scene;
    }

    public void setwindowId(String id) {
        this.id = id;
    }

    public String getwindowId() {
        return this.id;
    }
    public abstract void initUI();
}

