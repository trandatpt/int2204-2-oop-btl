package btl.ballgame.client;

import javafx.scene.image.Image;

import java.io.File;

import btl.ballgame.client.ui.audio.SoundManager;

public class CSAssets {
	public static TextureAtlas sprites;
	
	public static final Image
		LOGO = image("arkgo.png"),
		VS_BACKGROUND = image("vs-bkg.jpg")
	;
	
	public static void init() {
		try {
		SoundManager.onInit();
		sprites = new TextureAtlas("assets/sprites/sprites.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final Image image(String path) {
		File file = new File("assets/" + path);
		if (!file.exists()) {
			throw new RuntimeException("Required asset: " + path + " is missing!");
		}
		return new Image(file.toURI().toString());
	}
}
