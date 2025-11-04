package btl.ballgame.client;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import btl.ballgame.shared.libs.external.Json;

public class TextureAtlas {
	private final Image sheet;
	private final Map<String, LinkedHashMap<String, Sprite>> sprites = new HashMap<>();

	public TextureAtlas(String jsonPath) {
		Json root;
		try {
			root = Json.read(Files.readString(new File(jsonPath).toPath()));
		} catch (Exception e) {
			throw new RuntimeException("Failed to read JSON! " + jsonPath, e);
		}

		// get metadata
		Json atlas = root.at("TextureAtlas");
		String imagePath = atlas.at("spritePath").asString();
		sheet = new Image(new File(imagePath).toURI().toString());
		
		if (sheet.isError()) {
			throw new RuntimeException("Failed to load! " + imagePath);
		}
		
		// build the spritesheet
		Json namespaces = atlas.at("sprites");
		for (String namespace : namespaces.asJsonMap().keySet()) {
			Json membersJson = namespaces.at(namespace);
			LinkedHashMap<String, Sprite> members = new LinkedHashMap<>();
			
			for (String spriteName : membersJson.asJsonMap().keySet()) {
				// copy the necessary stuff from the json and create a struct
				Json s = membersJson.at(spriteName);
				int x = s.at("x").asInteger();
				int y = s.at("y").asInteger();
				int w = s.at("w").asInteger();
				int h = s.at("h").asInteger();
				members.put(spriteName, new Sprite(x, y, w, h));
			}
			
			// insert the namespace into the global lookup table
			sprites.put(namespace, members);
		}

		System.out.println("[ATLAS] Loaded Texture Atlas: " + imagePath + " with " + sprites.size() + " namespaces!");
	}
	
	@Deprecated
	public Image __get(String fullPath) {
		var split = fullPath.replace(".png", "").split("/");
		return get(split[0], split[1]).getImage();
	}
	
	public Collection<Sprite> getAllFrom(String namespace) {
		Map<String, Sprite> ns = sprites.get(namespace);
		if (ns == null) {
			throw new IllegalArgumentException("Unknown namespace: " + namespace);
		}
		return ns.values();
	}
	
	public Image getAsImage(String namespace, String name) {
		return get(namespace, name).image;
	}
	
	public Sprite get(String namespace, String name) {
		Map<String, Sprite> ns = sprites.get(namespace);
		if (ns == null) {
			throw new IllegalArgumentException("Unknown namespace: " + namespace);
		}
		Sprite sprite = ns.get(name);
		if (sprite == null) {
			throw new IllegalArgumentException("Unknown sprite: " + name + " in namespace " + namespace);
		}
		return sprite;
	}
	
	// my magic functions
	public static Color fromRgbInt(int rgb) {
		return Color.rgb(
			(rgb >> 16) & 0xFF, 
			(rgb >> 8) & 0xFF, 
			rgb & 0xFF
		);
	}
	
	public static Color fromArgbInt(int argb) {
		return Color.rgb(
			(argb >> 16) & 0xFF, 
			(argb >> 8) & 0xFF, 
			argb & 0xFF,
			((argb >> 24) & 0xFF) / 255.0 // the first byte
		);
	}
	
	public static int fromFXColor(Color color) {
		return ((int) (color.getRed() * 255) << 16) 
			| ((int) (color.getGreen() * 255) << 8)
			| ((int) (color.getBlue() * 255))
		;
	}
	
	public class Sprite {
		private final WritableImage image;

		private Sprite(int x, int y, int width, int height) {
			if (x < 0 || y < 0 || width <= 0 || height <= 0) {
				throw new IllegalArgumentException("Invalid sprite bounds: " + x + "," + y + "," + width + "," + height);
			}
			if (x + width > sheet.getWidth() || y + height > sheet.getHeight()) {
				throw new IllegalArgumentException("Sprite bounds exceed sheet dimensions!");
			}
			image = new WritableImage(sheet.getPixelReader(), x, y, width, height);
		}
		
		public WritableImage getImage() {
			return image;
		}
	}
}
