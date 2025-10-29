package btl.ballgame.client.ui.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ui.screen.Screen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class InformationalScreen extends Screen {
	
	private List<Node> screenButtons = new ArrayList<>();
	private String content;
	private String miniTitle;
		
	public InformationalScreen(String title, String content) {
		this(title, null, content);
	}
	
	public InformationalScreen(String title, String miniTitle, String content) {
		super(title); // base Screen constructor
		this.content = content;
		this.miniTitle = miniTitle;
	}
	
	public void setText(String content) {
		this.content = content;
		((Label) getElementById("informationalText")).setText(content);
	}
	
	public void addButton(String text, Runnable onClick) {
		Button button = this.createElement(
			UUID.randomUUID().toString(), new Button(text)
		);
		MenuUtils.styleButton(button, "#696969", "#575757");
		if (onClick != null) {
			button.setOnAction(e -> onClick.run());
		}
		button.setPrefWidth(400);
		button.setPrefHeight(30);
		screenButtons.add(button);
	}

	@Override
	public void onInit() {
		// background shit
		setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e1e, #2a2a2a);");

		// ARKANOID LOGO
		ImageView logo = this.createElement("logo", new ImageView(
			CSAssets.LOGO
		));
		logo.setPreserveRatio(true);
		logo.setFitWidth(500);
		
		// "INFORMATIONAL" CONTENT
		Label informational = this.createElement("informationalText", new Label(this.content));
		informational.setTextFill(Color.WHITE);
		informational.setFont(Font.font(null, FontWeight.BOLD, 18));
		
		// DEFINE THE LAYOUT
		VBox buttonsLayout = new VBox(10,
			this.screenButtons.toArray(Node[]::new)
		);
		buttonsLayout.setAlignment(Pos.CENTER);
		VBox infoLayout = new VBox(40);
		if (miniTitle != null) {
			// the small text above the informational text
			Label mini = this.createElement("miniText", new Label(this.miniTitle));
			mini.setTextFill(Color.LIGHTGRAY);
			mini.setFont(Font.font(15));
			infoLayout.getChildren().add(mini);
		}
		infoLayout.getChildren().add(informational);
		infoLayout.getChildren().add(buttonsLayout);
		infoLayout.setAlignment(Pos.CENTER);

		// general layout
		VBox layout = this.createElement("informationalScreenRoot", 
			new VBox(40, logo, infoLayout)
		);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(40));
		StackPane.setAlignment(layout, Pos.CENTER);
		
		this.addElement(layout);
	}
	
	@Override
	public void onRemove() {}
}
