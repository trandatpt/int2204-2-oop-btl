package btl.ballgame.client.ui.login;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import btl.ballgame.client.ui.window.*;

public class ForgotPassWord extends Window{
    private WindowManager manager;
    private Label label;
    private final Button forgotbtn;
    private final Button backtologin;
    private TextField userField;
    
    public ForgotPassWord(WindowManager manager) {
        this.manager = manager;
        this.label = new Label("");
        this.forgotbtn = new Button("Forgot");
        this.backtologin = new Button("Back To Login");
        this.userField = new TextField();
        this.userField.setPromptText("username");
        setwindowId("forgotpasswordid");
        initUI();
    }

    @Override
    public void initUI() {
        forgotbtn.setOnAction(e -> {
            String user = userField.getText();
            label.setText("Pass: " + getPassForgot(user));
        });

        backtologin.setOnAction(e -> manager.back());

        VBox buttons = new VBox(10, userField, forgotbtn, backtologin);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(50, label, buttons);
        root.setAlignment(Pos.CENTER);

        this.getChildren().add(root);
    }
    
    private String getPassForgot(String user) {
        List<Account> accounts = AccountManager.getAllAccounts();
        int size_account = accounts.size();

        for (int i = 0; i < size_account; i++) {
            if (accounts.get(i).getUsername().equals(user)) {
                return accounts.get(i).getPassword();
            }
        }
        return "Tài khoản không tồn tại !!!";
    }
}
