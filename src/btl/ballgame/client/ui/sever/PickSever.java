package btl.ballgame.client.ui.sever;

import btl.ballgame.client.ui.login.LoginMenu;
import btl.ballgame.client.ui.menu.Exit;
import btl.ballgame.client.ui.window.Window;
import btl.ballgame.client.ui.window.WindowManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

public class PickSever extends Window{
    WindowManager manager;
    private Label label;
    private Button offline;
    private MenuButton onlineButton;
    private MenuItem sv0;
    private MenuItem sv1;
    private Button online;
    private Button exit;

    public PickSever(WindowManager manager, String sever0, String sever1) {
        this.manager = manager;
        label = new Label("");
        offline = new Button("Offline");
        sv0 = new MenuItem(sever0);
        sv1 = new MenuItem(sever1);
        onlineButton = new MenuButton("Select Sever", null, sv0, sv1);
        online = new Button("Online");
        exit = new Button("Exit");
        manager.print();
        setTitle("Chọn Sever");
        setwindowId("pickseverid");
        initUI();
    }

    @Override
    public void initUI() {
        offline.setOnAction(e -> {
            label.setText("Offline");
            LoginMenu login = new LoginMenu(manager);
            manager.show(login, login.getTitle(), login.getwindowId());
        });

        /**
         * nhấn vào thì show ra, không trỏ vào thì hide
         */
        onlineButton.setOnMouseEntered(e -> onlineButton.show());
        //onlineButton.setOnMouseExited(e -> onlineButton.hide());

        sv0.setOnAction(e -> {
            selectSever(onlineButton, sv0.getText());
        });

        sv1.setOnAction(e -> {
            selectSever(onlineButton, sv1.getText());
        });

        online.setOnAction(e -> {
            if (onlineButton.getText().equals("Select Sever")) {
                System.out.println("sever chưa được chọn");
                label.setText("The server has not been selected");
            }
            else {
                LoginMenu login = new LoginMenu(manager);
                System.out.println("sever đã được chọn là: " + onlineButton.getText());
                System.out.println("Connect to Sever...");

                label.setText("The server has been selected as " + onlineButton.getText());

                delay(1, () -> label.setText("Connecting..."));
                delay(2, () -> {
                    label.setText("");
                    onlineButton.hide();
                    onlineButton.setText("Select Sever");
                    manager.show(login, login.getTitle(), login.getwindowId());
                });
            }
        });

        exit.setOnAction(e -> {
            Exit.cancelWindow();
        });

        VBox pick = new VBox(20, offline, online, onlineButton, exit);
        pick.setAlignment(Pos.CENTER);

        VBox sever = new VBox(30, label, pick);
        sever.setAlignment(Pos.CENTER);

        this.getChildren().add(sever);
    }

    private void selectSever(MenuButton button, String severname) {
        button.setText(severname);
        button.hide();
        System.out.println("đã chọn sever: " + severname);
    }
    
}
