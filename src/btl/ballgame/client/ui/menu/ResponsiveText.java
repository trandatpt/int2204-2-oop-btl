package btl.ballgame.client.ui.menu;

import java.util.List;
import javafx.scene.control.Button;
// chỉnh kích thước chữ theo màn hình
public class ResponsiveText {
    private List<Button> buttons;
    
    public ResponsiveText(List<Button> buttons) {
        this.buttons = buttons;
    }

    public void apply() {
        for(Button x : buttons) {
            x.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((o, oldVal, newVal) -> {
                    x.setStyle("-fx-font-size: " + (newVal.doubleValue() / 40) + "px;");
                    });
                }
            });
        }
    }
}
