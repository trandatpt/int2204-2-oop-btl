package btl.ballgame.client.ui.menus;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.screen.Screen;
import btl.ballgame.protocol.packets.in.PacketPlayInRoomSetReady;
import btl.ballgame.protocol.packets.in.PacketPlayInRoomSwapTeam;
import btl.ballgame.protocol.packets.out.PacketPlayOutRoomUpdate;
import btl.ballgame.protocol.packets.out.PacketPlayOutRoomUpdate.RoomPlayerEntry;
import btl.ballgame.protocol.packets.out.PacketPlayOutRoomUpdate.RoomTeamEntry;
import btl.ballgame.shared.libs.Constants.TeamColor;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoomScreenDynamic extends Screen {
	private ArkanoidClientCore core;
	private BorderPane root;
	private VBox[] teamBoxes = new VBox[2];
	private Label roomIdLabel;
	private Map<UUID, VBox> playerCards = new HashMap<>();

	public RoomScreenDynamic() {
		super("Room Lobby");
		if ((this.core = ArkanoidGame.core()) == null) {
			throw new IllegalStateException("Core is null!");
		}
	}

	@Override
	public void onInit() {
		root = new BorderPane();
		BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, false, true);
		root.setBackground(new Background(
			new BackgroundImage(CSAssets.LOBBY_BACKGROUND, 
			BackgroundRepeat.NO_REPEAT,
			BackgroundRepeat.NO_REPEAT, 
			BackgroundPosition.CENTER, bgSize
		)));

		// HEADER
		roomIdLabel = new Label("Room ID: Loading...");
		roomIdLabel.setTextFill(Color.WHITE);
		roomIdLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		Label headerLabel = new Label("Ready up to Begin the Match...");
		headerLabel.setTextFill(Color.WHITE);
		headerLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

		VBox headerBox = new VBox(6, headerLabel, roomIdLabel);
		headerBox.setAlignment(Pos.CENTER);
		headerBox.setPadding(new Insets(20));
		root.setTop(headerBox);
		
		// TEAMS
		HBox teamsContainer = new HBox(80);
		teamsContainer.setAlignment(Pos.CENTER);
		teamsContainer.setPadding(new Insets(20));

		teamBoxes[0] = createTeamBox("RED TEAM", "#b22222", "team_red");
		teamBoxes[1] = createTeamBox("BLUE TEAM", "#1e90ff", "team_blue");

		teamsContainer.getChildren().addAll(teamBoxes[0], teamBoxes[1]);
		root.setCenter(teamsContainer);

		// FOOTER
		Button leaveBtn = new Button("Leave Game");
		MenuUtils.styleButton(leaveBtn, "#b22222", "#8b1a1a");
		leaveBtn.setOnAction(e -> leaveRoom());

		HBox footer = new HBox(leaveBtn);
		footer.setAlignment(Pos.CENTER);
		footer.setPadding(new Insets(20));
		root.setBottom(footer);

		this.addElement("dynamicRoom", root);
	}
	
	private VBox createTeamBox(String teamName, String color, String logoKey) {
		Label teamLabel = new Label(teamName);
		teamLabel.setTextFill(Color.web(color));
		teamLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		
		Image logo = CSAssets.sprites.getAsImage("logo", logoKey);
		
		ImageView logoView = new ImageView(logo);
		logoView.setFitWidth(48);
		logoView.setFitHeight(48);
		logoView.setPreserveRatio(true);
		logoView.setEffect(new DropShadow(15, Color.color(0, 0, 0, 0.7)));

		HBox titleBox = new HBox(10, logoView, teamLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setPadding(new Insets(5));
		
		VBox teamBox = new VBox(15, titleBox);
		teamBox.setAlignment(Pos.CENTER);
		teamBox.setPadding(new Insets(20));
		teamBox.setPrefWidth(380);
		teamBox.setStyle(
			"-fx-background-color: rgba(25,25,25,0.8);" 
			+ "-fx-border-color: " + color + ";"
			+ "-fx-border-width: 3px;" + "-fx-background-radius: 15;" 
			+ "-fx-border-radius: 15;"
		);
		
		return teamBox;
	}

	private VBox createPlayerCard(RoomPlayerEntry player, boolean isCurrentUser, boolean isTeamRed) {
		VBox card = new VBox(8);
		card.setAlignment(Pos.CENTER);
		card.setPadding(new Insets(12));
		card.setPrefWidth(260);
		card.setStyle("-fx-background-color: rgba(50,50,50,0.85);" 
			+ "-fx-background-radius: 10;"
			+ "-fx-border-color: white;" 
			+ "-fx-border-width: 1.5;" 
			+ "-fx-border-radius: 10;"
		);

		Label nameLabel = new Label(player.name);
		nameLabel.setTextFill(Color.WHITE);
		nameLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");

		Label status = new Label(player.ready ? "READY" : "NOT READY");
		status.setTextFill(player.ready ? Color.LIGHTGREEN : Color.ORANGE);
		status.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

		Image banner = CSAssets.sprites.getAsImage("logo", isTeamRed ? "team_red" : "team_blue");
		ImageView bannerView = new ImageView(banner);
		bannerView.setFitWidth(100);
		bannerView.setFitHeight(100);
		bannerView.setPreserveRatio(true);
		bannerView.setOpacity(0.85);

		FadeTransition ft = new FadeTransition(Duration.millis(400), card);
		ft.setFromValue(0);
		ft.setToValue(1);
		ft.play();

		card.getChildren().addAll(bannerView, nameLabel, status);

		if (isCurrentUser) {
			Button readyBtn = new Button(player.ready ? "Unready" : "Ready Up");
			MenuUtils.styleButton(readyBtn, "#3b8a7c", "#2d695e");
			readyBtn.setOnAction(e -> toggleReady());

			Button switchBtn = new Button("Switch Team");
			MenuUtils.styleButton(switchBtn, "#4d476e", "#353147");
			switchBtn.setOnAction(e -> switchTeam());

			HBox btnBox = new HBox(10, readyBtn, switchBtn);
			btnBox.setAlignment(Pos.CENTER);
			card.getChildren().add(btnBox);
		}

		card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-border-color: #3b8a7c;"));
		card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("-fx-border-color: #3b8a7c;", "-fx-border-color: white;")));

		return card;
	}
	
	private TeamColor yourTeam;
	private boolean youReady;
	
	public void updateRoom(PacketPlayOutRoomUpdate packet) {
		roomIdLabel.setText("Room ID: " + packet.getRoomId());
		playerCards.clear();
		
		for (VBox box : teamBoxes) {
			box.getChildren().removeIf(n -> n != box.getChildren().get(0));
		}
		
		RoomTeamEntry[] teams = packet.getTeams();
		for (int i = 0; i < teams.length && i < 2; i++) {
			RoomTeamEntry team = teams[i];
			boolean isTeamRed = (i == 0);

			for (RoomPlayerEntry player : team.players) {
				// goofy ahh name
				boolean isMe = player.uuid.equals(core.getPlayer().getUniqueId());
				if (isMe) {
					yourTeam = TeamColor.of(team.teamColor);
					youReady = player.ready;
				}
				VBox playerCard = createPlayerCard(player, isMe, isTeamRed);
				playerCards.put(player.uuid, playerCard);
				teamBoxes[i].getChildren().add(playerCard);
			}
		}
	}
	
	private void toggleReady() {
		SoundManager.clickSoundConfirm();
		ArkanoidGame.core().getConnection().sendPacket(
			new PacketPlayInRoomSetReady(!youReady)
		);

	}

	private void switchTeam() {
	    SoundManager.clickSoundConfirm();
	    ArkanoidGame.core().getConnection().sendPacket(new PacketPlayInRoomSwapTeam(
	    	yourTeam == TeamColor.RED ? TeamColor.BLUE : TeamColor.RED
	    ));
	}


	private void leaveRoom() {
		SoundManager.clickBottonLogin();
		LobbyScreen screen = new LobbyScreen();
		ArkanoidGame.manager().setScreen(screen);
	}

	@Override
	public void onRemove() {}
}
