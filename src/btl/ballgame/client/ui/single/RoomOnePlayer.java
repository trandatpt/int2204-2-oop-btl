package btl.ballgame.client.ui.single;

import btl.ballgame.client.ui.login.Account;
import btl.ballgame.client.ui.multi.ModeMulti;
import btl.ballgame.client.ui.multi.RoomTwoPlayer;
import btl.ballgame.client.ui.window.Window;
import btl.ballgame.client.ui.window.WindowEntry;
import btl.ballgame.client.ui.window.WindowManager;
import java.util.Stack;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RoomOnePlayer extends Window{
    private final Account account;
    private WindowManager manager;
    private final Label label;
    private Button player1;
    private Button player2;
    private final Button start;
    private final Button changetomulti;
    private final Button change_regime;
    private final Button exit_room;

    public RoomOnePlayer(WindowManager manager, Account account, String user2) {
        this.account = account;
        this.manager = manager;
        label = new Label("");
        player1 = new Button(account.getName());
        player2 = new Button(user2);
        start = new Button("Start Game");
        changetomulti = new Button("Change to Multi Room");
        change_regime = new Button("Change level");
        exit_room = new Button("Exit Room");
        manager.print();
        setWindowId("roomsingleid");
        setTitle("Room Single");
        initUI();
    }
    @Override
    public void initUI() {
        start.setOnAction(e -> {

        });

        changetomulti.setOnAction(e -> {
            RoomTwoPlayer multi = new RoomTwoPlayer(manager, account, "", "", "");
            delay(0.5, () -> manager.show(multi, multi.getTitle(), multi.getWindowId()));
        });

        change_regime.setOnAction(e -> {

        });

        exit_room.setOnAction(e -> {
            Stack<WindowEntry> window = manager.getHistory();
            while(!window.peek().getWindow().getWindowId().equals("menuonlineid")) {
                manager.back();
            }
        });

        VBox players = new VBox(30, player1, player2);
        players.setAlignment(Pos.CENTER);

        VBox feature = new VBox(10, start, changetomulti, change_regime, exit_room);
        feature.setAlignment(Pos.CENTER);

        VBox room = new VBox(20, label, players, feature);
        room.setAlignment(Pos.CENTER);

        this.getChildren().add(room);
    }
}
