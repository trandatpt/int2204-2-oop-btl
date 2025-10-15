package btl.ballgame.client.ui.menu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import btl.ballgame.client.ui.window.*;

public class Grapic extends Window {
    WindowManager manager;
    private Label label;
    private final Button low;
    private final Button medium;
    private final Button high;
    private final Button back;

    public Grapic(WindowManager manager) {
        this.manager = manager;
        this.label = new Label();
        this.low = new Button("Low");
        this.medium = new Button("Medium");
        this.high = new Button("High");
        this.back = new Button("Back to Settings");

        setwindowId("grapicid");
        initUI();
    }

    @Override
    public void initUI() {
        low.setOnAction(e -> grapicSetting("LOW"));
        medium.setOnAction(e -> grapicSetting("MEDIUM"));
        high.setOnAction(e -> grapicSetting("HIGH"));
        back.setOnAction(e -> manager.back());

        VBox buttons = new VBox(20, low, medium, high, back);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(50, label, buttons);
        root.setAlignment(Pos.CENTER);

        this.getChildren().add(root);
    }
    /*
     * gán label
     */
    private void showLabel(String text) {
        this.label.setText(text);
    }
    /*
     * set các grapic
     */
    private void grapicSetting(String grapic) {
        switch (grapic) {
            case "LOW":
                GraphicSetting.setGraphicsLevel(grapic);
                GraphicSetting.setBackgroundDetail(false);
                GraphicSetting.setParticleEffects(false);
                showLabel("Graphics set to: Low");
                break;

            case "MEDIUM":
                GraphicSetting.setGraphicsLevel(grapic);
                GraphicSetting.setBackgroundDetail(false);
                GraphicSetting.setParticleEffects(true);
                showLabel("Graphics set to: Medium");
                break;

            case "HIGH":
                GraphicSetting.setGraphicsLevel(grapic);
                GraphicSetting.setBackgroundDetail(true);
                GraphicSetting.setParticleEffects(true);
                showLabel("Graphics set to: High");
                break;

            default:
                throw new IllegalArgumentException("Unknown graphics level: " + grapic);
        }
    }
}