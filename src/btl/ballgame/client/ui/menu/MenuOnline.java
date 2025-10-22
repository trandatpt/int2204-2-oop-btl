package btl.ballgame.client.ui.menu;

import java.util.Stack;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import btl.ballgame.client.ui.login.Account;
import btl.ballgame.client.ui.multi.ModeMulti;
import btl.ballgame.client.ui.single.ModeSingle;
import btl.ballgame.client.ui.window.*;


public class MenuOnline extends Window {
    private final Account account;
    private final WindowManager manager;
    private final Label label;
    private final Button singlePlayer;
    private final Button multiPlayer;
    private final Button settings;
    private final Button backlogin;
    private final Button exit;

    public MenuOnline(WindowManager manager, Account account) {
        this.account = account;
        this.manager = manager;
        label        = new Label("Arkanoid");
        singlePlayer = new Button("Single Player");
        multiPlayer  = new Button("Multi Player");
        settings     = new Button("Settings");
        backlogin    = new Button("Back to Login");
        exit         = new Button("Exit");
        setwindowId("menuonlineid");
        setTitle("Menu");
        initUI();
}

    @Override
    public void initUI() {
        // Thiết lập sự kiện
        singlePlayer.setOnAction(e -> {
            ModeSingle single = new ModeSingle(manager, account);
            manager.show(single, single.getTitle(), single.getwindowId());
            manager.print();
        });

        multiPlayer.setOnAction(e -> {
            ModeMulti multi = new ModeMulti(manager, account);
            manager.show(multi, multi.getTitle() , multi.getwindowId());
        });

        settings.setOnAction(e -> {
            Setting setting = new Setting(manager);
            manager.show(setting, setting.getTitle() , setting.getwindowId());
        });

        backlogin.setOnAction(e -> {
            Stack<WindowEntry> window = manager.getHistory();
            while(!window.peek().getWindow().getwindowId().equals("loginid")) {
                manager.back();
            }
        });

        exit.setOnAction(e -> Exit.cancelWindow());

        // Tạo layout
        VBox buttonMenu = new VBox(15, singlePlayer, multiPlayer, settings, backlogin, exit);
        buttonMenu.setAlignment(Pos.CENTER);

        VBox root = new VBox(30, label, buttonMenu);
        root.setAlignment(Pos.CENTER);
        this.getChildren().add(root);
    }
}
