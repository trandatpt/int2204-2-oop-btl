package btl.ballgame.client.ui.menu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import btl.ballgame.client.ui.window.*;


public class Setting extends Window {
    private final WindowManager manager;
    private final Label label;
    private final Button Sound;
    private final Button Skin_paddle;
    private final Button Grapic;
    private final Button Language;
    private final Button Exit;

    public Setting(WindowManager manager) {
        this.manager = manager;
        label = new Label("SETTINGS");
        Sound = new Button("Volume");
        Skin_paddle = new Button("Skin Paddle");
        Grapic = new Button("Grapics");
        Language = new Button("Language");
        Exit = new Button("Back to Menu");
        
        setWindowId("settingid");
        setTitle("Settings");
        initUI();
    }
    @Override
    public void initUI() {
        Sound.setOnAction(e -> {

        });

        Skin_paddle.setOnAction(e -> {

        });

        Grapic.setOnAction(e -> {
            Grapic grapic = new Grapic(manager);
            manager.show(grapic, grapic.getTitle(), grapic.getWindowId());
        });

        Language.setOnAction(e -> {

        });

        Exit.setOnAction(e -> {
            manager.back();
        });

        VBox root = new VBox(15, Sound, Skin_paddle, Grapic, Language, Exit);
        root.setAlignment(Pos.CENTER);

        VBox setting = new VBox(50, label, root);
        setting.setAlignment(Pos.CENTER);

        this.getChildren().add(setting);
    }
    
}
