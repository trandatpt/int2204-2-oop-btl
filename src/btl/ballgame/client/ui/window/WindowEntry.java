package btl.ballgame.client.ui.window;
/**
 * class WindowEntry có thuộc tính window và title để lưu 2 thuộc tính đó với mục đích back lại màn hình hoặc tạo history với tiêu đề và nội dung
 */
public class WindowEntry {
    private Window window;
    private String title;

    public WindowEntry(Window window, String title) {
        this.window = window;
        this.title = title;
    }

    public Window getWindow() {
        return this.window;
    }

    public String getTitle() {
        return this.title;
    }
}
