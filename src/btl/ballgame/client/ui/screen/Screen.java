package btl.ballgame.client.ui.screen;

import javafx.scene.layout.StackPane;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Screen extends StackPane {
	public static final String GAME_TITLE = "Arkanoid: Global Offensive - ";
	
	private final Map<String, Node> idToElement = new HashMap<>();
	private final Map<Node, String> elementToId = new HashMap<>();
	
	private ScreenManager manager;
	private UUID screenUUID;
	private String screenTitle;
	
	public Screen(String title) {
		this.screenTitle = GAME_TITLE + title;
		this.screenUUID = UUID.randomUUID();
	}
	
	public void removeElement(Node node) {
		String id = elementToId.remove(node);
		if (id != null) {
			idToElement.remove(id);
			this.getChildren().remove(node);
		}
	}

	public void removeElement(String id) {
		Node node = idToElement.remove(id);
		if (node != null) {
			elementToId.remove(node);
			this.getChildren().remove(node);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Node> T createElement(String id, Node node) {
		idToElement.put(id, node);
		elementToId.put(node, id);
		return (T) node;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Node> T addElement(String id, Node node) {
		this.createElement(id, node);
		this.getChildren().add(node);
		return (T) node;
	}
	
	public void addElement(Node node) {
		if (!elementToId.containsKey(node)) {
			throw new IllegalArgumentException("Element not created!");
		}
		this.getChildren().add(node);
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getElementById(String id) {
		return (T) idToElement.get(id);
	}

	public String getIdOf(Node node) {
		return elementToId.get(node);
	}
	
	public String getScreenTitle() {
		return screenTitle;
	}
	
	public UUID getScreenUUID() {
		return screenUUID;
	}
	
	protected void setManager(ScreenManager manager) {
		this.manager = manager;
	}
	
	public final ScreenManager getManager() {
		return manager;
	}

	public abstract void onInit();
	public abstract void onRemove();
}
