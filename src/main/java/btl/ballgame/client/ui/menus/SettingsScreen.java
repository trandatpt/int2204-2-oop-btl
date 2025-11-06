package btl.ballgame.client.ui.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.screen.Screen;

public class SettingsScreen extends Screen {

    private VBox contentBox;

    public SettingsScreen() {
        super("Settings");
    }

    @Override
    public void onInit() {
        // Root layout
        BorderPane root = new BorderPane();
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                CSAssets.LOBBY_BACKGROUND,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        root.setBackground(new Background(backgroundImage));

        ImageView logo = this.createElement("logo", new ImageView(CSAssets.LOGO));
        logo.setPreserveRatio(true);
        logo.setFitWidth(450);

        Label title = new Label("Game Settings");
        title.setTextFill(Color.WHITE);
        title.setFont(new Font("Arial", 28));

        Button volumeButton = this.createElement("volumeButton", new Button("Volume"));
        Button graphicsButton = this.createElement("graphicsButton", new Button("Graphics"));
        Button backButton = this.createElement("backButton", new Button("Back"));

        volumeButton.setPrefWidth(300);
        graphicsButton.setPrefWidth(300);
        backButton.setPrefWidth(300);

        MenuUtils.styleButton(volumeButton, "#4d476e", "#353147");
        MenuUtils.styleButton(graphicsButton, "#538e91", "#3a6466");
        MenuUtils.styleButton(backButton, "#b22222", "#8b1a1a");

        volumeButton.setOnAction(e -> showVolumeSettings());
        graphicsButton.setOnAction(e -> showGraphicsSettings());
        backButton.setOnAction(e -> back());

        VBox buttonBox = new VBox(20, title, volumeButton, graphicsButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        contentBox = new VBox();
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));
        contentBox.setSpacing(30);

        VBox layout = this.createElement("menuRoot", new VBox(40, logo, buttonBox, contentBox));
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        root.setCenter(layout);
        this.addElement("settingroot", root);
    }

    // Setting volume
    private void showVolumeSettings() {
        SoundManager.clickSoundConfirm();
        contentBox.getChildren().clear();

        Label volumeLabel = new Label("Master Volume");
        volumeLabel.setTextFill(Color.LIGHTGRAY);
        volumeLabel.setFont(new Font(18));

        Slider volumeSlider = new Slider(0, 100, SoundManager.getVolume() * 100);
        Label valueLabel = new Label((int) (SoundManager.getVolume()  * 100) + "%");
        valueLabel.setTextFill(Color.WHITE);

        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(25);
        volumeSlider.setPrefWidth(300);

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double v = newVal.doubleValue() / 100.0;
            valueLabel.setText((int)(v * 100) + "%");
            SoundManager.setVolume(v);
        });

        VBox volumeBox = new VBox(15, volumeLabel, volumeSlider, valueLabel);
        volumeBox.setAlignment(Pos.CENTER);

        contentBox.getChildren().add(volumeBox);
    }

    // Graphics Setting
    private void showGraphicsSettings() {
        SoundManager.clickSoundConfirm();
        contentBox.getChildren().clear();

        Label graphicsLabel = new Label("Graphics Settings");
        graphicsLabel.setTextFill(Color.LIGHTGRAY);
        graphicsLabel.setFont(new Font(18));

        Label aaLabel = new Label("Anti-Aliasing:");
        aaLabel.setTextFill(Color.WHITE);
        ComboBox<String> aaCombo = new ComboBox<>();
        aaCombo.getItems().addAll("TTRRM36", "Off");
        aaCombo.setValue("TTRRM36");

        // Quality
        Label qualityLabel = new Label("Quality:");
        qualityLabel.setTextFill(Color.WHITE);
        ComboBox<String> qualityCombo = new ComboBox<>();
        qualityCombo.getItems().addAll("Low", "Medium", "High");
        qualityCombo.setValue("High");

        // VSync
        CheckBox vsyncCheck = new CheckBox("Enable VSync");
        vsyncCheck.setTextFill(Color.WHITE);
        vsyncCheck.setSelected(true);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        grid.addRow(0, aaLabel, aaCombo);
        grid.addRow(1, qualityLabel, qualityCombo);
        grid.addRow(2, new Label(""), vsyncCheck);

        VBox graphicsBox = new VBox(20, graphicsLabel, grid);
        graphicsBox.setAlignment(Pos.CENTER);

        contentBox.getChildren().add(graphicsBox);
    }

    private void back() {
        SoundManager.clickSoundConfirm();
        MenuUtils.displayServerSelector();
    }

    @Override
    public void onRemove() {}
}
