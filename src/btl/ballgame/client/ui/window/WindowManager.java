package btl.ballgame.client.ui.window;

import java.util.Stack;
import javafx.stage.Stage;

public class WindowManager {
    private final Stage stage;
    private final Stack<WindowEntry> history; 

    public WindowManager(Stage stage) {
        this.stage = stage;
        this.history = new Stack<>();
    }

    public void show(Window window, String title, String id) {
        window.setWindowId(id);
        stage.setTitle(title);
        history.push(new WindowEntry(window, title, id));
        System.out.println(">>> pushed: " + id + " | size = " + history.size());
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

    public void print() {
        Stack<WindowEntry> checkwindow = (Stack<WindowEntry>) history.clone(); // clone để không ảnh hưởng history
        for (WindowEntry entry : checkwindow) {
            System.out.print(entry.getWindow().getWindowId() + " ");
        }
        System.out.println();
    }
}
