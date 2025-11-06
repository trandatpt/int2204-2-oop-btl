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
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

public class RoomScreenTwoVsTwo extends Screen {

    private ArkanoidClientCore core;
    private BoxPlayer[] team1Boxes = new BoxPlayer[2];
    private BoxPlayer[] team2Boxes = new BoxPlayer[2];
    private MediaPlayer[] team1Background = new MediaPlayer[2];
    private MediaPlayer[] team2Background = new MediaPlayer[2];

    public RoomScreenTwoVsTwo() {
        super("Room 2 vs 2");
        if ((this.core = ArkanoidGame.core()) == null) {
            throw new IllegalStateException("Core is null!");
        }
        // add Image
        team1Background[0] = CSAssets.randomGif();
        team1Background[1] = CSAssets.randomGif();
        team2Background[0] = CSAssets.randomGif();
        team2Background[1] = CSAssets.randomGif();
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

        Label labelHeader = new Label("2 vs 2 Battle Room");
        labelHeader.setTextFill(Color.WHITE);
        labelHeader.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        VBox headerBox = new VBox(5);
        headerBox.setPadding(new Insets(10));

        headerBox.getChildren().addAll(roomId, labelHeader);
        headerBox.setAlignment(Pos.CENTER);

        BorderPane.setAlignment(headerBox, Pos.CENTER);
        BorderPane.setMargin(headerBox, new Insets(10));
        root.setTop(headerBox);

        // Center content
        HBox centerBox = new HBox(300);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        VBox team1Box = createTeamBox("Team 1", Color.RED, team1Background, team1Boxes);
        VBox team2Box = createTeamBox("Team 2", Color.BLUE, team2Background, team2Boxes);

        centerBox.getChildren().addAll(team1Box, team2Box);
        root.setCenter(centerBox);

        // Footer buttons
        Button startBtn = new Button("Start");
        Button exitBtn = new Button("Exit");

        MenuUtils.styleButton(startBtn, "#b22222", "#8b1a1a");
        MenuUtils.styleButton(exitBtn, "#4d476e", "#353147");

        // Actions
        startBtn.setOnAction(e -> startGame());
        exitBtn.setOnAction(e -> exitRoom());

        HBox footer = new HBox(40, startBtn, exitBtn);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));

        root.setBottom(footer);

        this.addElement("room2v2", root);
    }

    private VBox createTeamBox(String teamName, Color c, MediaPlayer[] media, BoxPlayer[] teamBoxes) {
        VBox teamBoxFull = new VBox(10);
        teamBoxFull.setAlignment(Pos.CENTER);
        teamBoxFull.setPadding(new Insets(15));
        teamBoxFull.setMaxSize(800, 800);


        HBox teamBox = new HBox(20);
        teamBox.setAlignment(Pos.CENTER);
        teamBox.setPrefSize(600, 800);

        String color = null;
        if (c.equals(Color.RED)) {
            color = "red";
        } else {
            color = "blue";
        }

        teamBoxFull.setStyle(
            "-fx-background-color: #2b2b2b;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 2px;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;"
        );

        Label teamLabel = new Label(teamName);
        teamLabel.setTextFill(c);
        teamLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        String logoKey = teamName.equals("Team 1") ? "logoTeam_2" : "logoTeam_1";
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

        for (int i = 0; i < 2; i++) {
            BoxPlayer playerBox = createPlayerBox(teamName + " - Player " + (i + 1), media[i]);
            teamBoxes[i] = playerBox;
            teamBox.getChildren().add(playerBox);
        }
        teamBoxFull.getChildren().addAll(nameBox, teamBox);
        teamBoxFull.setAlignment(Pos.CENTER);

        return teamBoxFull;
    }

    private BoxPlayer createPlayerBox(String placeholder, MediaPlayer media) {
        BoxPlayer box = new BoxPlayer(placeholder, 259.2, 460.8, media);

        box.getNameField().setOnAction(e -> {
            String text = box.getNameField().getText().trim();

            // collect all other names
            String[] existingNames = getAllPlayerNames();

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

            // success
            SoundManager.clickSoundConfirm();
            box.hideWarning();
            box.getLabelName().setText(text);
            box.getLabelName().setVisible(true);
            box.removeField();
        });
        return box;
    }

    private String[] getAllPlayerNames() {
        String[] names = new String[4];
        for (int i = 0; i < 2; i++) {
            if (team1Boxes[i] != null && team1Boxes[i].getLabelName().isVisible()) {
                names[i] = team1Boxes[i].getLabelName().getText();
            }
            if (team2Boxes[i] != null && team2Boxes[i].getLabelName().isVisible()) {
                names[i + 2] = team2Boxes[i].getLabelName().getText();
            }
        }
        return names;
    }

    private void startGame() {
        for (int i = 0; i < 2; i++) {
            if (team1Boxes[i] == null || !team1Boxes[i].getLabelName().isVisible()
            || team2Boxes[i] == null || !team2Boxes[i].getLabelName().isVisible()) {
                System.out.println("Please enter all player names!");
                SoundManager.clickFalse();
                return;
            }
        }
        SoundManager.clickBottonLogin();
        // add code
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