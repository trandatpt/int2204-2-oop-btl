package btl.ballgame.client;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class TextureAtlas {
    private final Image sheet;
    
    public final Sprite bullet;
    public final Sprite heart;
    public final Sprite ball;
    public final Sprite brickStages[];
    
    public TextureAtlas(String path) {
        sheet = new Image(path);
        if (sheet.isError()) {
        	System.out.println("errored! " + path);
            throw new RuntimeException("Failed to load spritesheet: " + path);
        }
        
        this.bullet = new Sprite(0, 0, 17, 100);
        this.heart = new Sprite(17, 0, 128, 116);
        this.ball = new Sprite(145, 0, 128, 128);
        this.brickStages = new Sprite[] {
        	new Sprite(1426, 0, 384, 128), // most cracked
        	new Sprite(1041, 0, 384, 128), // cracked
        	new Sprite(657, 0, 384, 128), // least cracked
        	new Sprite(273, 0, 384, 128), // not cracked
        };
    }

    public class Sprite {
        private final WritableImage image;
        public Sprite(int x, int y, int width, int height) {
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
