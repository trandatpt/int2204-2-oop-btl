package btl.ballgame.client.ui.multi;

import java.util.Stack;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import btl.ballgame.client.ui.login.Account;
import btl.ballgame.client.ui.window.*;


/**
 * 2P gameplay
 */
public class ModeMulti extends Window {
    private final Account account;
    private final WindowManager manager;
    private final Label label;
    private final Button createroom;
    private final Button back;

    public ModeMulti(WindowManager manager, Account account) {
        this.account = account;
        this.manager = manager;
        label = new Label("Multi");
        createroom = new Button("Create Multi Room");
        back = new Button("Back to Menu");
        manager.print();
        setwindowId("multiid");
        setTitle("Multi Player");
        initUI();
    }

    @Override
    public void initUI() {
        createroom.setOnAction(e -> {
            RoomTwoPlayer room = new RoomTwoPlayer(manager, account, "", "", "");
            manager.print();
            delay(0.50, () -> manager.show(room, room.getTitle(), room.getwindowId()));
        });

        back.setOnAction(e -> {
            Stack<WindowEntry> window = manager.getHistory();
            while(!window.peek().getWindow().getwindowId().equals("menuonlineid")) {
                manager.print();
                manager.back();
            }
        });

        VBox buttonMenu = new VBox(15, createroom, back);
        buttonMenu.setAlignment(Pos.CENTER);

        VBox root = new VBox(30, label, buttonMenu);
        root.setAlignment(Pos.CENTER);

        this.getChildren().add(root);
    }
}
