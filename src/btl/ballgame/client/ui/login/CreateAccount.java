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
    private WindowManager manager;
    private final Label label;
    private final Button createbtn;
    private final Button backtologin;
    private TextField usernameField;
    private PasswordField passwordField;
    
    public CreateAccount(WindowManager manager) {
        this.manager = manager;
        this.label = new Label("Create Account");
        this.createbtn = new Button("Create");
        this.backtologin = new Button("Back To Login");

        this.usernameField = new TextField();
        usernameField.setPromptText("Username");

        this.passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        initUI();
    }
    @Override
    public void initUI() {
        createbtn.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();
            /*
             * code send to server
             */
            AccountManager.addAccount(user, pass);
            AccountManager.saveAccounts();
            Menu menu = new Menu(manager);
            manager.show(menu, "Menu");
        });

        backtologin.setOnAction(e -> {
            manager.back();
        });

        VBox buttons = new VBox(10, usernameField, passwordField, createbtn, backtologin);
        buttons.setAlignment(Pos.CENTER);

        VBox create = new VBox(100, label, buttons);
        create.setAlignment(Pos.CENTER);

        this.getChildren().add(create);
    }
    
}
