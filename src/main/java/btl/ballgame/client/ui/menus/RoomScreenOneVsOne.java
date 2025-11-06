package btl.ballgame.client.ui.menus;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.screen.Screen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RoomScreenOneVsOne extends Screen {
    private ArkanoidClientCore core;
    private BoxPlayer player1Box;
    private BoxPlayer player2Box;
    private MediaPlayer player1Background;
    private MediaPlayer player2Background;

    public RoomScreenOneVsOne() {
        super("Room 1 vs 1");
        if ((this.core = ArkanoidGame.core()) == null) {
            throw new IllegalStateException("Core is null!");
        }
        player1Background = CSAssets.randomGif();
        player2Background = CSAssets.randomGif();
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
        HBox centerBox = new HBox(400);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        VBox team1Box = createTeamBox("Player 1", Color.RED, player1Background);
        VBox team2Box = createTeamBox("Player 2", Color.BLUE, player2Background);

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


    private VBox createTeamBox(String teamName, Color c, MediaPlayer media) {
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

        String logoKey = teamName.equals("Player 1") ? "logoTeam_2" : "logoTeam_1";
        Image img = null;
        try {
            img = CSAssets.sprites.getAsImage("logo", logoKey);
        } catch (Exception e) {
            System.out.println("Failed to load sprite: logo " + logoKey);
        }
        if (img == null) {
            img = CSAssets.VS_BACKGROUND;
        }

        ImageView logoView = new ImageView(img);
        logoView.setFitWidth(28);
        logoView.setFitHeight(28);
        logoView.setPreserveRatio(true);

        HBox nameBox = new HBox(8);
        nameBox.setAlignment(Pos.CENTER);
        nameBox.getChildren().addAll(logoView, teamLabel);

        BoxPlayer player = createPlayerBox(teamName, media);
        if (teamName.equals("Player 1")) {
            player1Box = player;
        } else {
            player2Box = player;
        }
        
        teamBox.getChildren().addAll(nameBox, player);
        return teamBox;
    }

    private BoxPlayer createPlayerBox(String placeholder, MediaPlayer media) {
        BoxPlayer box = new BoxPlayer(placeholder, 288, 512, media);

        box.getNameField().setOnAction(e -> {
            String text = box.getNameField().getText().trim();

            // collect all other names
            String[] existingNames = getAllPlayerNames();

            // check duplicate
            if (text.isEmpty()) {
                SoundManager.clickFalse();
                box.showWarning("Name cannot be empty!");
                return;
            }

            // check duplicate
            for (String n : existingNames) {
                if (n != null && text.equals(n)) {
                    SoundManager.clickFalse();
                    box.showWarning("Duplicate name!");
                    return;
                }
            }

            SoundManager.clickSoundConfirm();
            box.hideWarning();
            box.getLabelName().setText(text);
            box.getLabelName().setVisible(true);
            box.removeField();
        });
        return box;
    }

    private String[] getAllPlayerNames() {
        String[] names = new String[2];
            if (player1Box != null && player1Box.getLabelName().isVisible()) {
                names[0] = player1Box.getLabelName().getText();
            }
            if (player2Box != null && player2Box.getLabelName().isVisible()) {
                names[1] = player2Box.getLabelName().getText();
            }
        return names;
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