package btl.ballgame.client.ui.menus;

import btl.ballgame.client.ui.audio.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class BoxPlayer extends VBox {

    private TextField nameField;
    private Label nameLabel;
    private Label warningLabel;
    private VBox content;

    public BoxPlayer(String placeholder, double width, double height, MediaPlayer player) {
    super(0);
    setAlignment(Pos.TOP_CENTER);
    setPadding(new Insets(0));
    setMaxSize(width, height);
    setMinSize(width, height);
    setStyle("-fx-border-color: white; -fx-border-width: 2px;");


    MediaView backgroundView = new MediaView(player);
    backgroundView.setPreserveRatio(true);
    adjustVideoSize(backgroundView, width * 0.99, height * 0.99);
    
    content = new VBox(10);
    content.setAlignment(Pos.TOP_CENTER);

    nameField = new TextField();
    nameField.setPromptText(placeholder + " name...");
    nameField.setMaxWidth(width * 0.6);
    nameField.setStyle(
        "-fx-background-color: transparent;" +
        "-fx-border-color: white;" +
        "-fx-text-fill: white;" +
        "-fx-focus-color: transparent;" +
        "-fx-faint-focus-color: transparent;"
    );

    nameLabel = new Label();
    nameLabel.setTextFill(Color.WHITE);
    nameLabel.setFont(Font.font("Arial", 16));
    nameLabel.setVisible(false);

    warningLabel = new Label();
    warningLabel.setTextFill(Color.RED);
    warningLabel.setVisible(false);

    content.getChildren().addAll(warningLabel, nameField, nameLabel);

    StackPane stack = new StackPane(backgroundView, content);
    StackPane.setAlignment(content, Pos.TOP_CENTER);

    getChildren().add(stack);
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
        this.content.getChildren().remove(nameField);
    }

    public boolean hasName() {
        return nameLabel != null;
    }

    public String getName() {
        return nameLabel.getText();
    }

    private void adjustVideoSize(MediaView view, double boxWidth, double boxHeight) {
        Media media = view.getMediaPlayer().getMedia();
        if (media.getWidth() <= 0 || media.getHeight() <= 0) return;

        double mediaRatio = (double) media.getWidth() / media.getHeight();
        double boxRatio = boxWidth / boxHeight;

        if (boxRatio > mediaRatio) {
            view.setFitWidth(boxWidth);
            view.setFitHeight(boxWidth / mediaRatio);
        } else {
            view.setFitHeight(boxHeight);
            view.setFitWidth(boxHeight * mediaRatio);
        }
        Rectangle clip = new Rectangle(boxWidth, boxHeight);
        view.setClip(clip);
    }
}