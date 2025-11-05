package btl.ballgame.client;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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

	public static MediaPlayer video(String path) {
		File file = new File("assets/" + path);
		if (!file.exists()) {
			throw new RuntimeException("Required video: " + path + " is missing!");
		}
		Media media = new Media(file.toURI().toString());
		return new MediaPlayer(media);
	}

	public static Image gif(String path) {
		File file = new File("assets/" + path);
		if (!file.exists()) {
			throw new RuntimeException("Required gif: " + path + " is missing!");
		}
		return new Image(file.toURI().toString());
	}
}
