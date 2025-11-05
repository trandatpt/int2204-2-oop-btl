package btl.ballgame.client.ui.menus;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.screen.Screen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class RoomScreenOneVsOne extends Screen {
    private ArkanoidClientCore core;
    private BoxPlayer player1Box;
    private BoxPlayer player2Box;

    public RoomScreenOneVsOne() {
        super("Room 1 vs 1");
        if ((this.core = ArkanoidGame.core()) == null) {
            throw new IllegalStateException("Core is null!");
        }
    }

    @Override
    public void onInit() {
        // Root layout
        BorderPane root = new BorderPane();
        // back ground
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                CSAssets.VS_BACKGROUND,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        root.setBackground(new Background(backgroundImage));

        // id + title header
        Label roomId = new Label("ID: ");
        roomId.setTextFill(Color.WHITE);
        roomId.setStyle("-fx-font-size: 12px; -fx-font-weight: normal;");

        Label labelHeader = new Label("1 vs 1 Battle Room");
        labelHeader.setTextFill(Color.WHITE);
        labelHeader.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        VBox headerBox = new VBox(5);
        headerBox.setPadding(new Insets(10));

        HBox idBox = new HBox();
        idBox.setAlignment(Pos.CENTER_LEFT);
        idBox.getChildren().add(roomId);

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(labelHeader);

        headerBox.getChildren().addAll(idBox, titleBox);

        BorderPane.setAlignment(headerBox, Pos.CENTER);
        BorderPane.setMargin(headerBox, new Insets(10));
        root.setTop(headerBox);

        // Center content
        HBox centerBox = new HBox(350);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        VBox team1Box = createTeamBox("Player 1", Color.RED, CSAssets.LOGO, player1Box);
        VBox team2Box = createTeamBox("Player 2", Color.BLUE, CSAssets.LOGO, player2Box);

        centerBox.getChildren().addAll(team1Box, team2Box);
        root.setCenter(centerBox);

        // Footer buttons
        Button startBtn = new Button("Start");
        Button switchModeBtn = new Button("Switch to 2vs2");
        Button exitBtn = new Button("Exit");

        MenuUtils.styleButton(startBtn, "#b22222", "#8b1a1a"); // aqua
		MenuUtils.styleButton(switchModeBtn, "#476e49ff", "#374731ff"); // purple
		MenuUtils.styleButton(exitBtn, "#4d476e", "#353147"); // purple

        // Actions
        startBtn.setOnAction(e -> startGame());
        switchModeBtn.setOnAction(e -> switchTo2vs2());
        exitBtn.setOnAction(e -> exitRoom());

        HBox footer = new HBox(20, startBtn, switchModeBtn, exitBtn);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));

        root.setBottom(footer);

        this.addElement("room1v1", root);
    }


    private VBox createTeamBox(String teamName, Color c, Image image, BoxPlayer playerBox) {
        VBox teamBox = new VBox(25);
        teamBox.setAlignment(Pos.CENTER);
        teamBox.setPadding(new Insets(15));
        teamBox.setPrefSize(350, 450);

        String color;
        if (c.equals(Color.RED)) {
            color = "red";
        } else {
            color = "blue";
        }

        teamBox.setStyle(
                "-fx-background-color: #2b2b2b;" +
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 2px;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
            );

        Label teamLabel = new Label(teamName);
        teamLabel.setTextFill(c);
        teamLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        BoxPlayer player = createPlayerBox(teamName, image);
        player1Box = player;
        teamBox.getChildren().add(teamLabel);
        teamBox.getChildren().add(player);
        return teamBox;
    }

    private BoxPlayer createPlayerBox(String placeholder, Image image) {
        BoxPlayer box = new BoxPlayer(placeholder, 280, 400, image);

        box.getNameField().setOnAction(e -> {
            String text = box.getNameField().getText().trim();

            String otherName = null;
            if (placeholder.equals("Team 1") && player2Box != null && player2Box.getLabelName() != null) {
                otherName = player2Box.getLabelName().getText();
            } else if (placeholder.equals("Team 2") && player1Box != null && player1Box.getLabelName() != null) {
                otherName = player1Box.getLabelName().getText();
            }

            // check duplicate
            if (text.isEmpty()) {
                SoundManager.clickFalse();
                box.showWarning("Name cannot be empty!");
                return;
            } else if (otherName != null && text.equalsIgnoreCase(otherName)) {
                SoundManager.clickFalse();
                box.showWarning("Duplicate name!");
                return;
            }

            SoundManager.clickSoundConfirm();
            box.hideWarning();
            box.getLabelName().setText(text);
            box.getLabelName().setVisible(true);
            box.removeField();
        });
        return box;
    }


    private void startGame() {
        if (!player1Box.hasName() || !player2Box.hasName()) {
            SoundManager.clickFalse();
            System.out.println("Please enter both player names!");
            return;
        } else {
            SoundManager.clickBottonLogin();
        }

    }

    private void switchTo2vs2() {
        //MenuUtils.displayTwoVsTwo();
        //temporarily
        RoomScreenTwoVsTwo two = new RoomScreenTwoVsTwo();
        ArkanoidGame.manager().setScreen(two);
    }

    private void exitRoom() {
        //MenuUtils.displayLobbyScreen();
        //temporarily
        LobbyScreen screen = new LobbyScreen();
        ArkanoidGame.manager().setScreen(screen);
    }

    @Override
    public void onRemove() {
    }
}