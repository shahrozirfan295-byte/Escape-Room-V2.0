import java.io.IOException;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

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

        PauseTransition delay = new PauseTransition(Duration.seconds(5));
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

        // Canvas for drawing the splash screen image
        Canvas canvas = new Canvas(EscapeRoomGame.WIDTH, EscapeRoomGame.HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw splash/start image
        ResourceManager rc = ResourceManager.getInstance();
        if (rc.hasImage("start")) {
            gc.drawImage(rc.getImage("start"), 0, 0, 1200, 700);
        } else {
            // Fallback if image missing
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.fillRect(0,0, 1200, 700);
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.fillText("Loading...", 600, 350);
        }

        root.getChildren().add(canvas);

        Scene scene = new Scene(root, EscapeRoomGame.WIDTH, EscapeRoomGame.HEIGHT);

        // Play background audio
        rc.playSound("background_music", 0.1);

        return scene;
    }

    private Scene buildStartScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPrefSize(EscapeRoomGame.WIDTH, EscapeRoomGame.HEIGHT);

        // --- TOP SECTION: TITLE ---
        Label title = new Label("Escape Room V2.0");
        // Removed hardcoded setFont, using CSS class instead
        title.getStyleClass().add("game-title");

        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        BorderPane.setMargin(title, new Insets(20, 0, 10, 0));
        root.setTop(title);

        // --- CENTER SECTION: INPUT & BUTTON ---
        VBox centerBox = new VBox(15); // Increased spacing slightly
        centerBox.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Enter your name to begin:");
        nameLabel.getStyleClass().add("prompt-label");

        TextField nameField = new TextField();
        nameField.setPromptText("Player");
        nameField.setMaxWidth(300);
        nameField.getStyleClass().add("name-field");

        Button startButton = new Button("Start Game");
        startButton.getStyleClass().add("start-button");

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

        // --- BOTTOM SECTION: LEADERBOARD & CONTROLS ---
        HBox bottomBox = new HBox(60);
        bottomBox.setPadding(new Insets(20, 40, 40, 40));
        bottomBox.setAlignment(Pos.TOP_CENTER);

        // Left Side: Leaderboard
        VBox leaderboardBox = new VBox(8);
        leaderboardBox.setAlignment(Pos.TOP_LEFT);

        Label lbTitle = new Label("Leaderboard");
        lbTitle.getStyleClass().add("section-header");
        leaderboardBox.getChildren().add(lbTitle);

        List<EscapeRoomGame.LeaderboardEntry> entries = EscapeRoomGame.getLeaderboardSnapshot(5);
        if (entries.isEmpty()) {
            Label none = new Label("No scores yet. Be the first!");
            none.getStyleClass().add("list-text");
            leaderboardBox.getChildren().add(none);
        } else {
            int rank = 1;
            for (EscapeRoomGame.LeaderboardEntry entry : entries) {
                Label line = new Label(rank + ". " + entry.name + " - " + entry.score);
                line.getStyleClass().add("list-text");
                leaderboardBox.getChildren().add(line);
                rank++;
            }
        }

        // Right Side: Controls
        VBox controlsBox = new VBox(6);
        controlsBox.setAlignment(Pos.TOP_LEFT);

        Label controlsTitle = new Label("Controls");
        controlsTitle.getStyleClass().add("section-header");
        controlsBox.getChildren().add(controlsTitle);

        controlsBox.getChildren().add(makeControlLabel("A / Left Arrow – Move left"));
        controlsBox.getChildren().add(makeControlLabel("D / Right Arrow – Move right"));
        controlsBox.getChildren().add(makeControlLabel("W / Up Arrow – Jump"));
        controlsBox.getChildren().add(makeControlLabel("SPACE – Shoot / use power"));
        controlsBox.getChildren().add(makeControlLabel("E – Absorb nearby color"));
        // controlsBox.getChildren().add(makeControlLabel("SHIFT + L – Hidden bonus life"));

        bottomBox.getChildren().addAll(leaderboardBox, controlsBox);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, EscapeRoomGame.WIDTH, EscapeRoomGame.HEIGHT);

        // --- LINK CSS FILE ---
        try {
            // Looks for style.css in the resources folder
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("Could not load style.css. Ensure it is in the resources folder.");
        }

        return scene;
    }

    private Label makeControlLabel(String text) {
        Label label = new Label(text);
        // Use CSS class instead of hardcoded font
        label.getStyleClass().add("list-text");
        return label;
    }

    private void startGame(Stage stage, String playerName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EsscapeRoomApp.class.getResource("/game.fxml"));
        // Note: You might want to add CSS to the game scene too if needed,
        // by modifying the FXML or adding stylesheets to the loaded scene here.
        Scene scene = new Scene(fxmlLoader.load(), EscapeRoomGame.WIDTH, EscapeRoomGame.HEIGHT);

        // Inject player name into controller
        Object controller = fxmlLoader.getController();
        if (controller instanceof GameController) {
            ((GameController) controller).setPlayerName(playerName);
        }

        stage.setScene(scene);
        // Ensure game canvas gets focus
        scene.getRoot().requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}