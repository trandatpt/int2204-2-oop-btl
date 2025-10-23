package btl.ballgame.client.ui.login;

import btl.ballgame.client.ui.window.*;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import btl.ballgame.client.ui.menu.*;


public class CreateAccount extends Window {
    private Account account;
    private TextField name;
    private WindowManager manager;
    private final Label label;
    private Label checknewaccount;
    private final Button createbtn;
    private final Button backtologin;
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField rPasswordField;
    
    public CreateAccount(WindowManager manager) {
        account = null;
        this.manager = manager;
        this.label = new Label("Create Account");
        this.checknewaccount = new Label("");
        this.createbtn = new Button("Create");
        this.backtologin = new Button("Back To Login");

        this.name = new TextField();
        name.setPromptText("Name");

        this.usernameField = new TextField();
        usernameField.setPromptText("Username");

        this.passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        this.rPasswordField = new PasswordField();
        rPasswordField.setPromptText("re-enter the password");

        setWindowId("createaccountid");
        setTitle("Create Account");
        initUI();
    }

    private void set_size_user_and_password(int size) {
        usernameField.setPrefWidth(size);
        passwordField.setPrefWidth(size);
        usernameField.setMaxWidth(size);
        passwordField.setMaxWidth(size);
        name.setPrefWidth(size);
        name.setMaxWidth(size);
        rPasswordField.setPrefWidth(size);
        rPasswordField.setMaxWidth(size);
    }

    @Override
    public void initUI() {
        set_size_user_and_password(150);
        createbtn.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();
            String name_ = name.getText();
            String remindPassword = rPasswordField.getText();
            /*
             * code send to server
             */
            int check_account = -1;

            if (!remindPassword.equals(pass)) {
                check_account = 0;
            }
            else {
                for (Account x : AccountManager.getAllAccounts()) {
                    if (x.getUsername().equals(user)) {
                        check_account = 1;
                        break;
                    }
                }
            }

            if (check_account == -1) {
                checknewaccount.setText("Account created successfully");
                AccountManager.addAccount(user, pass, name_);
                AccountManager.saveAccounts();
                account = new Account(user, pass, name_);
                MenuOnline menu = new MenuOnline(manager, account);
                delay(2, () -> manager.show(menu, menu.getTitle(), menu.getWindowId()));
            }
            else if (check_account == 1) {
                checknewaccount.setText("The account already exists | Account creation failed");
            }
            else {
                checknewaccount.setText("inconsistent password");
            }
        });

        backtologin.setOnAction(e -> {
            manager.back();
        });

        VBox buttons = new VBox(10, checknewaccount, name, usernameField, passwordField, rPasswordField, createbtn, backtologin);
        buttons.setAlignment(Pos.CENTER);

        VBox create = new VBox(50, label, buttons);
        create.setAlignment(Pos.CENTER);

        this.getChildren().add(create);
    }
}
