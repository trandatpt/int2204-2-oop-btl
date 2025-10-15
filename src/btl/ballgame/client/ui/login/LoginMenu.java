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
    WindowManager manager;
    private Label label;
    private TextField usernameField;
    private PasswordField passwordField;
    private final Button create;
    private final Button login;
    private final Button forgot;
    private final Button clearall;
    private final Button exit;
    
    public LoginMenu(WindowManager manager) {
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
        this.exit = new Button("Exit");
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
                label.setText("Login Completed \u2714");

                Menu menu = new Menu(manager);
                delay(3, () -> manager.show(menu, "Menu Game"));
            }
            else {
                label.setText("Username or Password is incorrect \u2718");
            }
        });

        create.setOnAction(e -> {
            CreateAccount create = new CreateAccount(manager);
            manager.show(create, "Create Account");
        });

        forgot.setOnAction(e -> {
            ForgotPassWord fPassWord = new ForgotPassWord(manager);
            manager.show(fPassWord, "Forgot");
        });

        clearall.setOnAction(e -> {
            AccountManager.clearAccounts();
            AccountManager.saveAccounts();
            label.setText("Clear Accounts Successfull");
        });
        exit.setOnAction(e -> Exit.cancelWindow());



        VBox buttons = new VBox(10, usernameField, passwordField, login, create, forgot, clearall, exit);
        buttons.setAlignment(Pos.CENTER);

        VBox loginBox = new VBox(10, label, buttons);
        loginBox.setAlignment(Pos.CENTER);

        this.getChildren().add(loginBox);
    }

    private void delay(double seconds, Runnable action) {
        javafx.animation.PauseTransition d =
            new javafx.animation.PauseTransition(javafx.util.Duration.seconds(seconds));
        d.setOnFinished(e -> action.run());
        d.play();
    }
}
