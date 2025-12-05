import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class EscapeRoomGame 
{
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 700;
    public static final int PLAYER_SIZE = 35;
    private static final int MAX_LIVES = 3;
    private static final double PLAYER_MOVE_SPEED = 2.3;
    private static final double PLAYER_JUMP_FORCE = -10;
    private static final double PLAYER_GRAVITY = 0.45;
    private static final double PLAYER_FRICTION = 0.9;
    private static final double ENEMY_BASE_SPEED = 3;
    private static final int GAME_OVER_DELAY_FRAMES = 180;
    private static final int POINTS_PER_ENEMY = 100;

    /**
     * Simple in-memory leaderboard entry.
     */
    public static class LeaderboardEntry 
    {
        public final String name;
        public final int score;

        public LeaderboardEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    public Player player;
    public ArrayList<ColoredBlock> coloredBlocks;
    public ArrayList<Enemy> enemies;
    public ArrayList<Platform> platforms;
    public ArrayList<Projectile> projectiles;
    public ArrayList<Particle> particles;
    public Goal goal;
    public int level;
    public boolean gameWon;
    private Random random;
    private ResourceManager resourceManager;
    private int lives;
    private boolean gameOver;
    private int gameOverTimer;

    // Scoring / rating
    private String playerName = "Player";
    private int totalScore = 0;
    private int enemiesAtLevelStart = 0;
    private int enemiesKilledThisLevel = 0;
    private int starsEarnedThisLevel = 0;

    // Shared leaderboard across runs of the game instance
    private static final ArrayList<LeaderboardEntry> leaderboard = new ArrayList<>();
    // Store the leaderboard in a visible text file under the project's resources folder
    // so it is easy to inspect and edit during development.
    private static final String LEADERBOARD_FILE =
            "src" + File.separator + "resources" + File.separator + "leaderboard.txt";

    public EscapeRoomGame() {
        random = new Random();
        level = 1;
        gameWon = false;
        resourceManager = ResourceManager.getInstance();
        lives = MAX_LIVES;
        gameOver = false;
        gameOverTimer = 0;
        resourceManager.playSound("background_music", 0.1);
        initLevel();
    }

    public void initLevel() {
        player = new Player(100, HEIGHT - 150);
        coloredBlocks = new ArrayList<>();
        enemies = new ArrayList<>();
        platforms = new ArrayList<>();
        projectiles = new ArrayList<>();
        particles = new ArrayList<>();
        gameWon = false;
        gameOver = false;

        // Reset per-level scoring
        enemiesAtLevelStart = 0;
        enemiesKilledThisLevel = 0;
        starsEarnedThisLevel = 0;

        // Ground
        platforms.add(new Platform(0, HEIGHT - 50, WIDTH, 50, Color.rgb(100, 100, 100)));

        

        switch (level) {
            case 1:
                createLevel1();
                break;
            case 2:
                createLevel2();
                break;
            case 3:
                createLevel3();
                break;
            case 4:
                createLevel4();
                break;
            default:
                createLevel5();
                break;
        }

        // Capture how many enemies were spawned for this level
        enemiesAtLevelStart = enemies.size();
    }

    private void createLevel1() {
        // Tutorial: Learn basic color absorption
        coloredBlocks.add(new ColoredBlock(300, HEIGHT - 150, 50, 50, "red"));
        coloredBlocks.add(new ColoredBlock(500, HEIGHT - 250, 50, 50, "blue"));
        coloredBlocks.add(new ColoredBlock(700, HEIGHT - 150, 50, 50, "yellow"));

        platforms.add(new Platform(450, HEIGHT - 200, 150, 20, Color.rgb(100, 100, 100)));

        enemies.add(new Enemy(600, HEIGHT - 100, "normal"));
        enemies.add(new Enemy(900, HEIGHT - 100, "normal"));
        enemies.add(new Enemy(750, HEIGHT - 150, "normal"));

        goal = new Goal(1050, HEIGHT - 150);
    }

    private void createLevel2() {
        // Ice and water mechanics
        coloredBlocks.add(new ColoredBlock(250, HEIGHT - 120, 50, 50, "blue"));
        coloredBlocks.add(new ColoredBlock(450, HEIGHT - 180, 50, 50, "red"));

        platforms.add(new Platform(300, HEIGHT - 150, 100, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(500, HEIGHT - 250, 150, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(750, HEIGHT - 200, 100, 20, Color.rgb(100, 100, 100)));

        // Water pool
        platforms.add(new Platform(600, HEIGHT - 50, 120, 50, Color.rgb(255, 85, 0)));

        enemies.add(new Enemy(350, HEIGHT - 100, "fire"));
        enemies.add(new Enemy(800, HEIGHT - 250, "normal"));
        enemies.add(new Enemy(900, HEIGHT - 100, "normal"));

        coloredBlocks.add(new ColoredBlock(800, HEIGHT - 250, 50, 50, "yellow"));

        goal = new Goal(1050, HEIGHT - 220);
    }

    private void createLevel3() {
        // Electricity puzzles
        coloredBlocks.add(new ColoredBlock(200, HEIGHT - 120, 50, 50, "yellow"));
        coloredBlocks.add(new ColoredBlock(400, HEIGHT - 320, 50, 50, "blue"));
        coloredBlocks.add(new ColoredBlock(700, HEIGHT - 220, 50, 50, "red"));

        platforms.add(new Platform(250, HEIGHT - 150, 100, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(350, HEIGHT - 270, 150, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(550, HEIGHT - 350, 100, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(750, HEIGHT - 250, 150, 20, Color.rgb(100, 100, 100)));

        enemies.add(new Enemy(300, HEIGHT - 200, "electric"));
        enemies.add(new Enemy(600, HEIGHT - 400, "ice"));
        enemies.add(new Enemy(850, HEIGHT - 300, "fire"));

        goal = new Goal(950, HEIGHT - 350);
    }

    private void createLevel4() {
        // All colors needed (without green power)
        coloredBlocks.add(new ColoredBlock(180, HEIGHT - 120, 50, 50, "red"));
        coloredBlocks.add(new ColoredBlock(280, HEIGHT - 220, 50, 50, "blue"));
        coloredBlocks.add(new ColoredBlock(380, HEIGHT - 320, 50, 50, "yellow"));
        coloredBlocks.add(new ColoredBlock(780, HEIGHT - 380, 50, 50, "purple"));

        platforms.add(new Platform(150, HEIGHT - 150, 100, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(250, HEIGHT - 250, 100, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(350, HEIGHT - 350, 100, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(500, HEIGHT - 300, 150, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(700, HEIGHT - 400, 150, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(900, HEIGHT - 300, 150, 20, Color.rgb(100, 100, 100)));

        enemies.add(new Enemy(200, HEIGHT - 200, "fire"));
        enemies.add(new Enemy(400, HEIGHT - 400, "electric"));
        enemies.add(new Enemy(650, HEIGHT - 350, "ice"));
        enemies.add(new Enemy(950, HEIGHT - 350, "normal"));

        goal = new Goal(1000, HEIGHT - 400);
    }

    private void createLevel5() {
        // Master challenge - complex but memory efficient
        
        // Create vertical layers with strategic gaps
        // Layer 1: Ground level stepping stones
        platforms.add(new Platform(150, HEIGHT - 120, 90, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(300, HEIGHT - 140, 80, 20, Color.rgb(100, 100, 100)));
        
        // Layer 2: Lower mid-level
        platforms.add(new Platform(120, HEIGHT - 220, 85, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(280, HEIGHT - 240, 70, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(420, HEIGHT - 230, 90, 20, Color.rgb(100, 100, 100)));
        
        // Layer 3: Mid-level with gaps
        platforms.add(new Platform(180, HEIGHT - 330, 75, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(340, HEIGHT - 350, 65, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(480, HEIGHT - 340, 80, 20, Color.rgb(100, 100, 100)));
        
        // Layer 4: Upper platforms
        platforms.add(new Platform(600, HEIGHT - 260, 90, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(760, HEIGHT - 300, 85, 20, Color.rgb(100, 100, 100)));
        
        // Layer 5: High level path
        platforms.add(new Platform(220, HEIGHT - 450, 80, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(380, HEIGHT - 470, 70, 20, Color.rgb(100, 100, 100)));
        
        // Layer 6: Final approach
        platforms.add(new Platform(880, HEIGHT - 380, 75, 20, Color.rgb(100, 100, 100)));
        platforms.add(new Platform(1000, HEIGHT - 460, 90, 20, Color.rgb(100, 100, 100)));
        
        // Water hazard
        platforms.add(new Platform(650, HEIGHT - 180, 110, 50, Color.rgb(255, 85, 0)));        
        // Strategic color placement
        coloredBlocks.add(new ColoredBlock(160, HEIGHT - 180, 50, 50, "red"));
        coloredBlocks.add(new ColoredBlock(235, HEIGHT - 510, 50, 50, "yellow"));
        coloredBlocks.add(new ColoredBlock(350, HEIGHT - 410, 50, 50, "blue"));
        coloredBlocks.add(new ColoredBlock(620, HEIGHT - 320, 50, 50, "green"));
        coloredBlocks.add(new ColoredBlock(780, HEIGHT - 360, 50, 50, "purple"));
        
        // Enemy placement - total 10 enemies
        enemies.add(new Enemy(220, HEIGHT - 100, "fire"));
        enemies.add(new Enemy(380, HEIGHT - 100, "normal"));
        enemies.add(new Enemy(190, HEIGHT - 370, "electric"));
        enemies.add(new Enemy(350, HEIGHT - 390, "ice"));
        enemies.add(new Enemy(490, HEIGHT - 380, "fire"));
        enemies.add(new Enemy(620, HEIGHT - 300, "electric"));
        enemies.add(new Enemy(770, HEIGHT - 340, "ice"));
        enemies.add(new Enemy(230, HEIGHT - 490, "fire"));
        enemies.add(new Enemy(890, HEIGHT - 420, "electric"));
        enemies.add(new Enemy(1020, HEIGHT - 500, "ice"));
        
        // Goal placement
        goal = new Goal(1025, HEIGHT - 520);
    }
    public void render(GraphicsContext gc) 
    {
    
        if (resourceManager.hasImage("background") && level % 2 == 0) 
        {
            gc.drawImage(resourceManager.getImage("background"), 0, 0, WIDTH, HEIGHT);
        } 
        else if (resourceManager.hasImage("background2") && level % 2 != 0) 
        {
            gc.drawImage(resourceManager.getImage("background2"), 0, 0, WIDTH, HEIGHT);
            // System.out.println("DEBUG : Background image 2 naattt workingggg");
        } 
        else 
        {
            gc.setFill(Color.rgb(200, 200, 200));
            // System.out.println("DEBUG:::: IMAGES NOTTTT WORKINGGGGG"));
            gc.fillRect(0, 0, WIDTH, HEIGHT);
        }
        // // Draw background
        // if (resourceManager.hasImage("background")) {
        //     gc.drawImage(resourceManager.getImage("background"), 0, 0, WIDTH, HEIGHT);
        // } else {
        //     gc.setFill(Color.rgb(200, 200, 200));
        //     gc.fillRect(0, 0, WIDTH, HEIGHT);
        // }

        // Draw platforms
        for (Platform p : platforms) {
            p.draw(gc, resourceManager);
        }

        // Draw colored blocks
        for (ColoredBlock cb : coloredBlocks) {
            cb.draw(gc, this, resourceManager);
        }

        // Draw particles
        for (Particle p : particles) {
            p.draw(gc, this, resourceManager);
        }

        // Draw projectiles
        for (Projectile proj : projectiles) {
            proj.draw(gc, resourceManager);
        }

        // Draw enemies
        for (Enemy e : enemies) {
            e.draw(gc, resourceManager);
        }

        // Draw goal
        goal.draw(gc, resourceManager);

        // Draw player
        player.draw(gc, this, resourceManager);

        // Draw UI
        drawUI(gc);

        // Game state overlays
        if (gameOver) {
            gc.setFill(Color.rgb(0, 0, 0, 0.75));
            gc.fillRect(0, 0, WIDTH, HEIGHT);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 60));
            drawCenteredString(gc, "GAME OVER", WIDTH / 2.0, HEIGHT / 2.0 - 200);

            // Final score
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 30));
            gc.setFill(Color.LIGHTGOLDENRODYELLOW);
            drawCenteredString(gc, "Player: " + playerName, WIDTH / 2.0, HEIGHT / 2.0 - 140);
            drawCenteredString(gc, "Total Points: " + totalScore, WIDTH / 2.0, HEIGHT / 2.0 - 100);

            // Leaderboard
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 26));
            gc.setFill(Color.WHITE);
            drawCenteredString(gc, "Leaderboard (Top 5)", WIDTH / 2.0, HEIGHT / 2.0 - 40);

            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 22));
            int rank = 1;
            int yOffset = -10;
            for (LeaderboardEntry entry : getTopLeaderboardEntries(5)) {
                String line = rank + ". " + entry.name + " - " + entry.score;
                drawCenteredString(gc, line, WIDTH / 2.0, HEIGHT / 2.0 + yOffset);
                yOffset += 28;
                rank++;
            }

            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
            gc.setFill(Color.LIGHTGRAY);
            drawCenteredString(gc, "Restarting at Level 1...", WIDTH / 2.0, HEIGHT / 2.0 + yOffset + 20);
        } else if (gameWon) {
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRect(0, 0, WIDTH, HEIGHT);
            gc.setFill(Color.YELLOW);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 54));
            drawCenteredString(gc, "FIN..! Level Complete!", WIDTH / 2.0, HEIGHT / 2.0 - 160);

            // Star rating
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 40));
            gc.setFill(Color.GOLD);
            String starsText = buildStarsText(starsEarnedThisLevel);
            drawCenteredString(gc, "Rating: " + starsText, WIDTH / 2.0, HEIGHT / 2.0 - 110);

            // // Kills info
            // gc.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
            // gc.setFill(Color.WHITE);
            // String killsInfo = "Enemies defeated: " + enemiesKilledThisLevel + " / " + Math.max(1, enemiesAtLevelStart);
            // drawCenteredString(gc, killsInfo, WIDTH / 2.0, HEIGHT / 2.0 - 70);

            // Instructions depending on stars
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 30));
            if (starsEarnedThisLevel <= 1) {
                drawCenteredString(gc, "You can do Better", WIDTH / 2.0, HEIGHT / 2.0 + 10);
                drawCenteredString(gc, "Press SPACE to replay this level", WIDTH / 2.0, HEIGHT / 2.0 + 45);
            } 
            else 
                {
                    
                
                    // drawCenteredString(gc, "You earned " + starsEarnedThisLevel + " stars!", WIDTH / 2.0, HEIGHT / 2.0 + 10);
                    drawCenteredString(gc, "Press SPACE for next level", WIDTH / 2.0, HEIGHT / 2.0 + 45);
                }
                
        }
    }

    private void drawUI(GraphicsContext gc) 
    {
        // Level info
        if (level % 2 == 0) 
        {
            gc.setFill(Color.BLACK);
        } 
        else if (level % 2 != 0) 
        {
            gc.setFill(Color.WHITE);
        } 
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.fillText("Level: " + level, 20, 30);

        // Score
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.fillText("Score: " + totalScore, 20, 60);

        // Current color power
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.fillText("Power: ", 20, 90);
        if (!player.currentColor.isEmpty()) {
            Color powerColor = getColorFromString(player.currentColor);
            gc.setFill(powerColor);
            gc.fillRect(110, 70, 30, 30);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(110, 70, 30, 30);
            gc.setFill(Color.BLACK);
            gc.fillText(player.currentColor.toUpperCase(), 150, 90);
        } else {
            gc.fillText("None", 110, 90);
        }

        // Lives indicator
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        if (level % 2 == 0) 
        {
            gc.setFill(Color.BLACK);
        } 
        else if (level % 2 != 0) 
        {
            gc.setFill(Color.WHITE);
        } 
        gc.fillText("Lives:", 20, 125);
        for (int i = 0; i < MAX_LIVES; i++) {
            double heartX = 90 + i * 28;
            gc.setFill(i < lives ? Color.RED : Color.DARKGRAY);
            gc.fillOval(heartX, 108, 20, 20);
            gc.setStroke(Color.BLACK);
            gc.strokeOval(heartX, 108, 20, 20);
        }

        // Color guide
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText("Colors:", 20, 140);

        String[] colorNames = {"RED: Fire", "BLUE: Ice/Water", "YELLOW: Electric", "PURPLE: Shield"};
        String[] colorKeys = {"red", "blue", "yellow", "purple"};

        for (int i = 0; i < colorNames.length; i++) 
        {
            gc.setFill(getColorFromString(colorKeys[i]));
            gc.fillRect(20, 150 + i * 25, 20, 20);
            if (level % 2 == 0) 
            gc.setFill(Color.BLACK);
            else 
            gc.setFill(Color.WHITE);
            gc.strokeRect(20, 150 + i * 25, 20, 20);
            if (level % 2 == 0) 
            gc.setFill(Color.BLACK);
            else 
            gc.setFill(Color.WHITE);
            gc.fillText(colorNames[i], 45, 165 + i * 25);
        }
    }

    private void drawCenteredString(GraphicsContext gc, String text, double x, double y) {
        Text textNode = new Text(text);
        textNode.setFont(gc.getFont());
        double width = textNode.getLayoutBounds().getWidth();
        gc.fillText(text, x - width / 2, y);
    }

    public Color getColorFromString(String color) {
        switch (color) {
            case "red": return Color.RED;
            case "blue": return Color.BLUE;
            case "yellow": return Color.YELLOW;
            case "purple": return Color.rgb(148, 0, 211);
            default: return Color.GRAY;
        }
    }

    public void update() {
        if (gameOver) {
            // Freeze gameplay when game over; scene navigation back to the
            // start screen is handled by the controller.
            return;
        }

        if (gameWon) return;

        player.update(platforms);

        // Water hazard: if player is in water, they sink and lose a life
        if (isPlayerInWater()) {
            handlePlayerHit();
        }

        // Update enemies
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(platforms, player);

            // Check collision with player
            if (enemy.collidesWith(player) && !player.hasShield) {
                handlePlayerHit();
            }

            if (enemy.health <= 0) {
                enemiesKilledThisLevel++;
                totalScore += POINTS_PER_ENEMY;

                for (int j = 0; j < 10; j++) {
                    particles.add(new Particle(enemy.x + 15, enemy.y + 15, enemy.type));
                }
                resourceManager.playSound("enemy_death", 0.4);
                enemies.remove(i);
            }
        }

        // Update projectiles
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile proj = projectiles.get(i);
            proj.update();

            if (proj.x < 0 || proj.x > WIDTH || proj.y < 0 || proj.y > HEIGHT) {
                projectiles.remove(i);
                continue;
            }

            // Check collision with enemies
            for (Enemy enemy : enemies) {
                if (proj.collidesWith(enemy)) {
                    enemy.takeDamage(proj.damage, proj.type);
                    resourceManager.playSound("hit", 0.3);
                    projectiles.remove(i);
                    break;
                }
            }
        }

        // Update particles
        for (int i = particles.size() - 1; i >= 0; i--) {
            particles.get(i).update();
            if (particles.get(i).life <= 0) {
                particles.remove(i);
            }
        }

        // Check goal
        if (!gameWon && goal.collidesWith(player)) {
            calculateStarsForCurrentLevel();
            gameWon = true;
            resourceManager.playSound("level_complete", 0.5);
        }
    }

    private void handlePlayerHit() {
        if (gameWon || gameOver) {
            return;
        }

        lives = Math.max(0, lives - 1);
        if(!isPlayerInWater())
        {
            resourceManager.playSound("hit", 1.0);
        }
        else
        {
            resourceManager.playSound("aag",1.0);
        }

        player.x = 100;
        player.y = HEIGHT - 150;
        player.vx = 0;
        player.vy = 0;
        player.currentColor = "";
        player.shieldTimer = 0;
        player.hasShield = false;

        if (lives <= 0) {
            triggerGameOver();
        }
    }

    private void triggerGameOver() {
        gameOver = true;
        gameOverTimer = GAME_OVER_DELAY_FRAMES;
        addScoreToLeaderboard();
    }

    private boolean isPlayerInWater() {
        for (Platform p : platforms) {
            // Water platforms are rendered with this specific blue color
            if (p.isWater) {
                boolean intersects =
                        player.x + PLAYER_SIZE > p.x &&
                        player.x < p.x + p.width &&
                        // treat touching the water surface as being in water
                        player.y + PLAYER_SIZE >= p.y &&
                        player.y < p.y + p.height;
                if (intersects) {
                    return true;
                }
            }
        }
        return false;
    }

    private void restartFromBeginning() {
        level = 1;
        lives = MAX_LIVES;
        totalScore = 0;
        gameOver = false;
        gameWon = false;
        gameOverTimer = 0;
        initLevel();
    }

    public void grantBonusLife() {
        if (gameOver) return;
        if (lives < MAX_LIVES) {
            lives++;
            resourceManager.playSound("power_use", 0.4);
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    // ---- Scoring / leaderboard helpers ----

    private void calculateStarsForCurrentLevel() {
        if (enemiesAtLevelStart <= 0) {
            // No enemies: always grant 3 stars
            starsEarnedThisLevel = 3;
            return;
        }

        double ratio = (double) enemiesKilledThisLevel / (double) enemiesAtLevelStart;
        if (ratio >= 0.8) {
            starsEarnedThisLevel = 3;
        } else if (ratio >= 0.5) {
            starsEarnedThisLevel = 2;
        } else if (enemiesKilledThisLevel > 0) {
            starsEarnedThisLevel = 1;
        } else {
            starsEarnedThisLevel = 0;
        }
    }

    private void addScoreToLeaderboard() {
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player";
        }
        leaderboard.add(new LeaderboardEntry(playerName, totalScore));
        // Keep highest scores first
        leaderboard.sort((a, b) -> Integer.compare(b.score, a.score));
        saveLeaderboardToDisk();
    }

    private ArrayList<LeaderboardEntry> getTopLeaderboardEntries(int max) {
        ArrayList<LeaderboardEntry> top = new ArrayList<>();
        for (int i = 0; i < leaderboard.size() && i < max; i++) {
            top.add(leaderboard.get(i));
        }
        return top;
    }

    /**
     * Public snapshot of the leaderboard to be used by menus / start screens.
     */
    public static ArrayList<LeaderboardEntry> getLeaderboardSnapshot(int max) {
        ArrayList<LeaderboardEntry> top = new ArrayList<>();
        for (int i = 0; i < leaderboard.size() && i < max; i++) {
            top.add(leaderboard.get(i));
        }
        return top;
    }

    public static void loadLeaderboardFromDisk() {
        leaderboard.clear();
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length != 2) continue;
                String name = parts[0].trim();
                String scoreStr = parts[1].trim();
                try {
                    int score = Integer.parseInt(scoreStr);
                    leaderboard.add(new LeaderboardEntry(name, score));
                } catch (NumberFormatException ignore) {
                    // Skip malformed line
                }
            }
            // Ensure leaderboard is sorted descending
            leaderboard.sort((a, b) -> Integer.compare(b.score, a.score));
        } catch (IOException e) {
            // If reading fails, just keep the in-memory leaderboard empty
        }
    }

    private static void saveLeaderboardToDisk() {
        File file = new File(LEADERBOARD_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (LeaderboardEntry entry : leaderboard) {
                writer.write(entry.name + "\t" + entry.score);
                writer.newLine();
            }
        } catch (IOException e) {
            // Ignore save errors to avoid crashing the game
        }
    }

    public void setPlayerName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.playerName = name.trim();
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getStarsEarnedThisLevel() {
        return starsEarnedThisLevel;
    }

    private String buildStarsText(int stars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if (i < stars) {
                sb.append("★");
            } else {
                sb.append("☆");
            }
            if (i < 2) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public class Player {
        public double x, y, vx, vy;
        public boolean onGround;
        public String currentColor = "";
        public boolean hasShield = false;
        public int shieldTimer = 0;

        Player(double x, double y) {
            this.x = x;
            this.y = y;
        }

        void update(ArrayList<Platform> platforms) {
            vy += PLAYER_GRAVITY;
            x += vx;
            y += vy;

            vx *= PLAYER_FRICTION;
            onGround = false;

            // Platform collision
            for (Platform p : platforms) {
                if (x + PLAYER_SIZE > p.x && x < p.x + p.width &&
                        y + PLAYER_SIZE > p.y && y < p.y + p.height && vy > 0) {
                    y = p.y - PLAYER_SIZE;
                    vy = 0;
                    onGround = true;
                }
            }

            // Keep player within horizontal bounds
            if (x < 0) {
                x = 0;
                vx = 0;
            } else if (x + PLAYER_SIZE > WIDTH) {
                x = WIDTH - PLAYER_SIZE;
                vx = 0;
            }

            // Keep player within vertical bounds (ceiling / floor)
            if (y < 0) {
                y = 0;
                vy = 0;
            } else if (y + PLAYER_SIZE > HEIGHT) {
                y = HEIGHT - PLAYER_SIZE;
                vy = 0;
                onGround = true;
            }

            // Shield timer
            if (shieldTimer > 0) {
                shieldTimer--;
                hasShield = true;
            } else {
                hasShield = false;
            }
        }

        void moveLeft() {
            vx = -PLAYER_MOVE_SPEED;
        }

        void moveRight() {
            vx = PLAYER_MOVE_SPEED;
        }

        void jump() {
            if (onGround) {
                vy = PLAYER_JUMP_FORCE;
                resourceManager.playSound("jump", 0.01);
            }
        }

        void shoot() {
            if (currentColor.equals("red")) {
                projectiles.add(new Projectile(x + PLAYER_SIZE, y + PLAYER_SIZE / 2, 7, 0, "fire", 2));
                resourceManager.playSound("shoot", 0.4);
            } else if (currentColor.equals("blue")) {
                projectiles.add(new Projectile(x + PLAYER_SIZE, y + PLAYER_SIZE / 2, 6, 0, "ice", 1));
                resourceManager.playSound("shoot", 0.4);
            } else if (currentColor.equals("yellow")) {
                projectiles.add(new Projectile(x + PLAYER_SIZE, y + PLAYER_SIZE / 2, 8, 0, "electric", 3));
                resourceManager.playSound("shoot", 0.4);
            }
        }

        void usePower() {
            if (currentColor.equals("purple")) {
                // Shield
                shieldTimer = 300;
                hasShield = true;
                currentColor = "";
                resourceManager.playSound("shield_activate", 0.6);
            } else {
                shoot();
            }
        }

        void absorbColor(String color) {
            currentColor = color;
            for (int i = 0; i < 20; i++) {
                particles.add(new Particle(x + PLAYER_SIZE / 2, y + PLAYER_SIZE / 2, color));
            }
            resourceManager.playSound("absorb", 0.9);
        }

        void draw(GraphicsContext gc, EscapeRoomGame game, ResourceManager rm) {
            String imageKey = "player_default";
            if (!currentColor.isEmpty()) {
                imageKey = "player_" + currentColor;
            }
            
            if (rm.hasImage(imageKey)) {
                gc.drawImage(rm.getImage(imageKey), x, y, PLAYER_SIZE, PLAYER_SIZE);
            } else {
                Color playerColor = currentColor.isEmpty() ? Color.GRAY : game.getColorFromString(currentColor);
                rm.drawPlayerFallback(gc, x, y, PLAYER_SIZE, playerColor);
            }

            // Shield effect
            if (hasShield) {
                if (rm.hasImage("player_shield")) {
                    gc.drawImage(rm.getImage("player_shield"), x - 10, y - 10, PLAYER_SIZE + 20, PLAYER_SIZE + 20);
                } else {
                    gc.setStroke(Color.rgb(148, 0, 211, 0.4));
                    gc.strokeOval(x - 10, y - 10, PLAYER_SIZE + 20, PLAYER_SIZE + 20);
                    gc.strokeOval(x - 8, y - 8, PLAYER_SIZE + 16, PLAYER_SIZE + 16);
                }
            }
        }
    }

    public class ColoredBlock {
        int x, y, width, height;
        String color;
        boolean absorbed = false;
        int pulseFrame = 0;

        ColoredBlock(int x, int y, int width, int height, String color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
        }

        void draw(GraphicsContext gc, EscapeRoomGame game, ResourceManager rm) 
        {
            if (absorbed) return;

            pulseFrame++;
            int pulse = (int) (Math.sin(pulseFrame * 0.05) * 5);
            
            String imageKey = "block_" + color;
            if (rm.hasImage(imageKey)) {
                gc.drawImage(rm.getImage(imageKey), x - pulse / 2, y - pulse / 2, width + pulse, height + pulse);
            } else {
                Color blockColor = game.getColorFromString(color);
                rm.drawBlockFallback(gc, x - pulse / 2, y - pulse / 2, width + pulse, height + pulse, blockColor);
            }
        }
    }

    public class Enemy {
        public double x, y;
        public int health;
        public String type;
        public Color color;
        public double vx = ENEMY_BASE_SPEED;

        Enemy(double x, double y, String type) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.health = 3;

            switch (type) {
                case "fire": color = Color.RED; break;
                case "ice": color = Color.CYAN; break;
                case "electric": color = Color.YELLOW; break;
                default: color = Color.DARKGRAY;
            }
        }

        void update(ArrayList<Platform> platforms, Player player) {
            x += vx;

            if (x < 50 || x > WIDTH - 80) {
                vx = -vx;
            }
        }

        /*
        DAMAGE :

        fire     + ice       = 2x damage
        ice      + fire      = 2x damage
        ice      + electric  = 2x damage
        else                 = normal damage
        */
        

        void takeDamage(int damage, String damageType) 
        {
            if ((type.equals("fire") && damageType.equals("ice")) || (type.equals("ice") && damageType.equals("fire")) || (type.equals("electric") && damageType.equals("ice"))) 
            {
                damage *= 2;
            }
            health -= damage;
        }

        boolean collidesWith(Player p) {
            return Math.abs(p.x + PLAYER_SIZE / 2 - (x + 15)) < 30 &&
                    Math.abs(p.y + PLAYER_SIZE / 2 - (y + 15)) < 30;
        }

        void draw(GraphicsContext gc, ResourceManager rm) {
            String imageKey = "enemy_" + type;
            if (rm.hasImage(imageKey)) {
                gc.drawImage(rm.getImage(imageKey), x, y, 30, 30);
            } else {
                rm.drawEnemyFallback(gc, x, y, 30, color);
            }

            // Health bar
            gc.setFill(Color.RED);
            gc.fillRect(x, y - 10, 30, 5);
            gc.setFill(Color.GREEN);
            gc.fillRect(x, y - 10, 10 * health, 5);
        }
    }

    public class Projectile {
        double x, y, vx, vy;
        String type;
        int damage;

        Projectile(double x, double y, double vx, double vy, String type, int damage) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.type = type;
            this.damage = damage;
        }

        void update() {
            x += vx;
            y += vy;
        }

        boolean collidesWith(Enemy e) {
            return Math.abs(x - (e.x + 15)) < 20 && Math.abs(y - (e.y + 15)) < 20;
        }

        void draw(GraphicsContext gc, ResourceManager rm) {
            String imageKey = "projectile_" + type;
            if (rm.hasImage(imageKey)) {
                gc.drawImage(rm.getImage(imageKey), x - 5, y - 5, 10, 10);
            } else {
                Color projColor = Color.WHITE;
                if (type.equals("fire")) projColor = Color.RED;
                else if (type.equals("ice")) projColor = Color.CYAN;
                else if (type.equals("electric")) projColor = Color.YELLOW;
                rm.drawProjectileFallback(gc, x, y, 10, projColor);
            }
        }
    }

    public class Particle {
        double x, y, vx, vy;
        int life = 30;
        String color;

        Particle(double x, double y, String color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.vx = (Math.random() - 0.5) * 2;
            this.vy = (Math.random() - 0.5) * 2;
        }

        void update() {
            x += vx;
            y += vy;
            vy += 0.2;
            life--;
        }

        void draw(GraphicsContext gc, EscapeRoomGame game, ResourceManager rm) {
            String imageKey = "particle_" + color;
            double alpha = Math.max(0, life / 30.0);
            
            if (rm.hasImage(imageKey)) {
                gc.setGlobalAlpha(alpha);
                gc.drawImage(rm.getImage(imageKey), x, y, 5, 5);
                gc.setGlobalAlpha(1.0);
            } else {
                Color particleColor = game.getColorFromString(color);
                rm.drawParticleFallback(gc, x, y, 5, particleColor, alpha);
            }
        }
    }

    public class Platform {
        int x, y, width, height;
        Color color;
        boolean isWater;

        Platform(int x, int y, int width, int height, Color color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.isWater = color.equals(Color.rgb(255, 85, 0));        
        }

        void draw(GraphicsContext gc, ResourceManager rm) {
            String imageKey = "platform";
            // Check if it's a water platform (blue color)
            if (isWater) {
                imageKey = "platform_water";
            }
            
            if (rm.hasImage(imageKey)) {
                gc.drawImage(rm.getImage(imageKey), x, y, width, height);
            } else {
                rm.drawPlatformFallback(gc, x, y, width, height, color);
            }
        }
    }

    public class Goal 
    {
        int x, y;
        int frame = 0;

        Goal(int x, int y) {
            this.x = x;
            this.y = y;
        }

        boolean collidesWith(Player p) {
            return Math.abs(p.x + PLAYER_SIZE / 2 - (x + 25)) < 40 &&
                    Math.abs(p.y + PLAYER_SIZE / 2 - (y + 25)) < 40;
        }

        void draw(GraphicsContext gc, ResourceManager rm) {
            frame++;
            int pulse = (int) (Math.sin(frame * 0.1) * 5);

            if (rm.hasImage("goal")) {
                gc.drawImage(rm.getImage("goal"), x, y + pulse, 50, 50);
            } else {
                rm.drawGoalFallback(gc, x, y, pulse);
            }
        }
    }
}

