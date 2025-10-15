package btl.ballgame.client.ui.menu;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import btl.ballgame.client.ui.window.*;


/**
 * 2P gameplay
 */
public class ModeMulti extends Window {
    private final WindowManager manager;
    private final Label label;
    private final Button adventure;
    private final Button online;
    private final Button boss;
    private final Button back;

    public ModeMulti(WindowManager manager) {
        this.manager = manager;
        label = new Label("Multi Player");
        adventure = new Button("Adventure");
        online = new Button("Online 2 vs 2");
        boss = new Button("Boss two player");
        back = new Button("Back to Menu");

        setwindowId("multiid");
        initUI();
    }

    private void ResText() {
        List<Button> button = new ArrayList<>();
        button.add(adventure);
        button.add(online);
        button.add(boss);
        button.add(back);
        ResponsiveText text = new ResponsiveText(button);
        text.apply();
    }

    @Override
    public void initUI() {
        back.setOnAction(e -> {
            manager.back();
        });
        VBox buttonMenu = new VBox(15, adventure, online, boss, back);
        buttonMenu.setAlignment(Pos.CENTER);

        VBox root = new VBox(30, label, buttonMenu);
        root.setAlignment(Pos.CENTER);

        this.getChildren().add(root);
        this.ResText();
    }
}
