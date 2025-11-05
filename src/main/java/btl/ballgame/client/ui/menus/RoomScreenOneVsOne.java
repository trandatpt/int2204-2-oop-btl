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
import javafx.scene.text.Font;

public class RoomScreenOneVsOne extends Screen {
    private ArkanoidClientCore core;
    private BoxPlayer team1Box;
    private BoxPlayer team2Box;

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
        root.setStyle("-fx-background-color: #1e1e1e;");

        // Header
        Label header = new Label("1 vs 1 Battle Room");
        header.setTextFill(Color.WHITE);
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setMargin(header, new Insets(10));
        root.setTop(header);

        // Center content
        HBox centerBox = new HBox(150);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        team1Box = createPlayerBox("Team 1");
        team2Box = createPlayerBox("Team 2");

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

    private BoxPlayer createPlayerBox(String placeholder) {
        BoxPlayer box = new BoxPlayer(placeholder, 300, 400);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(15));
        box.setPrefSize(300, 400);
        box.setStyle("-fx-background-color: #2b2b2b; -fx-border-color: white; -fx-border-width: 2px;");

        box.getNameField().setOnAction(e -> {
            String text = box.getNameField().getText().trim();

            String otherName = null;
            if (placeholder.equals("Team 1") && team2Box != null && team2Box.getLabelName() != null) {
                otherName = team2Box.getLabelName().getText();
            } else if (placeholder.equals("Team 2") && team1Box != null && team1Box.getLabelName() != null) {
                otherName = team1Box.getLabelName().getText();
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

        // add sprite placeholder (để trống cho sau này)
        // StackPane spriteArea = new StackPane();
        // spriteArea.setPrefSize(250, 300);
        // spriteArea.setStyle("-fx-border-color: #666; -fx-border-style: dashed; -fx-border-width: 2px;");
        // box.getChildren().addAll(nameField, duplicateLabel, nameLabel, spriteArea);

        return box;
    }


    private void startGame() {
        if (!team1Box.hasName() || !team2Box.hasName()) {
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