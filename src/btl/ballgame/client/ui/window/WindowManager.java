package btl.ballgame.client.ui.window;

import java.util.Stack;
import javafx.stage.Stage;
/**
 * class WindowManager tạo ra cơ chế quay lại màn hình hoặc show màn hình mới
 */
public class WindowManager {
    private final Stage stage;
    private final Stack<WindowEntry> history; 

    public WindowManager(Stage stage) {
        this.stage = stage;
        this.history = new Stack<>();
    }

    /**
     * show ra màn hình với nội dung, title mới và add History vào Stack để có thể back lại
     * @param window
     * @param title
     */
    public void show(Window window, String title, String id) {
        stage.setTitle(title);
        history.push(new WindowEntry(window, title, id));
        stage.setScene(window.getWindowScene());
        stage.show();
    }

    public void back() {
        if (!history.empty()) {
            history.pop();
            if (!history.empty()) {
                WindowEntry prev = history.peek();
                stage.setScene(prev.getWindow().getWindowScene());
                stage.setTitle(prev.getTitle());
                stage.show();
            }
        }
    }

    public Stack<WindowEntry> getHistory() {
        return this.history;
    }
}
