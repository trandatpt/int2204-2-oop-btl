package btl.ballgame.client.ui.login;

import btl.ballgame.client.ui.window.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import btl.ballgame.client.ui.menu.*;

public class LoginMenu extends Window{
    private Account account;
    WindowManager manager;
    private Label label;
    private TextField usernameField;
    private PasswordField passwordField;
    private final Button create;
    private final Button login;
    private final Button forgot;
    private final Button clearall;
    private final Button score;
    private final Button link;
    private final Button server;
    private final Button exit;
    
    public LoginMenu(WindowManager manager) {
        account = null;
        this.label = new Label("");
        this.manager = manager;
        AccountManager.loadAccounts();

        this.usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        this.passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        this.create = new Button("Create Account");
        this.login = new Button("Login");
        this.forgot = new Button("Forget Password");
        this.clearall = new Button("Clear All Accounts");
        this.score = new Button("High Score");
        link         = new Button("Fanpage");
        this.server = new Button("Return to the server selection");
        this.exit = new Button("Exit");

        manager.print();
        setwindowId("loginid");
        setTitle("Login");
        initUI();
    }

    private void set_size_user_and_password(int size) {
        usernameField.setPrefWidth(size);
        passwordField.setPrefWidth(size);
        usernameField.setMaxWidth(size);
        passwordField.setMaxWidth(size);
    }
    
    @Override
    public void initUI() {
        set_size_user_and_password(150);

        /**
         * set login khi enter
         */
        getWindowScene().setOnKeyPressed(e -> {
           if (e.getCode().equals(KeyCode.ENTER)) {
                login.fire();
           }
        });

        login.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                label.setText("Please fill in both fields!");
                return;
            }
            boolean checklogin = false;

            for (Account x : AccountManager.getAllAccounts()) {
                if (x.getUsername().equals(user) && x.getPassword().equals(pass)) {
                    checklogin = true;
                    break;
                }
            }

            if (checklogin) {
                setAccount(user, pass);
                label.setText("Login Completed \u2714");
                delay(1, () -> label.setText("Welcome " + account.getName().toUpperCase() + " to the game"));
                animation(">", 0);
                animation("~", 0.5);
                MenuOnline menu = new MenuOnline(manager, account);
                delay(3, () -> {
                    label.setText("");
                    manager.show(menu, menu.getTitle() , menu.getwindowId());
                });
            }
            else {
                label.setText("Username or Password is incorrect \u2718");
            }
        });

        create.setOnAction(e -> {
            CreateAccount create = new CreateAccount(manager);
            manager.show(create, create.getTitle() , create.getwindowId());
        });

        forgot.setOnAction(e -> {
            ForgotPassWord fPassWord = new ForgotPassWord(manager);
            manager.show(fPassWord, fPassWord.getTitle(), fPassWord.getwindowId());
        });

        clearall.setOnAction(e -> {
            AccountManager.clearAccounts();
            AccountManager.saveAccounts();
            label.setText("Clear Accounts Successfull");
        });

        score.setOnAction(e -> {

        });

        link.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(
                        new java.net.URI("https://www.facebook.com/tran.viet.212574")
                    );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        server.setOnAction(e -> {
            manager.back();
        });

        exit.setOnAction(e -> Exit.cancelWindow());



        VBox buttons = new VBox(10, usernameField, passwordField, login, create, forgot, clearall,score, link, server, exit);
        buttons.setAlignment(Pos.CENTER);

        VBox loginBox = new VBox(10, label, buttons);
        loginBox.setAlignment(Pos.CENTER);

        this.getChildren().add(loginBox);
    }

    private void setAccount(String user, String pass) {
        String name = null;
        for (Account x : AccountManager.getAllAccounts()) {
                if (x.getUsername().equals(user) && x.getPassword().equals(pass)) {
                    name = x.getName();
                    break;
                }
            }
        account = new Account(user, pass, name);
    }

    private void animation(String n, double number) {
                delay(2.0 + number, () -> label.setText(n));
                delay(2.1 + number, () -> label.setText(n + n));
                delay(2.2 + number, () -> label.setText(n + n + n));
                delay(2.3 + number, () -> label.setText(n + n + n + n));
                delay(2.4 + number, () -> label.setText(n + n + n + n + n));
    }
}
