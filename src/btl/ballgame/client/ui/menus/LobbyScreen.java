package btl.ballgame.client.ui.menus;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.screen.Screen;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.*;

public class LobbyScreen extends Screen {
    private ArkanoidClientCore core;
    private VBox roomListContainer;
    private ScheduledExecutorService autoRefreshExec;
    private RoomInfo selectedRoom = null;
    private VBox roomDetailBox;

    public LobbyScreen() {
        super("Lobby Screen");
        if ((this.core = ArkanoidGame.core()) == null) {
			throw new IllegalStateException("What the fuck??");
		}
    }

    @Override
    public void onInit() {

        // Root layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1e1e1e;");

        // header
        Label header = new Label("Danh sách phòng");
        header.setTextFill(Color.WHITE);
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setMargin(header, new Insets(10));
        root.setTop(header);

        // center
        HBox mainContent = new HBox(10);
        mainContent.setPadding(new Insets(10));

        // room list
        roomListContainer = new VBox(8);
        roomListContainer.setPadding(new Insets(8));

        ScrollPane scroll = new ScrollPane(roomListContainer);
        scroll.setFitToWidth(true);

        // room detail
        roomDetailBox = new VBox(10);
        roomDetailBox.setPadding(new Insets(15));
        roomDetailBox.setStyle("-fx-background-color: #2b2b2b; -fx-border-color: white;");
        Label noSelectLbl = new Label("Chọn 1 phòng để xem chi tiết");
        noSelectLbl.setTextFill(Color.WHITE);
        roomDetailBox.getChildren().add(noSelectLbl);

        HBox centerBox = new HBox(10, scroll, roomDetailBox);
        HBox.setHgrow(scroll, Priority.ALWAYS);
        centerBox.setPrefHeight(450);
        root.setCenter(centerBox);

        // button
        Button joinByCodeBtn = new Button("Vào bằng mã phòng");
        Button createRoomBtn = new Button("Tạo phòng mới");
        Button leaderboardBtn = new Button("Leaderboard");
        Button exitBtn = new Button("Rời máy chủ");

        HBox footer = new HBox(10, joinByCodeBtn, createRoomBtn, leaderboardBtn, exitBtn);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));

        root.setBottom(footer);

        this.addElement("lobbyRoot", root);

        // actions
        createRoomBtn.setOnAction(e -> createRoomDialog());
        exitBtn.setOnAction(e -> {
            SoundManager.clickSoundConfirm();
            core.disconnect();
            MenuUtils.displayServerSelector();
        });

        // auto refresh (mock)
        autoRefreshExec = Executors.newSingleThreadScheduledExecutor();
        autoRefreshExec.scheduleAtFixedRate(this::mockLoadRooms, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onRemove() {
        if (autoRefreshExec != null) autoRefreshExec.shutdownNow();
    }

    // load room
    private void mockLoadRooms() {
        Platform.runLater(() -> {
            roomListContainer.getChildren().clear();

            List<RoomInfo> mock = List.of(
                    new RoomInfo(1, "Phòng chiến 1v1", "1/2", "Chờ người chơi"),
                    new RoomInfo(2, "Tổ đội 2v2", "2/4", "Đang tuyển"),
                    new RoomInfo(3, "UET tryhard", "4/4", "Đầy"),
                    new RoomInfo(4, "Không Lag", "1/4", "Chờ")
            );

            for (RoomInfo r : mock) {
                roomListContainer.getChildren().add(makeRoomCard(r));
            }
        });
    }

    // creat room card id
    private HBox makeRoomCard(RoomInfo room) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: gray;");
        card.setAlignment(Pos.CENTER_LEFT);

        Label nameLbl = new Label(room.name);
        nameLbl.setTextFill(Color.WHITE);

        Label playerLbl = new Label(room.players);
        playerLbl.setTextFill(Color.LIGHTGRAY);

        Label statusLbl = new Label(room.status);
        statusLbl.setTextFill(Color.LIGHTGRAY);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button joinBtn = new Button("Tham gia");
        joinBtn.setOnAction(e -> {
            MenuUtils.toast("Tham gia phòng: " + room.name);
        });

        // click to select room
        card.setOnMouseClicked(e -> {
            selectedRoom = room;
            updateRoomDetail(room);
        });

        card.getChildren().addAll(nameLbl, playerLbl, statusLbl, spacer, joinBtn);
        return card;
    }

    // show room detail
    private void updateRoomDetail(RoomInfo r) {
        roomDetailBox.getChildren().clear();

        Label title = new Label("Chi tiết phòng");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        title.setTextFill(Color.WHITE);

        Label n = new Label("Tên phòng: " + r.name);
        Label p = new Label("Người chơi: " + r.players);
        Label s = new Label("Trạng thái: " + r.status);

        n.setTextFill(Color.WHITE);
        p.setTextFill(Color.WHITE);
        s.setTextFill(Color.WHITE);

        roomDetailBox.getChildren().addAll(title, n, p, s);
    }

    private void createRoomDialog() {
        SoundManager.clickSoundConfirm();
        TextInputDialog dialog = new TextInputDialog("Tên phòng");
        dialog.setHeaderText("Tạo phòng mới");
        dialog.setContentText("Nhập tên phòng:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            MenuUtils.toast("Đã tạo phòng: " + name);
        });
    }

    // Simple room data
    private static class RoomInfo {
        int id;
        String name;
        String players;
        String status;

        RoomInfo(int id, String name, String players, String status) {
            this.id = id;
            this.name = name;
            this.players = players;
            this.status = status;
        }
    }
}
