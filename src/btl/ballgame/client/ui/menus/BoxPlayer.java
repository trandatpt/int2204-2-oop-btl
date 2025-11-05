package btl.ballgame.client.ui.menus;

import btl.ballgame.client.ui.audio.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class BoxPlayer extends VBox {

    private TextField nameField;
    private Label nameLabel;
    private Label warningLabel;

    public BoxPlayer(String placeholder, double width, double height) {
        super(10);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(15));
        setPrefSize(width, height);
        setStyle("-fx-background-color: #2b2b2b; -fx-border-color: white; -fx-border-width: 2px;");

        nameField = new TextField();
        nameField.setPromptText(placeholder + " name...");
        nameField.setMaxWidth(width * 0.6);

        nameLabel = new Label();
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("Arial", 16));
        nameLabel.setVisible(false);

        warningLabel = new Label();
        warningLabel.setTextFill(Color.RED);
        warningLabel.setVisible(false);

        getChildren().addAll(warningLabel, nameField, nameLabel);
    }

    public TextField getNameField() {
        return this.nameField;
    }

    public Label getLabelName() {
        return this.nameLabel;
    }

    public void setLabelName(Label label) {
        this.nameLabel = label;
    }

    public void showWarning(String msg) {
        warningLabel.setText(msg);
        warningLabel.setVisible(true);
        SoundManager.clickFalse();
    }

    public void hideWarning() {
        warningLabel.setVisible(false);
    }

    public void removeField() {
        this.getChildren().remove(nameField);
    }

    public boolean hasName() {
        return nameLabel != null;
    }

    public String getName() {
        return nameLabel.getText();
    }
}
