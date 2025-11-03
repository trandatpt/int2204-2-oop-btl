package btl.ballgame.client.ui.menus;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ui.screen.Screen;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.*;

public class LobbyScreen extends Screen {

    private final TableView<RoomInfo> table = new TableView<>();
    private final ObservableList<RoomInfo> rooms = FXCollections.observableArrayList();

    // auto refresh
    private ScheduledExecutorService autoRefreshExec;

    public LobbyScreen() {
        super("Lobby Browser");
    }

    @Override
    public void onInit() {

        setStyle("-fx-background-color: linear-gradient(to bottom, #1e1e1e, #2a2a2a);");

        Label title = new Label("Available Rooms");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // table
        table.setItems(rooms);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<RoomInfo, String> nameCol = new TableColumn<>("Room Name");
        nameCol.setCellValueFactory(c -> c.getValue().nameProperty());

        TableColumn<RoomInfo, String> playersCol = new TableColumn<>("Players");
        playersCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPlayers() + "/" + c.getValue().getMaxPlayers())
        );

        TableColumn<RoomInfo, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> c.getValue().statusProperty());

        table.getColumns().addAll(nameCol, playersCol, statusCol);
        table.setPrefHeight(400);

        // button
        Button joinBtn = new Button("Join Room");
        Button createBtn = new Button("Create Room");
        Button refreshBtn = new Button("Refresh");
        Button backBtn = new Button("Back");

        MenuUtils.styleButton(joinBtn, "#699456", "#4c6940");
        MenuUtils.styleButton(createBtn, "#4682b4", "#36648b");
        MenuUtils.styleButton(refreshBtn, "#636363", "#454545");
        MenuUtils.styleButton(backBtn, "#b22222", "#8b1a1a");

        HBox buttons = new HBox(10, joinBtn, createBtn, refreshBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, title, table, buttons);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        this.addElement(layout);

        // action
        joinBtn.setOnAction(e -> joinSelectedRoom());
        createBtn.setOnAction(e -> createRoomDialog());
        refreshBtn.setOnAction(e -> requestRoomList());
        backBtn.setOnAction(e -> MenuUtils.displayServerSelector());

        // auto refresh every 7s
        autoRefreshExec = Executors.newSingleThreadScheduledExecutor();
        autoRefreshExec.scheduleAtFixedRate(this::requestRoomList, 0, 7, TimeUnit.SECONDS);
    }

    @Override
    public void onRemove() {
        if (autoRefreshExec != null) autoRefreshExec.shutdownNow();
    }

    private void requestRoomList() {
        // TODO: send packet C2S_RequestRoomList to server
        Platform.runLater(() -> {
            rooms.setAll(
                    new RoomInfo(1, "Room 1", 1, 2, "Waiting"),
                    new RoomInfo(2, "Room 2", 2, 2, "Full"),
                    new RoomInfo(3, "Vietnam Players", 1, 4, "Waiting")
            );
        });
    }

    private void joinSelectedRoom() {
        RoomInfo selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MenuUtils.toast("Please select a room.");
            return;
        }
        // TODO: send packet C2S_JoinRoom(selected.roomId)
        MenuUtils.toast("Joining " + selected.getName());
    }

    private void createRoomDialog() {
        TextInputDialog dialog = new TextInputDialog("My Room");
        dialog.setHeaderText("Create a new Room");
        dialog.setContentText("Enter room name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            // TODO: send packet C2S_CreateRoom(name)
            MenuUtils.toast("Room created: " + name);
        });
    }

    // data table
    public static class RoomInfo {
        private final int roomId;
        private final StringProperty name = new SimpleStringProperty();
        private final IntegerProperty players = new SimpleIntegerProperty();
        private final IntegerProperty maxPlayers = new SimpleIntegerProperty();
        private final StringProperty status = new SimpleStringProperty();

        public RoomInfo(int roomId, String name, int players, int maxPlayers, String status) {
            this.roomId = roomId;
            this.name.set(name);
            this.players.set(players);
            this.maxPlayers.set(maxPlayers);
            this.status.set(status);
        }

        public int getRoomId() { return roomId; }
        public StringProperty nameProperty() { return name; }
        public String getName() { return name.get(); }

        public int getPlayers() { return players.get(); }
        public int getMaxPlayers() { return maxPlayers.get(); }
        public StringProperty statusProperty() { return status; }
    }
}
