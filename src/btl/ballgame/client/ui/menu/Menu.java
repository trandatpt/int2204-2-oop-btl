package btl.ballgame.client.ui.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import btl.ballgame.client.ui.window.*;


public class Menu extends Window {
    private final WindowManager manager;
    private final Label label;
    private final Button singlePlayer;
    private final Button multiPlayer;
    private final Button settings;
    private final Button link;
    private final Button score;
    private final Button backlogin;
    private final Button exit;

    public Menu(WindowManager manager) {
        this.manager = manager;
        label        = new Label("Arkanoid");
        singlePlayer = new Button("Single Player");
        multiPlayer  = new Button("Multi Player");
        settings     = new Button("Settings");
        link         = new Button("Fanpage");
        score        = new Button("History Score");
        backlogin    = new Button("Back to Login");
        exit         = new Button("Exit");
        initUI();
}

    private void ResText() {
        List<Button> buttons = new ArrayList<>();
        buttons.add(singlePlayer);
        buttons.add(multiPlayer);
        buttons.add(settings);
        buttons.add(link);
        buttons.add(backlogin);
        buttons.add(exit);
        ResponsiveText text = new ResponsiveText(buttons);
        text.apply();
    }

    @Override
    public void initUI() {
        // Thiết lập sự kiện
        singlePlayer.setOnAction(e -> {
            ModeSingle single = new ModeSingle(manager);
            manager.show(single, "Single");
        });

        multiPlayer.setOnAction(e -> {
            ModeMulti multi = new ModeMulti(manager);
            manager.show(multi, "Multi Player");
        });

        settings.setOnAction(e -> {
            Setting setting = new Setting(manager);
            manager.show(setting, "Settings");
        });

        link.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(
                        new java.net.URI("https://www.facebook.com/tran.viet.212574")
                    );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        score.setOnAction(e -> {

        });

        backlogin.setOnAction(e -> {
            Stack<WindowEntry> window = manager.getHistory();
            while(!window.peek().getTitle().equals("Login")) {
                manager.back();
            }
        });

        exit.setOnAction(e -> Exit.cancelWindow());

        // Tạo layout
        VBox buttonMenu = new VBox(15, singlePlayer, multiPlayer, settings, link, backlogin , exit);
        buttonMenu.setAlignment(Pos.CENTER);

        VBox root = new VBox(30, label, buttonMenu);
        root.setAlignment(Pos.CENTER);
        this.getChildren().add(root);
        this.ResText();
    }
}
