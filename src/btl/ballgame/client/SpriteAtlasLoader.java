package btl.ballgame.client;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Loads and manages sprites from a TextureAtlas (XML + PNG) file pair.
 * <p>
 * The XML should follow the TexturePacker format:
 * &lt;TextureAtlas imagePath="atlas.png"&gt;
 *     &lt;SubTexture name="brick.png" x="0" y="0" width="32" height="16"/&gt;
 * &lt;/TextureAtlas&gt;
 */
public class SpriteAtlasLoader {

    private Image spriteSheet;
    private final Map<String, Image> sprites = new HashMap<>();

    /** Load from resource inside JAR (e.g. /assets/atlas.xml). */
    public SpriteAtlasLoader(Class<?> anchorClass, String resourcePath) throws Exception {
        URL xmlUrl = anchorClass.getResource(resourcePath);
        if (xmlUrl == null)
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        try (InputStream in = xmlUrl.openStream()) {
            URI baseUri = xmlUrl.toURI().resolve(".");
            loadFromStream(in, baseUri);
        }
    }

    /** Load from external file system (e.g. assets/atlas.xml). */
    public SpriteAtlasLoader(String xmlFilePath) throws Exception {
        File xmlFile = new File(xmlFilePath);
        try (InputStream in = new FileInputStream(xmlFile)) {
            URI baseUri = xmlFile.getParentFile().toURI();
            loadFromStream(in, baseUri);
        }
    }

    private void loadFromStream(InputStream xmlStream, URI baseUri) throws Exception {
        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = dBuilder.parse(xmlStream);
        Element root = (Element) doc.getElementsByTagName("TextureAtlas").item(0);

        String imagePath = root.getAttribute("imagePath");
        URI imageUri = baseUri.resolve(imagePath);
        spriteSheet = new Image(imageUri.toString());

        NodeList list = root.getElementsByTagName("sprite");
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            String name = e.getAttribute("n");
            int x = Integer.parseInt(e.getAttribute("x"));
            int y = Integer.parseInt(e.getAttribute("y"));
            int width = Integer.parseInt(e.getAttribute("w"));
            int height = Integer.parseInt(e.getAttribute("h"));

            WritableImage sub = new WritableImage(spriteSheet.getPixelReader(), x, y, width, height);
            sprites.put(name, sub);
        }

        System.out.println("✅ Loaded " + sprites.size() + " sprites from " + imageUri);
    }

    /** Retrieve a sprite by name (as declared in XML). */
    public Image get(String name) {
        return sprites.get(name);
    }

    /** Get all sprite names in the atlas. */
    public Set<String> names() {
        return Collections.unmodifiableSet(sprites.keySet());
    }

    /** Return the full sprite sheet image. */
    public Image getSpriteSheet() {
        return spriteSheet;
    }

    /** Number of sprites loaded. */
    public int size() {
        return sprites.size();
    }
}
