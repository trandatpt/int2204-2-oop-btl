package btl.ballgame.client.ui.menus;

import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.protocol.packets.in.PacketPlayInRequestAllPlayersList;
import btl.ballgame.protocol.packets.out.PacketPlayOutGetAllPlayers.PlayerDetails;

public class LeaderboardScreen extends Screen {

	private TableView<PlayerDetails> leaderboardTable;
	private Label loadingLabel;

	public LeaderboardScreen() {
		super("Server Leaderboard (3rd-party Server)");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onInit() {
		ArkanoidGame.maximizeWindow();
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
		
		ImageView logo = this.createElement("logo", new ImageView(CSAssets.LOGO));
		logo.setPreserveRatio(true);
		logo.setFitWidth(450);

		Label title = new Label("🏆 Leaderboard 🏆");
		title.setTextFill(Color.WHITE);
		title.setFont(Font.font("Arial", FontWeight.BOLD, 32));

		// loading label (shown before the server shits itself and reply)
		loadingLabel = this.createElement("loadingLabel", new Label("Downloading leaderboard, please wait..."));
		loadingLabel.setTextFill(Color.LIGHTGRAY);
		loadingLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

		// what even is this massive garbage what the fuck am i doing with my life
		// god please help me
		// fuck this world
		// what is this boilerplate fucking stupif shit
		leaderboardTable = this.createElement("leaderboardTable", new TableView<>());
		leaderboardTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		leaderboardTable.setPlaceholder(new Label("No leaderboard data available!"));

		TableColumn<PlayerDetails, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue().username()));
		
		// the fuck is this
		TableColumn<PlayerDetails, Integer> mpWinsCol = new TableColumn<>("Global Offensive Wins");
		mpWinsCol.setCellValueFactory(e -> new ReadOnlyIntegerWrapper(e.getValue().mpWins()).asObject());
		
		TableColumn<PlayerDetails, Integer> classicScoreCol = new TableColumn<>("Classic High Score");
		classicScoreCol.setCellValueFactory(e -> new ReadOnlyIntegerWrapper(e.getValue().classicHiScore()).asObject());

		leaderboardTable.getColumns().addAll(nameCol, mpWinsCol, classicScoreCol);

		// sort by multiplayer wins, descending
		mpWinsCol.setSortType(TableColumn.SortType.DESCENDING);
		leaderboardTable.getSortOrder().add(mpWinsCol);

		leaderboardTable.setMaxWidth(600);
		leaderboardTable.setMinHeight(400);
		leaderboardTable.setVisible(false); // hidden until data arrives

		// back to loby button
		Button backButton = this.createElement("backButton", new Button("Return to the Server Lobby"));
		backButton.setPrefWidth(350);
		MenuUtils.styleButton(backButton, "#b22222", "#8b1a1a");

		backButton.setOnAction(e -> {
			SoundManager.clickSoundConfirm();
			MenuUtils.displayLobbyScreen();
		});

		VBox mainBox = new VBox(20, logo, title, loadingLabel, leaderboardTable, backButton);
		mainBox.setAlignment(Pos.CENTER);
		mainBox.setPadding(new Insets(40));
		StackPane.setAlignment(mainBox, Pos.CENTER);

		root.setCenter(mainBox);
		this.addElement("root", root);
		
		// ask the server for mercy
		this.requestUpdate();
	}
	
	public void requestUpdate() {
		ArkanoidGame.core().getConnection().sendPacket(
			new PacketPlayInRequestAllPlayersList()
		);
	}
	
	public void updateLeaderboard(List<PlayerDetails> entries) {
		if (entries == null || entries.isEmpty()) {
			loadingLabel.setText("No leaderboard data found! Just... me?");
			return;
		}
		
		leaderboardTable.getItems().setAll(entries);
		leaderboardTable.setVisible(true);
		loadingLabel.setVisible(false);
		
		// i hate javafx
		leaderboardTable.getSortOrder().clear();
		leaderboardTable.getColumns().get(1).setSortType(TableColumn.SortType.DESCENDING);
		leaderboardTable.getSortOrder().add(leaderboardTable.getColumns().get(1));
		leaderboardTable.sort();
	}

	@Override
	public void onRemove() {}
}
