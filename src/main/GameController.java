import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GameController implements Initializable 
{
    @FXML
    private Canvas gameCanvas;
    private long lastPowerTime = 0;
    private static final long POWER_COOLDOWN = 500_000_000; 

    private EscapeRoomGame game;
    private AnimationTimer gameLoop;
    private boolean aPressed = false;
    private boolean dPressed = false;
    private String playerName = "Player";
    private boolean navigatedToStartAfterGameOver = false;

    // In GameController.java

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        game = new EscapeRoomGame();
        game.setPlayerName(playerName);

        // --- FIX STARTS HERE ---
        // Instead of trusting the Canvas to keep focus, we wait for the Scene to load
        // and attach the key listeners to the WHOLE SCENE.
        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKeyPressed);
                newScene.setOnKeyReleased(this::handleKeyReleased);
            }
        });

        // Optional: Allow clicking the canvas to ensure it's active (backup)
        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnMouseClicked(e -> gameCanvas.requestFocus());
        gameCanvas.requestFocus();
        // --- FIX ENDS HERE ---

        // Game loop
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Handle continuous movement
                if (!game.gameWon && !game.isGameOver()) {
                    if (aPressed) game.player.moveLeft();
                    if (dPressed) game.player.moveRight();
                }

                // Update game
                game.update();

                // Render
                game.render(gameCanvas.getGraphicsContext2D());

                // On game over, return to the start / name input screen once
                if (game.isGameOver() && !navigatedToStartAfterGameOver)
                {
                    navigatedToStartAfterGameOver = true;
                    EsscapeRoomApp.showStartScene();
                }
            }
        };
        gameLoop.start();
    }

    private void handleKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        
        switch (code) {
            case A:
            case LEFT:
                aPressed = true;
                break;
            case D:
            case RIGHT:
                dPressed = true;
                break;
            case W:
            case UP:
                if (!game.isGameOver()) {
                    game.player.jump();
                }
                break;
            case SPACE:
                if (game.gameWon) {
                    // Only allow progression when at least two stars were earned
                    if (game.getStarsEarnedThisLevel() <= 1) {
                        // Replay same level
                        game.initLevel();
                    } else {
                        game.level++;
                        game.initLevel();
                    }
                } else 
                {
                    if (!game.isGameOver()) 
                    {
                        long now = System.nanoTime();

                        if (now - lastPowerTime >= POWER_COOLDOWN) 
                        {
                            game.player.usePower();
                            lastPowerTime = now;
                        }
                    }
                }
                break;
            case E:
                // Absorb nearby color
                for (EscapeRoomGame.ColoredBlock cb : game.coloredBlocks) {
                    if (!cb.absorbed &&
                            Math.abs(game.player.x - cb.x) < 80 &&
                            Math.abs(game.player.y - cb.y) < 80) {
                        game.player.absorbColor(cb.color);
                        cb.absorbed = true;
                        break;
                    }
                }
                break;
            case L:
                if (e.isShiftDown()) {
                    game.grantBonusLife();
                }
                break;
        }
    }

    private void handleKeyReleased(KeyEvent e) {
        KeyCode code = e.getCode();
        
        switch (code) 
        {
            case A:
            case LEFT:
                aPressed = false;
                break;
            case D:
            case RIGHT:
                dPressed = false;
                break;
        }
    }

    public void setPlayerName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.playerName = name.trim();
        }
        if (game != null) {
            game.setPlayerName(this.playerName);
        }
    }
}

