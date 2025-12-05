import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class EsscapeRoomApp extends Application 
{
    private static EsscapeRoomApp instance;
    private static Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException 
    {
        instance = this;
        primaryStage = stage;

        // Load persisted leaderboard before showing the start screen
        EscapeRoomGame.loadLeaderboardFromDisk();

        stage.setTitle("Escape Room V2.0");
        stage.setResizable(false);

        // First show a splash screen, then transition to the main start screen
        Scene splashScene = buildSplashScene(stage);
        stage.setScene(splashScene);
        stage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(2.5));
        delay.setOnFinished(e -> {
            Scene startScene = buildStartScene(stage);
            stage.setScene(startScene);
        });
        delay.play();
    }

    public static void showStartScene() {
        if (instance == null || primaryStage == null) {
            return;
        }
        Scene startScene = instance.buildStartScene(primaryStage);
        primaryStage.setScene(startScene);
    }

    private Scene buildSplashScene(Stage stage) 
    {
        StackPane root = new StackPane();
        root.setPrefSize(EscapeRoomGame.WIDTH, EscapeRoomGame.HEIGHT);

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        Label title = new Label("Escape Room V2.0");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 40));

        Label subtitle = new Label("This time with multiple rooms");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 22));

        box.getChildren().addAll(title, subtitle);
        root.getChildren().add(box);

        // Add style classes for CSS
        root.getStyleClass().add("splash-root");
        title.getStyleClass().add("splash-title");
        subtitle.getStyleClass().add("splash-subtitle");

        Scene scene = new Scene(root, EscapeRoomGame.WIDTH, EscapeRoomGame.HEIGHT);
        // Attach stylesheet for the splash screen (splash.css is under src/main/resources).
        scene.getStylesheets().add(
                getClass().getResource("/splash.css").toExternalForm()
        );
        return scene;
    }

    private Scene buildStartScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPrefSize(EscapeRoomGame.WIDTH, EscapeRoomGame.HEIGHT);

        // Title at top
        Label title = new Label("Escape Room V2.0");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        BorderPane.setMargin(title, new Insets(20, 0, 10, 0));
        root.setTop(title);

        // Center: name input and start button
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        Label nameLabel = new Label("Enter your name to begin:");
        nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        TextField nameField = new TextField();
        nameField.setPromptText("Player");
        nameField.setMaxWidth(300);

        Button startButton = new Button("Start Game");
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        startButton.setOnAction(e -> {
            String entered = nameField.getText();
            String playerName = (entered == null || entered.trim().isEmpty()) ? "Player" : entered.trim();
            try {
                startGame(stage, playerName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        centerBox.getChildren().addAll(nameLabel, nameField, startButton);
        root.setCenter(centerBox);

        // Bottom: leaderboard (left half) and controls (right half)
        HBox bottomBox = new HBox(60);
        bottomBox.setPadding(new Insets(20, 40, 40, 40));
        bottomBox.setAlignment(Pos.TOP_CENTER);

        // Leaderboard
        VBox leaderboardBox = new VBox(8);
        leaderboardBox.setAlignment(Pos.TOP_LEFT);
        Label lbTitle = new Label("Leaderboard");
        lbTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        leaderboardBox.getChildren().add(lbTitle);

        List<EscapeRoomGame.LeaderboardEntry> entries =
                EscapeRoomGame.getLeaderboardSnapshot(5);
        if (entries.isEmpty()) {
            Label none = new Label("No scores yet. Be the first!");
            none.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
            leaderboardBox.getChildren().add(none);
        } else {
            int rank = 1;
            for (EscapeRoomGame.LeaderboardEntry entry : entries) {
                Label line = new Label(rank + ". " + entry.name + " - " + entry.score);
                line.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
                leaderboardBox.getChildren().add(line);
                rank++;
            }
        }

        // Controls on right half
        VBox controlsBox = new VBox(6);
        controlsBox.setAlignment(Pos.TOP_LEFT);
        Label controlsTitle = new Label("Controls");
        controlsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        controlsBox.getChildren().add(controlsTitle);

        controlsBox.getChildren().add(makeControlLabel("A / Left Arrow – Move left"));
        controlsBox.getChildren().add(makeControlLabel("D / Right Arrow – Move right"));
        controlsBox.getChildren().add(makeControlLabel("W / Up Arrow – Jump"));
        controlsBox.getChildren().add(makeControlLabel("SPACE – Shoot / use power"));
        controlsBox.getChildren().add(makeControlLabel("E – Absorb nearby color"));
        controlsBox.getChildren().add(makeControlLabel("SHIFT + L – Hidden bonus life"));

        bottomBox.getChildren().addAll(leaderboardBox, controlsBox);
        root.setBottom(bottomBox);

        return new Scene(root, EscapeRoomGame.WIDTH, EscapeRoomGame.HEIGHT);
    }

    private Label makeControlLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        return label;
    }

    private void startGame(Stage stage, String playerName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EsscapeRoomApp.class.getResource("/game.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), EscapeRoomGame.WIDTH, EscapeRoomGame.HEIGHT);

        // Inject player name into controller
        Object controller = fxmlLoader.getController();
        if (controller instanceof GameController) {
            ((GameController) controller).setPlayerName(playerName);
        }

        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}

