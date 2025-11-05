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
		GIF5  = video("gif5.mp4"),
		GIF6  = video("gif6.mp4"),
		GIF7  = video("gif7.mp4"),
		GIF8  = video("gif8.mp4"),
		GIF9  = video("gif9.mp4"),
		GIF10 = video("gif10.mp4"),
		GIF11 = video("gif11.mp4"),
		GIF12 = video("gif12.mp4"),
		GIF13 = video("gif13.mp4"),
		GIF14 = video("gif14.mp4"),
		GIF15 = video("gif15.mp4"),
		GIF16 = video("gif16.mp4"),
		GIF17 = video("gif17.mp4"),
		GIF18 = video("gif18.mp4"),
		GIF19 = video("gif19.mp4"),
		GIF20 = video("gif20.mp4"),
		GIF21 = video("gif21.mp4"),
		GIF22 = video("gif22.mp4"),
		GIF23 = video("gif23.mp4"),
		GIF24 = video("gif24.mp4"),
		GIF25 = video("gif25.mp4"),
		GIF26 = video("gif26.mp4"),
		GIF27 = video("gif27.mp4"),
		GIF28 = video("gif28.mp4")
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
			GIF5, GIF6, GIF7, GIF8,
			GIF9, GIF10, GIF11, GIF12,
			GIF13, GIF14, GIF15, GIF16,
			GIF17, GIF18, GIF19, GIF20,
			GIF21, GIF22, GIF23, GIF24,
			GIF25, GIF26, GIF27, GIF28
		);

		int index = (int) (Math.random() * list.size());
		MediaPlayer media = list.get(index);
		media.setCycleCount(MediaPlayer.INDEFINITE);
		media.setAutoPlay(true);
		media.setMute(true);
		return media;
	}
}
