package btl.ballgame.client.ui.window;
/**
 * class WindowEntry cÃ³ thuá»™c tÃ­nh window vÃ  title Ä‘á»ƒ lÆ°u 2 thuá»™c tÃ­nh Ä‘Ã³ vá»›i má»¥c Ä‘Ã­ch back láº¡i mÃ n hÃ¬nh hoáº·c táº¡o history vá»›i tiÃªu Ä‘á»� vÃ  ná»™i dung
 */
public class WindowEntry {
    private Window window;
    private String title;

    public WindowEntry(Window window, String title, String id) {
        this.window = window;
        this.title = title;
        this.window.setWindowId(id);
    }

    public Window getWindow() {
        return this.window;
    }

    public String getTitle() {
        return this.title;
    }
}
