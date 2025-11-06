package btl.ballgame.client.ui.menus;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.protocol.packets.in.PacketPlayInCreateRoom;
import btl.ballgame.protocol.packets.in.PacketPlayInJoinRoom;
import btl.ballgame.protocol.packets.in.PacketPlayInRequestRoomList;
import btl.ballgame.protocol.packets.out.PacketPlayOutListPublicRooms;
import btl.ballgame.protocol.packets.out.PacketPlayOutListPublicRooms.RoomInfo;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.*;
import java.util.concurrent.*;

public class LobbyScreen extends Screen {
	private ArkanoidClientCore core;
	private VBox roomListContainer;
	private VBox roomDetailBox;
	private ScheduledExecutorService autoRefreshExec;
	private DisplayRoomInfo selectedRoom = null;

	private final List<DisplayRoomInfo> rooms = new ArrayList<>();

	public LobbyScreen() {
		super("Main Lobby (3rd-party Server)");
		if ((this.core = ArkanoidGame.core()) == null) {
			throw new IllegalStateException("Core is null!");
		}
	}

	@Override
	public void onInit() {
		// root
		BorderPane root = new BorderPane();
		BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
		BackgroundImage backgroundImage = new BackgroundImage(
			CSAssets.LOBBY_BACKGROUND, 
			BackgroundRepeat.NO_REPEAT,
			BackgroundRepeat.NO_REPEAT, 
			BackgroundPosition.CENTER, backgroundSize
		);
		root.setBackground(new Background(backgroundImage));

		// HEADER
		Label header = new Label("🎮 Arkanoid: GO - Main Lobby");
		header.setTextFill(Color.WHITE);
		header.setFont(Font.font("Segoe UI", 30));
		header.setAlignment(Pos.CENTER);
		header.setEffect(new DropShadow(10, Color.BLACK));
		BorderPane.setAlignment(header, Pos.CENTER);
		BorderPane.setMargin(header, new Insets(20, 0, 10, 0));
		root.setTop(header);

		// CENTER
		HBox centerBox = new HBox(20);
		centerBox.setPadding(new Insets(20, 40, 20, 40));

		// ROOM LIST
		roomListContainer = new VBox(10);
		roomListContainer.setPadding(new Insets(10));
		ScrollPane scrollPane = new ScrollPane(roomListContainer);
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
		scrollPane.setPrefWidth(450);

		// ROOM DETAILS PANEL
		roomDetailBox = new VBox(15);
		roomDetailBox.setPadding(new Insets(20));
		roomDetailBox.setAlignment(Pos.TOP_LEFT);
		roomDetailBox.setPrefWidth(350);
		roomDetailBox.setStyle(
			"-fx-background-color: rgba(30, 30, 30, 0.85); " +
			"-fx-background-radius: 10; "
			+ "-fx-border-radius: 10; " + 
			"-fx-border-color: rgba(255,255,255,0.2);"
		);
		Label detailPlaceholder = new Label("Select a room to view details");
		detailPlaceholder.setTextFill(Color.LIGHTGRAY);
		detailPlaceholder.setFont(Font.font("Segoe UI", 16));
		roomDetailBox.getChildren().add(detailPlaceholder);

		centerBox.getChildren().addAll(scrollPane, roomDetailBox);
		HBox.setHgrow(scrollPane, Priority.ALWAYS);
		root.setCenter(centerBox);

		// FUT BUTTONS
		Button joinByCodeBtn = new Button("🔑 Join by Code");
		Button createRoomBtn = new Button("➕ Create Room");
		Button leaderboardBtn = new Button("🏆 Leaderboard");
		Button exitBtn = new Button("🚪 Disconnect from Server");

		MenuUtils.styleButton(joinByCodeBtn, "#3b8a7c", "#2d695e");
		MenuUtils.styleButton(createRoomBtn, "#4d476e", "#353147");
		MenuUtils.styleButton(leaderboardBtn, "#365b8c", "#274167");
		MenuUtils.styleButton(exitBtn, "#b22222", "#8b1a1a");

		HBox footer = new HBox(20, joinByCodeBtn, createRoomBtn, leaderboardBtn, exitBtn);
		footer.setAlignment(Pos.CENTER);
		footer.setPadding(new Insets(20, 0, 20, 0));
		root.setBottom(footer);

		this.addElement("lobbyRoot", root);

		// DISPATCHER
		joinByCodeBtn.setOnAction(e -> joinByCode());
		createRoomBtn.setOnAction(e -> createRoomDialog());
		leaderboardBtn.setOnAction(e -> leaderBoard());
		exitBtn.setOnAction(e -> disconnect());

		// auto Refresh (request room list from server periodically)
		autoRefreshExec = Executors.newSingleThreadScheduledExecutor();
		autoRefreshExec.scheduleAtFixedRate(this::requestRoomList, 0, 2, TimeUnit.SECONDS);		
	}

	@Override
	public void onRemove() {
		if (autoRefreshExec != null) {
			autoRefreshExec.shutdownNow();
		}
	}

	/**
	 * Periodically request the list of rooms from the server.
	 */
	private void requestRoomList() {
		core.getConnection().sendPacket(new PacketPlayInRequestRoomList());
	}
	
	public void updateLobbyWith(PacketPlayOutListPublicRooms packet) {
		if (packet == null || packet.getRooms() == null) {
			System.err.println("[LobbyScreen] Found a broken packet!");
			Platform.runLater(() -> {
				roomListContainer.getChildren().setAll(new Label("Packet Error! No public rooms available."));
			});
			return;
		}
		List<DisplayRoomInfo> updatedRooms = new ArrayList<>();
		for (RoomInfo info : packet.getRooms()) {
			System.out.println(info);
			updatedRooms.add(DisplayRoomInfo.from(info));
		}
		synchronized (rooms) {
			rooms.clear();
			rooms.addAll(updatedRooms);
		}
		Platform.runLater(this::refreshRoomList);
	}

	private void refreshRoomList() {
		roomListContainer.getChildren().clear();
		if (rooms.isEmpty()) {
			roomListContainer.getChildren().add(new Label("No public rooms available."));
			return;
		}
		for (DisplayRoomInfo room : rooms) {
			VBox card = makeRoomCard(room);
			roomListContainer.getChildren().add(card);
		}
		// keep current selection if still exists
		if (selectedRoom != null && rooms.stream().noneMatch(r -> r.roomId.equals(selectedRoom.roomId))) {
			selectedRoom = null;
			roomDetailBox.getChildren().setAll(new Label("Select a room to view details"));
		}
	}

	private VBox makeRoomCard(DisplayRoomInfo room) {
		VBox card = new VBox(6);
		card.setPadding(new Insets(12));
		card.setStyle(
			"-fx-background-color: rgba(45,45,45,0.85);" + 
			"-fx-border-color: rgba(255,255,255,0.15);" + 
			"-fx-border-radius: 8;" + 
			"-fx-background-radius: 8;"
		);
		card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.5)));
		
		Label nameLabel = new Label(room.roomName);
		nameLabel.setTextFill(Color.WHITE);
		nameLabel.setFont(Font.font("Segoe UI", 18));

		Label infoLabel = new Label(room.gameMode + "  •  " + (room.playerCount + " / " + room.maxPlayers) + "  •  " + room.status);
		infoLabel.setTextFill(Color.LIGHTGRAY);
		infoLabel.setFont(Font.font("Segoe UI", 14));

		Button joinBtn = new Button("Join Match");
		MenuUtils.styleButton(joinBtn, "#3b8a7c", "#2d695e");
		joinBtn.setPrefWidth(100);
		joinBtn.setOnAction(e -> joinRoom(room.roomId));

		card.setOnMouseClicked(e -> {
			selectedRoom = room;
			updateRoomDetail(room);
		});

		HBox topRow = new HBox(nameLabel);
		HBox bottomRow = new HBox(infoLabel, new Region(), joinBtn);
		HBox.setHgrow(bottomRow.getChildren().get(1), Priority.ALWAYS);

		card.getChildren().addAll(topRow, bottomRow);
		card.setOnMouseEntered(e -> card.setStyle(
			"-fx-background-color: rgba(70,70,70,0.9); "
			+ "-fx-border-color: #3b8a7c; -fx-border-radius: 8; "
			+ "-fx-background-radius: 8;"
		));
		card.setOnMouseExited(e -> card.setStyle(
			"-fx-background-color: rgba(45,45,45,0.85); "
			+ "-fx-border-color: rgba(255,255,255,0.15); "
			+ "-fx-border-radius: 8; "
			+ "-fx-background-radius: 8;"
		));

		return card;
	}

	private void updateRoomDetail(DisplayRoomInfo r) {
		roomDetailBox.getChildren().clear();
		Label title = new Label("Arkanoid Room Details");
		title.setTextFill(Color.WHITE);
		title.setFont(Font.font("Segoe UI", 20));
		
		Label name = new Label("Room Name: " + r.roomName);
		Label players = new Label("Players: " + r.playerCount + " / " + r.maxPlayers);
		Label gamemode = new Label("Game Mode: " + r.gameMode);
		Label status = new Label("Match Status: " + r.status);

		for (Label l : new Label[] { name, players, gamemode, status }) {
			l.setTextFill(Color.LIGHTGRAY);
			l.setFont(Font.font("Segoe UI", 14));
		}

		Button joinBtn = new Button("Join this Room");
		MenuUtils.styleButton(joinBtn, "#3b8a7c", "#2d695e");
		joinBtn.setOnAction(e -> joinRoom(r.roomId));
		
		roomDetailBox.getChildren().addAll(title, name, players, gamemode, status, new Separator(), joinBtn);
	}

	private void createRoomDialog() {
		SoundManager.clickSoundConfirm();
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Arkanoid: Global Offensive - Create Room");
		dialog.setHeaderText("Arkanoid: GO - Enter Room Preferences");
		
		// the header
		TextField nameField = new TextField();
		nameField.setPromptText("Room Display Name");
		
		ComboBox<ArkanoidMode> modeBox = new ComboBox<>();
		for (ArkanoidMode mode : ArkanoidMode.values()) {
			if (mode.isSinglePlayer()) {
				continue; // skip classic arka mode
			}
			modeBox.getItems().add(mode);
		}
		modeBox.setValue(ArkanoidMode.ONE_VERSUS_ONE);
		
		// stupid hacks that used internal shit
		modeBox.setCellFactory(cb -> new ListCell<>() {
			@Override
			protected void updateItem(ArkanoidMode mode, boolean empty) {
				super.updateItem(mode, empty);
				setText(empty || mode == null ? null : mode.getFriendlyName());
			}
		});
		modeBox.setButtonCell(new ListCell<>() {
			@Override
			protected void updateItem(ArkanoidMode mode, boolean empty) {
				super.updateItem(mode, empty);
				setText(empty || mode == null ? null : mode.getFriendlyName());
			}
		});
		
		// 
		Spinner<Integer> firstToScoreSpinner = new Spinner<>(1, 10, 3);
		Spinner<Integer> timePerRoundSpinner = new Spinner<>(60, 600, 180, 10);
		Spinner<Integer> teamLivesSpinner = new Spinner<>(1, 5, 3);
		CheckBox privateBox = new CheckBox("I want to make this room private");
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		
		// what the fuck did i do???
		grid.setPadding(new Insets(20, 150, 10, 10));
		grid.add(new Label("Room Name:"), 0, 0);
		grid.add(nameField, 1, 0);
		grid.add(new Label("Game Mode:"), 0, 1);
		grid.add(modeBox, 1, 1);
		grid.add(new Label("First to Score:"), 0, 2);
		grid.add(firstToScoreSpinner, 1, 2);
		grid.add(new Label("Time per Round (sec):"), 0, 3);
		grid.add(timePerRoundSpinner, 1, 3);
		grid.add(new Label("Team Lives:"), 0, 4);
		grid.add(teamLivesSpinner, 1, 4);
		grid.add(privateBox, 1, 5);
		
		// create the confirm buttons
		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String name = nameField.getText().trim();
			if (name.isEmpty()) {
				MenuUtils.toast(AlertType.ERROR, "Please enter a room name!");
				return;
			}

			boolean isPrivate = privateBox.isSelected();
			int firstToScore = firstToScoreSpinner.getValue();
			int timePerRound = timePerRoundSpinner.getValue();
			int teamLives = teamLivesSpinner.getValue();
			
			PacketPlayInCreateRoom packet = new PacketPlayInCreateRoom(
				name, isPrivate, modeBox.getValue(),
				firstToScore, timePerRound, teamLives
			);
			core.getConnection().sendPacket(packet);
			//MenuUtils.showLoadingScreen("Creating Arkanoid Room...");
		}
	}

	private void joinByCode() {
		SoundManager.clickSoundConfirm();
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Arkanoid: Global Offensive - Join Room by Code");
		dialog.setHeaderText("Enter the Room Code provided by your friends/foes:");
		
		TextField codeField = new TextField();
		codeField.setPromptText("Room Code");
		codeField.setPrefWidth(200);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		grid.add(new Label("Room Code:"), 0, 0);
		grid.add(codeField, 1, 0);
		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String code = codeField.getText().trim();
			if (!code.matches("^[A-Z1-9]{8}$")) {
				MenuUtils.toast("Please enter a valid 8-character room code!");
				return;
			}
			this.joinRoom(code);
		}
	}
	
	private void leaderBoard() {
		// open leaderboard screen
	}

	private void disconnect() {
		SoundManager.clickButtonLogin();
		core.disconnect();
		MenuUtils.displayServerSelector();
	}
	
	private void joinRoom(String id) {
		core.getConnection().sendPacket(new PacketPlayInJoinRoom(id));
		MenuUtils.showLoadingScreen("Joining Room...");
	}

	// === Standardized Data Model ===
	private static class DisplayRoomInfo {
		final String roomId;
		final String roomName;
		final String gameMode;
		final int playerCount;
		final int maxPlayers;
		final String status;

		DisplayRoomInfo(String roomId, String roomName, String gameMode, int playerCount, int maxPlayers) {
			this.roomId = roomId;
			this.roomName = roomName;
			this.gameMode = gameMode;
			this.playerCount = playerCount;
			this.maxPlayers = maxPlayers;
			this.status = (playerCount >= maxPlayers) ? "Full!" : "Waiting...";
		}

		static DisplayRoomInfo from(PacketPlayOutListPublicRooms.RoomInfo info) {
			return new DisplayRoomInfo(
				info.roomId, info.roomName,
				info.gameMode != null ? info.gameMode.getFriendlyName() : "Unknown", 
				info.playerCount,
				info.maxPlayer
			);
		}

		@Override
		public String toString() {
			return String.format("[%s] %s (%d/%d) - %s", gameMode, roomName, playerCount, maxPlayers, status);
		}
	}
}