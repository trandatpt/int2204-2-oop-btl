package btl.ballgame.client.ui.menu;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import btl.ballgame.client.ui.window.*;

/*
 * 1p gameplay
 */
public class ModeSingle extends Window{
    private final WindowManager manager;
    private final Label label;
    private final Button adventure;
    private final Button online;
    private final Button boss;
    private final Button back;
    
    public ModeSingle(WindowManager manager) {
        this.manager = manager;
        label = new Label("Single Player");
        adventure = new Button("Adventure");
        online = new Button("Online 1 vs 1");
        boss = new Button("Boss one player");
        back = new Button("Back to Menu");

        setwindowId("singleid");
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
        VBox root = new VBox(15, adventure, online, boss, back);
        root.setAlignment(Pos.CENTER);

        VBox single = new VBox(100, label, root);
        single.setAlignment(Pos.CENTER);

        this.getChildren().add(single);
        this.ResText();
    }
    
}
