package btl.ballgame.client;

import javafx.scene.image.Image;

import java.io.File;

public class CSAssets {
	public static final Image 
		LOGO = image("arkgo.png"),
		VS_BACKGROUND = image("vs-bkg.jpg")
	;

    public static final SpriteAtlasLoader sprites = loadSprite(
            "SpriteSheet/spritesheet.xml"
    );

    public static final SpriteAtlasLoader loadSprite(String path) {
        File file = new File("assets/" + path);
        if (!file.exists()) {
            throw new RuntimeException("Required sprite atlas: " + path + " is missing!");
        }
        try {
            return new SpriteAtlasLoader(file.getPath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sprite atlas: " + path, e);
        }
    }

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
