package btl.ballgame.client.ui.game;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ClientArkanoidMatch;
import btl.ballgame.client.CSAssets;
import btl.ballgame.client.net.systems.CSWorld;
import btl.ballgame.client.ui.screen.Screen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
// import javafx.scene.layout.Priority; // Không còn cần ở đây
// import javafx.scene.layout.Region; // Không còn cần ở đây
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GameScreen extends Screen {
    private CSWorld world;
    private ClientArkanoidMatch match;

    public GameScreen() {
        super("game");
        this.match = ArkanoidGame.core().getActiveMatch();
        this.world = match.getGameWorld();
    }

    @Override
    public void onInit() {
        BorderPane root = new BorderPane();

        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);

        BackgroundImage backgroundImage = new BackgroundImage(
                CSAssets.VS_BACKGROUND,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );

        root.setBackground(new Background(backgroundImage));

        StackPane centerPane = new StackPane(new Canvas(world.getWidth(), world.getHeight()));
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setStyle("-fx-background-color: transparent;");
        root.setCenter(centerPane);

        // --- (RED TEAM) ---
        VBox infoPane_R = new VBox(10);
        infoPane_R.setPrefWidth(400);
        infoPane_R.setMaxHeight(600);
        infoPane_R.setPadding(new Insets(15));
        infoPane_R.setStyle("-fx-background-color: rgba(34, 34, 34, 0.4); " +
                "-fx-border-color: white; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10;"
        );
        infoPane_R.setAlignment(Pos.TOP_CENTER);

        // Title
        HBox teamBox_R = new HBox(8);
        teamBox_R.setAlignment(Pos.TOP_CENTER);
        teamBox_R.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 5;"
        );
        teamBox_R.setPadding(new Insets(5));
        StackPane colorBox_R = new StackPane();
        colorBox_R.setPrefSize(30, 30);
        colorBox_R.setStyle("-fx-background-color: red; " +
                "-fx-border-color: white;"
        );
        Label teamLabel_R = new Label("RED");
        teamLabel_R.setTextFill(Color.RED);
        teamLabel_R.setStyle("-fx-font-size: 20px; " +
                "-fx-font-weight: bold;"
        );
        teamBox_R.getChildren().addAll(colorBox_R, teamLabel_R);

        // Team's life
        Label teamLivesLabel_R = new Label("Team Lives");
        teamLivesLabel_R.setStyle("-fx-font-size: 16px; " +
                "-fx-text-fill: white;"
        );

        HBox hearts_R = new HBox(5);
        hearts_R.setAlignment(Pos.TOP_CENTER);
        for (int i = 0; i < 3; i++) {
            Label heart = new Label("❤");
            heart.setStyle("-fx-font-size: 24px;");
            heart.setTextFill(Color.RED);
            hearts_R.getChildren().add(heart);
        }

        // (Score)
        HBox scoreBox_R = new HBox(10);
        scoreBox_R.setAlignment(Pos.CENTER);
        Label scoreLabel_R = new Label("Score:");
        scoreLabel_R.setTextFill(Color.WHITE);
        scoreLabel_R.setStyle("-fx-font-size: 20px; " +
                "-fx-font-weight: bold;"
        );
        Label scoreValue_R = new Label("0000000000000000"); // Placeholder
        scoreValue_R.setTextFill(Color.WHITE);
        scoreValue_R.setStyle("-fx-font-size: 20px; " +
                "-fx-font-weight: bold; " +
                "-fx-font-family: 'Monospaced';"
        );
        scoreBox_R.getChildren().addAll(scoreLabel_R, scoreValue_R);

        // Player 1 (Right)
        VBox player1Info_R = PlayerInfoBuilder.createPlayerInfoBox("P1",
                "Dat09", "SEMI AUTO", Pos.CENTER_RIGHT
        );

        // Player 2 (Right)
        VBox player2Info_R = PlayerInfoBuilder.createPlayerInfoBox("P2",
                "Huy34", "FULL AUTO", Pos.CENTER_RIGHT
        );

        // Clean up and add new content to infoPane_R
        infoPane_R.getChildren().clear();
        infoPane_R.getChildren().addAll(
                teamBox_R,
                teamLivesLabel_R,
                hearts_R,
                scoreBox_R,
                player1Info_R,
                player2Info_R
        );
        VBox.setMargin(player1Info_R, new Insets(15, 0, 0, 0));
        VBox.setMargin(player2Info_R, new Insets(10, 0, 0, 0));

        StackPane rightContainer = new StackPane();
        rightContainer.setPadding(new Insets(0, 20, 0, 20));
        rightContainer.getChildren().add(infoPane_R);


        // --- (BLUE TEAM) ---
        VBox infoPane_L = new VBox(10);
        infoPane_L.setPrefWidth(400);
        infoPane_L.setMaxHeight(600);
        infoPane_L.setPadding(new Insets(15));
        infoPane_L.setStyle("-fx-background-color: rgba(34, 34, 34, 0.4); " +
                "-fx-border-color: white; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10;"
        );
        infoPane_L.setAlignment(Pos.TOP_CENTER);

        // Title
        HBox teamBox_L = new HBox(8);
        teamBox_L.setAlignment(Pos.TOP_CENTER);
        teamBox_L.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 5;"
        );
        teamBox_L.setPadding(new Insets(5));
        StackPane colorBox_L = new StackPane();
        colorBox_L.setPrefSize(30, 30);
        colorBox_L.setStyle("-fx-background-color: blue; " +
                "-fx-border-color: white;"
        );
        Label teamLabel_L = new Label("BLUE");
        teamLabel_L.setTextFill(Color.BLUE);
        teamLabel_L.setStyle("-fx-font-size: 20px; " +
                "-fx-font-weight: bold;"
        );
        teamBox_L.getChildren().addAll(colorBox_L, teamLabel_L);

        // Team's life
        Label teamLivesLabel_L = new Label("Team Lives");
        teamLivesLabel_L.setStyle("-fx-font-size: 16px; " +
                "-fx-text-fill: white;"
        );

        HBox hearts_L = new HBox(5);
        hearts_L.setAlignment(Pos.TOP_CENTER);
        for (int i = 0; i < 3; i++) {
            Label heart = new Label("❤");
            heart.setStyle("-fx-font-size: 24px;");
            heart.setTextFill(Color.RED);
            hearts_L.getChildren().add(heart);
        }

        // (Score)
        HBox scoreBox_L = new HBox(10);
        scoreBox_L.setAlignment(Pos.CENTER);
        Label scoreLabel_L = new Label("Score:");
        scoreLabel_L.setTextFill(Color.WHITE);
        scoreLabel_L.setStyle("-fx-font-size: 20px; " +
                "-fx-font-weight: bold;"
        );
        Label scoreValue_L = new Label("0000000000000000"); // Placeholder
        scoreValue_L.setTextFill(Color.WHITE);
        scoreValue_L.setStyle("-fx-font-size: 20px; " +
                "-fx-font-weight: bold; " +
                "-fx-font-family: 'Monospaced';"
        );
        scoreBox_L.getChildren().addAll(scoreLabel_L, scoreValue_L);

        // Player 1 (Left)
        VBox player1Info_L = PlayerInfoBuilder.createPlayerInfoBox("P1",
                "Dat09", "SEMI AUTO", Pos.CENTER_LEFT
        );

        // Player 2 (Left)
        VBox player2Info_L = PlayerInfoBuilder.createPlayerInfoBox("P2",
                "Huy34", "FULL AUTO", Pos.CENTER_LEFT
        );

        // Clean up and add new content to infoPane_L
        infoPane_L.getChildren().clear();
        infoPane_L.getChildren().addAll(
                teamBox_L,
                teamLivesLabel_L,
                hearts_L,
                scoreBox_L,
                player1Info_L,
                player2Info_L
        );
        VBox.setMargin(player1Info_L, new Insets(15, 0, 0, 0));
        VBox.setMargin(player2Info_R, new Insets(10, 0, 0, 0));

        StackPane leftContainer = new StackPane();
        leftContainer.setPadding(new Insets(0, 20, 0, 20));
        leftContainer.getChildren().add(infoPane_L);

        root.setRight(rightContainer);
        root.setLeft(leftContainer);
        this.addElement("root", root);
    }

    @Override
    public void onRemove() {

    }

}

