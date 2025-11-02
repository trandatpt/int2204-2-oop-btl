package btl.ballgame.client;

import javafx.scene.image.Image;

import java.io.File;

public class CSAssets {
	public static TextureAtlas ATLAS;
	
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

	// public static final AudioClip audio(String path) {
	// 	File file = new File("assets/" + path);
	// 	if (!file.exists()) {
	// 		throw new RuntimeException("Required asset: " + path + " is missing!");
	// 	}
	// 	return new AudioClip(file.toURI().toString());
	// }
}
