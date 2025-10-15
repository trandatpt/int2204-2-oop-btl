package btl.ballgame.client.ui.single;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import btl.ballgame.client.ui.menu.ResponsiveText;
import btl.ballgame.client.ui.window.*;

/*
 * 1p gameplay
 */
public class ModeSingle extends Window{
    private final WindowManager manager;
    private final Label label;
    private final Button createroom;
    private final Button back;
    
    public ModeSingle(WindowManager manager) {
        this.manager = manager;
        label = new Label("Single");
        createroom = new Button("Create Single Room");
        back = new Button("Back to Menu");

        setwindowId("modesingleid");
        setTitle("Single");
        initUI();
    }

    private void ResText() {
        List<Button> button = new ArrayList<>();
        button.add(createroom);
        button.add(back);
        ResponsiveText text = new ResponsiveText(button);
        text.apply();
    }

    @Override
    public void initUI() {
        createroom.setOnAction(e -> {
            RoomOnePlayer room = new RoomOnePlayer(manager, "abc");
            delay(0.5, () -> manager.show(room, room.getTitle() , room.getId()));
        });

        back.setOnAction(e -> {
            Stack<WindowEntry> window = manager.getHistory();
            while(!window.peek().getWindow().getwindowId().equals("menuid")) {
                manager.back();
            }
        });
        VBox root = new VBox(15, createroom, back);
        root.setAlignment(Pos.CENTER);

        VBox single = new VBox(100, label, root);
        single.setAlignment(Pos.CENTER);

        this.getChildren().add(single);
        this.ResText();
    }
    
}
