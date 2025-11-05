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
    private List<RoomInfo> mock;
    public LobbyScreen() {
        super("Lobby Screen");
        if ((this.core = ArkanoidGame.core()) == null) {
			throw new IllegalStateException("What the fuck??");
		}

        this.mock  = new ArrayList<>();
        mock.add(new RoomInfo("Room 1vs1", "0/2", "Waiting..."));
        mock.add(new RoomInfo("Room 2vs2", "0/4", "Waiting..."));
        mock.add(new RoomInfo("UET tryhard", "4/4", "Full"));
        mock.add(new RoomInfo("No Lag", "1/4", "Wait"));
    }

    @Override
    public void onInit() {
        SoundManager.playloop("MusicInGame");

        // Root layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1e1e1e;");

        // header
        Label header = new Label("List Room");
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
        Label noSelectLbl = new Label("Choose one room to view details");
        noSelectLbl.setTextFill(Color.WHITE);
        roomDetailBox.getChildren().add(noSelectLbl);

        HBox centerBox = new HBox(10, scroll, roomDetailBox);
        HBox.setHgrow(scroll, Priority.ALWAYS);
        centerBox.setPrefHeight(450);
        root.setCenter(centerBox);

        // button
        Button joinByCodeBtn = new Button("Join by Code");
        Button createRoomBtn = new Button("Create new room");
        Button leaderboardBtn = new Button("Leaderboard");
        Button exitBtn = new Button("Exit server");

        HBox footer = new HBox(10, joinByCodeBtn, createRoomBtn, leaderboardBtn, exitBtn);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));

        root.setBottom(footer);

        this.addElement("lobbyRoot", root);

        // actions
        joinByCodeBtn.setOnAction(e -> joinByCode());
        createRoomBtn.setOnAction(e -> createRoomDialog());
        leaderboardBtn.setOnAction(e -> leaderBoard());
        exitBtn.setOnAction(e -> exit());

        // auto refresh (mock)
        autoRefreshExec = Executors.newSingleThreadScheduledExecutor();
        autoRefreshExec.scheduleAtFixedRate(this::mockLoadRooms, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onRemove() {
        if (autoRefreshExec != null) autoRefreshExec.shutdownNow();
    }

    // load room
    private void mockLoadRooms() {
        Platform.runLater(() -> {
            roomListContainer.getChildren().clear();
        for (RoomInfo room : mock) {
                roomListContainer.getChildren().add(makeRoomCard(room));
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

        Button joinBtn = new Button("Join");
        joinBtn.setOnAction(e -> joinRoom(room));

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

        Label title = new Label("Room details");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        title.setTextFill(Color.WHITE);

        Label n = new Label("Room name: " + r.name);
        Label p = new Label("Players: " + r.players);
        Label s = new Label("Status: " + r.status);

        n.setTextFill(Color.WHITE);
        p.setTextFill(Color.WHITE);
        s.setTextFill(Color.WHITE);

        roomDetailBox.getChildren().addAll(title, n, p, s);
    }

    private void createRoomDialog() {
        SoundManager.clickSoundConfirm();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create new room");
        dialog.setHeaderText("Enter room information");

        // text
        TextField nameField = new TextField();
        nameField.setPromptText("Room Name");

        // create combo box choose type of room
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("1vs1", "2vs2");
        typeBox.setValue("1vs1");

        // new table
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Name: "), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Type: "), 0, 1);
        grid.add(typeBox, 1, 1);

        // add content
        dialog.getDialogPane().setContent(grid);

        //add button OK, Cancel
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        //waiting......
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String roomName = nameField.getText();
            String roomType = typeBox.getValue();

            // +++++++send to server

            roomType = convert(roomType);
            addRoom(
                new RoomInfo(roomName == "" ? "Room " + roomType : roomName,
                            roomType,
                            "Waiting..."
                )
            );
            MenuUtils.toast("Room has been created: " + roomName + " (" + roomType + ")");
        }
    }

    // Join Room by Code
    private void joinByCode() {
        // + code
    }

    // Leader Board
    private void leaderBoard() {
        // + code
    }

    // Back
    private void exit() {
        SoundManager.clickBottonLogin();
        core.disconnect();
        MenuUtils.displayServerSelector();
    }

    // Join Room
    private void joinRoom(RoomInfo room) {
        if (room.getNumberOfRoom() == room.getMaxNumberOfRoom()) {
            SoundManager.clickFalse();
            MenuUtils.toast("False to join: " + room.name + "/n" + "Status: " + room.status);
            // code sent to server
        } else {
            SoundManager.clickSoundConfirm();
            MenuUtils.toast("Join the room: " + room.name);

            // code sent to server

            // set number room
            room.setNumberOfRoom(room.getNumberOfRoom() + 1);
            // new Screen
            if (room.getMaxNumberOfRoom() == 2) {
                RoomScreenOneVsOne roomSreen = new RoomScreenOneVsOne();
                ArkanoidGame.manager().setScreen(roomSreen);
            } else {
                RoomScreenTwoVsTwo roomSreen = new RoomScreenTwoVsTwo();
                ArkanoidGame.manager().setScreen(roomSreen);
            }
        }
    }

    // add new room
    private void addRoom(RoomInfo room) {
        mock.add(room);
    }

    // change room information (change room's name)
    private void changeInforRoom(String name, int index) {
        mock.get(index).setName(name);
    }

    // Simple room data
    private static class RoomInfo {
        private static int sizeRoom = 0;
        int id;
        String name;
        String players;
        String status;

        RoomInfo(String name, String players, String status) {
            sizeRoom++;
            this.id = sizeRoom;
            this.name = name;
            this.players = players;
            this.status = status;
        }

        void setPlayers(String players) {
            this.players = players;
        }

        void setStatus(String status) {
            this.status = status;
        }

        // rename
        void setName(String name) {
            this.name = name;
        }

        // take number of room
        int getNumberOfRoom() {
            return Character.getNumericValue(players.charAt(0));
        }

        // take max number of room
        int getMaxNumberOfRoom() {
            return Character.getNumericValue(players.charAt(2));
        }

        // change number player
        void setNumberOfRoom(int number) {
            if (number == this.getMaxNumberOfRoom()) {
                this.setStatus("Full");
                System.out.println("Room " + this.name + " " + this.status);
            }
            players = players.substring(1);
            players = number + players;
            System.out.println("Number Of Room " + this.name + " is " + this.players);
        }
    }

    private String convert(String input) {
        int number1 = Character.getNumericValue(input.charAt(0));
        int number2 = Character.getNumericValue(input.charAt(3));
        number2 = number2 + number1;
        return "0/" + number2;
    }
}