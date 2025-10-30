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
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.screen.Screen;

public class SettingsScreen extends Screen {

    public SettingsScreen() {
        super("Settings");
    }

    @Override
    public void onInit() {
        // Background
        setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e1e, #2a2a2a);");

        ImageView logo = this.createElement("logo", new ImageView(CSAssets.LOGO));
        logo.setPreserveRatio(true);
        logo.setFitWidth(500);

        Button volumeButton = this.createElement("volumeButton", new Button("Volume"));
        Button graphicsButton = this.createElement("graphicsButton", new Button("Graphics"));
        Button backButton = this.createElement("backButton", new Button("Back"));
        
        volumeButton.setPrefWidth(300);
        graphicsButton.setPrefWidth(300);
        backButton.setPrefWidth(300);

        MenuUtils.styleButton(volumeButton, "#4d476e", "#353147"); // purple
        MenuUtils.styleButton(graphicsButton, "#538e91", "#3a6466"); // aqua
        MenuUtils.styleButton(backButton, "#b22222", "#8b1a1a"); // red
        
        volumeButton.setOnAction(e -> setVolume());
        graphicsButton.setOnAction(e -> setGraphics());
        backButton.setOnAction(e -> back());

        Label title = new Label("Game Settings");
        title.setTextFill(Color.WHITE);

        VBox buttonBox = new VBox(20, title, volumeButton, graphicsButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = this.createElement("menuRoot", new VBox(50, logo, buttonBox));
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        StackPane.setAlignment(layout, Pos.CENTER);

        this.addElement(layout);
    }

    private void setVolume() {

    }

    private void setGraphics() {

    }

    private void back() {
        MenuUtils.displayServerSelector();
    }

    @Override
    public void onRemove() {}
}
