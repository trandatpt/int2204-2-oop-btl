package btl.ballgame.client;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.List;

import btl.ballgame.client.ui.audio.SoundManager;

public class CSAssets {
	public static TextureAtlas sprites;
	
	public static final Image
		LOGO = image("arkgo.png"),
		VS_BACKGROUND = image("vs-bkg.jpg"),
		CHARACTER1_IMAGE = image("Character1.png"),
		CHARACTER2_IMAGE = image("Character2.png"),
		CHARACTER3_IMAGE = image("Character3.png"),
		CHARACTER4_IMAGE = image("Character4.png"),
		CHARACTER5_IMAGE = image("Character5.png"),
		CHARACTER6_IMAGE = image("Character6.png")
		
	;
	
	public static final MediaPlayer
		GIF1  = video("gif1.mp4"),
		GIF2  = video("gif2.mp4"),
		GIF3  = video("gif3.mp4"),
		GIF4  = video("gif4.mp4"),
		GIF5  = video("gif5.mp4")
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
		File file = new File("assets/gifs/" + path);
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

	public static Image randomImage() {
		List<Image> list = List.of(
			CHARACTER1_IMAGE,
			CHARACTER2_IMAGE,
			CHARACTER3_IMAGE,
			CHARACTER4_IMAGE,
			CHARACTER5_IMAGE,
			CHARACTER6_IMAGE
		);
		int index = (int) (Math.random() * list.size());
		return list.get(index);
	}

	public static MediaPlayer randomGif() {
    List<MediaPlayer> list = List.of(
			GIF1, GIF2, GIF3, GIF4,
			GIF5
		);

		int index = (int) (Math.random() * list.size());
		MediaPlayer media = list.get(index);
		media.setCycleCount(MediaPlayer.INDEFINITE);
		media.setAutoPlay(true);
		media.setMute(true);
		return media;
	}
}
