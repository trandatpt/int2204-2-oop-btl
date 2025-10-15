package btl.ballgame.client.ui.single;

import btl.ballgame.client.ui.multi.ModeMulti;
import btl.ballgame.client.ui.window.Window;
import btl.ballgame.client.ui.window.WindowEntry;
import btl.ballgame.client.ui.window.WindowManager;
import java.util.Stack;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RoomOnePlayer extends Window{
    private WindowManager manager;
    private final Label label;
    private Button player1;
    private Button player2;
    private final Button start;
    private final Button changetomulti;
    private final Button change_regime;
    private final Button exit_room;

    public RoomOnePlayer(WindowManager manager, String usename1) {
        this.manager = manager;
        label = new Label("");
        player1 = new Button(usename1);
        player2 = new Button("");
        start = new Button("Start Game");
        changetomulti = new Button("Change to Multi Mode");
        change_regime = new Button("Change level");
        exit_room = new Button("Exit Room");
        setId("roomsingleid");
        setTitle("Room");
        initUI();
    }
    @Override
    public void initUI() {
        start.setOnAction(e -> {

        });

        changetomulti.setOnAction(e -> {
            ModeMulti multi = new ModeMulti(manager);
            delay(0.5, () -> manager.show(multi, multi.getTitle(), multi.getId()));
        });

        change_regime.setOnAction(e -> {

        });

        exit_room.setOnAction(e -> {
            Stack<WindowEntry> window = manager.getHistory();
            while(!window.peek().getWindow().getwindowId().equals("menuid")) {
                manager.back();
            }
        });

        VBox buttons = new VBox(10, player1, player2, start, changetomulti, change_regime, exit_room);
        buttons.setAlignment(Pos.CENTER);

        VBox room = new VBox(20, label, buttons);
        room.setAlignment(Pos.CENTER);

        this.getChildren().add(room);
    }
}
