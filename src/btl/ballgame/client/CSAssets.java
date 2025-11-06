package btl.ballgame.client;

import btl.ballgame.client.ui.audio.SoundManager;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class CSAssets {
	public static TextureAtlas sprites;
	
	public static final Image
		LOGO = image("arkgo.png"),
		VS_BACKGROUND = image("vs-bkg.jpg"),
        VS_BACKGROUND2 = image("background.gif"),
        VS_BACKGROUND3 = image("background2.gif"),
        BORDER_BG = image("BorderBackground.png"),
        BORDER_RED = image("redBorder.png"),
        BORDER_BLUE = image("blueBorder.png"),
        BORDER_GAME = image("GameBorder.png"),
        OVERSCREEN = image("overScreen.gif"),
		LOBBY_BACKGROUND = image("lobby.png")
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
		System.out.println("Load image: " + "assets/" + path);
		return new Image(file.toURI().toString());
	}

	public static MediaPlayer video(String path) {
		File file = new File("assets/gifs/" + path);
		if (!file.exists()) {
			throw new RuntimeException("Required video: " + path + " is missing!");
		}
		System.out.println("Load video/gif: " + "assets/" + path);
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