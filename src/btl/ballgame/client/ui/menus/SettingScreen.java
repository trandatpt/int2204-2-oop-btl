package btl.ballgame.client.ui.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.screen.Screen;

public class SettingScreen extends Screen {

    public SettingScreen() {
        super("Settings");
    }

    @Override
    public void onInit() {
        // Background
        setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e1e, #2a2a2a);");

        ImageView logo = this.createElement("logo", new ImageView(ArkanoidGame.LOGO));
        logo.setPreserveRatio(true);
        logo.setFitWidth(500);

        Button volumeButton = this.createElement("volumeButton", new Button("Volume"));
        Button graphicButton = this.createElement("graphicButton", new Button("Graphic"));
        Button backButton = this.createElement("backButton", new Button("Back"));

        volumeButton.setPrefWidth(300);
        graphicButton.setPrefWidth(300);
        backButton.setPrefWidth(300);

        MenuUtils.styleButton(volumeButton, "#4d476e", "#353147"); // purple
        MenuUtils.styleButton(graphicButton, "#538e91", "#3a6466"); // aqua
        MenuUtils.styleButton(backButton, "#b22222", "#8b1a1a"); // red

        volumeButton.setOnMouseEntered(e -> SoundManager.play("ClickTiny"));
        graphicButton.setOnMouseEntered(e -> SoundManager.play("ClickTiny"));
        backButton.setOnMouseEntered(e -> SoundManager.play("ClickTiny"));

        volumeButton.setOnAction(e -> setVolume());
        graphicButton.setOnAction(e -> setGraphic());
        backButton.setOnAction(e -> back());


        Label title = new Label("Game Settings");
        title.setTextFill(Color.WHITE);

        VBox buttonBox = new VBox(20, title, volumeButton, graphicButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = this.createElement("menuRoot", new VBox(50, logo, buttonBox));
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        StackPane.setAlignment(layout, Pos.CENTER);

        this.addElement(layout);
    }

    private void setVolume() {

    }

    private void setGraphic() {

    }

    private void back() {
        MenuUtils.displayServerSelector();
    }

    @Override
    public void onRemove() {
        // Optional cleanup
    }
}
