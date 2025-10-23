package btl.ballgame.client.ui.server;

import btl.ballgame.client.ui.login.LoginMenu;
import btl.ballgame.client.ui.menu.Exit;
import btl.ballgame.client.ui.window.Window;
import btl.ballgame.client.ui.window.WindowManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PickServer extends Window {

	private final WindowManager manager;
	private final Label titleLabel;
	private final Label statusLabel;
	private final ComboBox<Object> serverDropdown;
	private final TextField customServerBox;
	private final Button connectButton;
	private final Button offlineButton;
	private final Button exitButton;

	public PickServer(WindowManager manager, PredefinedServer... predefinedServers) {
		this.manager = manager;
		this.titleLabel = new Label("CHOOSE SERVER");
		this.statusLabel = new Label("");
		this.serverDropdown = new ComboBox<>();
		this.customServerBox = new TextField();
		this.connectButton = new Button("Play Online");
		this.offlineButton = new Button("Offline Mode");
		this.exitButton = new Button("Exit");

		setTitle("Server Selector");
		setWindowId("pickserverid");
		initUI(Arrays.asList(predefinedServers));
	}

	@Override
	public void initUI() {
	}

	public void initUI(List<PredefinedServer> predefinedServers) {
		// ====== STYLE ======
		this.setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e1e, #2a2a2a);");
		titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 26));
		titleLabel.setTextFill(Color.WHITE);
		statusLabel.setTextFill(Color.LIGHTGRAY);
		statusLabel.setFont(Font.font("Consolas", 14));

		// ====== SERVER DROPDOWN ======
		serverDropdown.getItems().addAll(predefinedServers);
		serverDropdown.getItems().add("Custom Server");
		serverDropdown.setPromptText("Select a server...");
		serverDropdown.setMaxWidth(400);
		serverDropdown.setMinHeight(30);

		customServerBox.setPromptText("Enter custom server address...");
		customServerBox.setMaxWidth(400);
		customServerBox.setMinHeight(30);
		customServerBox.setDisable(true);
		
		// Dropdown behavior
		serverDropdown.setOnAction(e -> {
			Object selected = serverDropdown.getValue();
			if (selected instanceof PredefinedServer server) {
				customServerBox.setText(server.getDomain());
				customServerBox.setDisable(true);
			} else {
				customServerBox.clear();
				customServerBox.setDisable(false);
			}
		});

		// ====== BUTTONS ======
		connectButton.setPrefWidth(300);
		offlineButton.setPrefWidth(300);
		exitButton.setPrefWidth(200);

		connectButton.setOnAction(e -> connectOnline());
		offlineButton.setOnAction(e -> goOffline());
		exitButton.setOnAction(e -> Exit.cancelWindow());

		styleButton(connectButton, "#3cb371", "#2e8b57"); // green
		styleButton(offlineButton, "#4682b4", "#36648b"); // blue
		styleButton(exitButton, "#b22222", "#8b1a1a"); // red

		// ====== LAYOUT ======
		VBox serverBox = new VBox(10, new Label("Online Server:"), serverDropdown, customServerBox, connectButton);
		((Label) serverBox.getChildren().get(0)).setTextFill(Color.WHITE);
		serverBox.setAlignment(Pos.CENTER);

		VBox bottomButtons = new VBox(10, offlineButton, exitButton);
		bottomButtons.setAlignment(Pos.CENTER);

		VBox layout = new VBox(30, titleLabel, serverBox, statusLabel, bottomButtons);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(40));

		// Force center positioning, not relative sizing
		StackPane.setAlignment(layout, Pos.CENTER);

		this.getChildren().clear();
		this.getChildren().add(layout);
	}

	private void connectOnline() {
		Object selected = serverDropdown.getValue();

		if (selected == null) {
			statusLabel.setText("! Please select or enter a server.");
			return;
		}

		String address;
		if (selected instanceof PredefinedServer server) {
			address = server.getDomain();
		} else {
			address = customServerBox.getText().trim();
			if (address.isEmpty()) {
				statusLabel.setText("❗ Please enter a custom server address.");
				return;
			}
		}

		statusLabel.setText("Connecting to " + address + "...");
		delay(1, () -> {
			LoginMenu login = new LoginMenu(manager);
			manager.show(login, login.getTitle(), login.getWindowId());
		});
	}

	private void goOffline() {
		statusLabel.setText("Launching offline mode...");
		delay(1, () -> {
			LoginMenu login = new LoginMenu(manager);
			manager.show(login, login.getTitle(), login.getWindowId());
		});
	}

	private void styleButton(Button btn, String baseColor, String hoverColor) {
		btn.setStyle("-fx-background-color: " + baseColor + "; -fx-text-fill: white; -fx-font-weight: bold;");
		btn.setOnMouseEntered(e -> {
			btn.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-font-weight: bold;");
		});
		btn.setOnMouseExited(e -> { 
			btn.setStyle("-fx-background-color: " + baseColor + "; -fx-text-fill: white; -fx-font-weight: bold;");
		});
	}

	public static class PredefinedServer {
		private final String displayName;
		private final String domain;

		public PredefinedServer(String displayName, String domain) {
			this.displayName = displayName;
			this.domain = domain;
		}

		public String getDisplayName() {
			return displayName;
		}

		public String getDomain() {
			return domain;
		}

		@Override
		public String toString() {
			return displayName;
		}
	}
}
