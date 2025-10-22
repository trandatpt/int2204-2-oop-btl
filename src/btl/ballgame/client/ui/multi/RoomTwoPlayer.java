package btl.ballgame.client.ui.multi;

import btl.ballgame.client.ui.login.Account;
import btl.ballgame.client.ui.single.RoomOnePlayer;
import btl.ballgame.client.ui.window.Window;
import btl.ballgame.client.ui.window.WindowEntry;
import btl.ballgame.client.ui.window.WindowManager;
import java.util.Stack;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RoomTwoPlayer extends Window{
    private final Account account;
    private WindowManager manager;
    private final Label label;
    private Button player1;
    private Button player2;
    private Button player3;
    private Button player4;
    private final Button start;
    private final Button changetosingle;
    private final Button change_regime;
    private final Button exit_room;

    public RoomTwoPlayer(WindowManager manager, Account account, String user2, String user3, String user4) {
        this.account = account;
        this.manager = manager;
        label = new Label("");
        player1 = new Button(account.getName());
        player2 = new Button(user2);
        player3 = new Button(user3);
        player4 = new Button(user4);
        start = new Button("Start Game");
        changetosingle = new Button("Change to Single Room");
        change_regime = new Button("Change level");
        exit_room = new Button("Exit Room");
        manager.print();
        setwindowId("roommultiid");
        setTitle("Room Multi");
        initUI();
    }

    @Override
    public void initUI() {
        start.setOnAction(e -> {

        });

        changetosingle.setOnAction(e -> {
            RoomOnePlayer single = new RoomOnePlayer(manager, account, "");
            delay(0.5, () -> manager.show(single, single.getTitle(), single.getwindowId()));
        });

        change_regime.setOnAction(e -> {

        });

        exit_room.setOnAction(e -> {
            Stack<WindowEntry> window = manager.getHistory();
            while(!window.peek().getWindow().getwindowId().equals("menuonlineid")) {
                manager.back();
            }
        });

        VBox players_redteam = new VBox(10, player1, player2);
        players_redteam.setAlignment(Pos.CENTER);
        
        VBox players_blueteam = new VBox(10, player3, player4);
        players_blueteam.setAlignment(Pos.CENTER);

        VBox players = new VBox(30, players_redteam, players_blueteam);
        players.setAlignment(Pos.CENTER);

        VBox feature = new VBox(10, start, changetosingle, change_regime, exit_room);
        feature.setAlignment(Pos.CENTER);

        VBox room = new VBox(20, label, players, feature);
        room.setAlignment(Pos.CENTER);

        this.getChildren().add(room);
    }
}
