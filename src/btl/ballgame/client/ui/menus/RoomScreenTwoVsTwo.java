package btl.ballgame.client.ui.menus;

import btl.ballgame.client.ArkanoidClientCore;
import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ui.audio.SoundManager;
import btl.ballgame.client.ui.screen.Screen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class RoomScreenTwoVsTwo extends Screen {

    private ArkanoidClientCore core;
    private BoxPlayer[] team1Boxes = new BoxPlayer[2];
    private BoxPlayer[] team2Boxes = new BoxPlayer[2];

    public RoomScreenTwoVsTwo() {
        super("Room 2 vs 2");
        if ((this.core = ArkanoidGame.core()) == null) {
            throw new IllegalStateException("Core is null!");
        }
    }

    @Override
    public void onInit() {
        // Root layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1e1e1e;");

        // Header
        Label header = new Label("2 vs 2 Battle Room");
        header.setTextFill(Color.WHITE);
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setMargin(header, new Insets(10));
        root.setTop(header);

        // Center content
        HBox centerBox = new HBox(150);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        VBox team1Box = createTeamBox("Team 1", team1Boxes);
        VBox team2Box = createTeamBox("Team 2", team2Boxes);

        centerBox.getChildren().addAll(team1Box, team2Box);
        root.setCenter(centerBox);

        // Footer buttons
        Button startBtn = new Button("Start");
        Button switchModeBtn = new Button("Switch to 1vs1");
        Button exitBtn = new Button("Exit");

        MenuUtils.styleButton(startBtn, "#b22222", "#8b1a1a");
        MenuUtils.styleButton(switchModeBtn, "#476e49ff", "#374731ff");
        MenuUtils.styleButton(exitBtn, "#4d476e", "#353147");

        // Actions
        startBtn.setOnAction(e -> startGame());
        switchModeBtn.setOnAction(e -> switchTo1vs1());
        exitBtn.setOnAction(e -> exitRoom());

        HBox footer = new HBox(20, startBtn, switchModeBtn, exitBtn);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));

        root.setBottom(footer);

        this.addElement("room2v2", root);
    }

    private VBox createTeamBox(String teamName, BoxPlayer[] teamBoxes) {
        VBox teamBox = new VBox(25);
        teamBox.setAlignment(Pos.CENTER);
        teamBox.setPadding(new Insets(15));
        teamBox.setPrefSize(350, 450);
        teamBox.setStyle("-fx-background-color: #2b2b2b; -fx-border-color: white; -fx-border-width: 2px;");

        Label teamLabel = new Label(teamName);
        teamLabel.setTextFill(Color.WHITE);
        teamLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        for (int i = 0; i < 2; i++) {
            BoxPlayer playerBox = createPlayerBox(teamName + " - Player " + (i + 1));
            teamBoxes[i] = playerBox;
            teamBox.getChildren().add(playerBox);
        }

        teamBox.getChildren().add(0, teamLabel);
        return teamBox;
    }

    private BoxPlayer createPlayerBox(String placeholder) {
        BoxPlayer box = new BoxPlayer(placeholder, 280, 180);

        box.getNameField().setOnAction(e -> {
            String text = box.getNameField().getText().trim();

            // collect all other names
            String[] existingNames = getAllPlayerNames();

            if (text.isEmpty()) {
                box.showWarning("Name cannot be empty!");
                return;
            }

            // check duplicate
            for (String n : existingNames) {
                if (n != null && text.equalsIgnoreCase(n)) {
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
        //SoundManager.clickBottonLogin();
        // add code
    }

    private void switchTo1vs1() {
        //MenuUtils.displayOneVsOne();
        //temporarily
        RoomScreenOneVsOne one = new RoomScreenOneVsOne();
        ArkanoidGame.manager().setScreen(one);
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
