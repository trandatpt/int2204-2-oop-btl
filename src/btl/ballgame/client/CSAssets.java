package btl.ballgame.client;

import java.io.File;

import javafx.scene.image.Image;

public class CSAssets {
	public static final Image 
		LOGO = image("arkgo.png"),
		VS_BACKGROUND = image("vs-bkg.jpg")
	;
	
	
	public static final Image image(String path) {
		File file = new File("assets/" + path);
		if (!file.exists()) {
			throw new RuntimeException("Required asset: " + path + " is missing!");
		}
		return new Image(file.toURI().toString());
	}
}
