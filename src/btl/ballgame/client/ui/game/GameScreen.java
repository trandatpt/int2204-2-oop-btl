package btl.ballgame.client.ui.game;

import btl.ballgame.client.ui.screen.Screen;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GameScreen extends Screen {

	public GameScreen(String title) {
		super(title);
	}

	@Override
	public void onInit() {
	    HBox root = new HBox();
	    root.setSpacing(20);

	    Canvas canvas = new Canvas(800, 640);
	    StackPane worldPane = new StackPane(canvas);
	    worldPane.setStyle("-fx-background-color: transparent;");
	    worldPane.setPadding(new Insets(20));
	    
	    GraphicsContext gc = canvas.getGraphicsContext2D();
	    gc.setFill(Color.BLACK);
	    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	    
	    // right side = info UI
	    VBox infoPane = new VBox(10);
	    infoPane.setPrefWidth(200);
	    infoPane.getChildren().add(new Label("amongus sus sus"));

	    // assemble
	    root.getChildren().addAll(worldPane, infoPane);
	    this.addElement("root", root);
	}

	@Override
	public void onRemove() {
		
	}
	
}