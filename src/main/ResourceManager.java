import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;

public class ResourceManager {
    private static ResourceManager instance;
    private Map<String, Image> images = new HashMap<>();
    private Map<String, AudioClip> sounds = new HashMap<>();
    
    private ResourceManager() {
        loadImages();
        loadSounds();
    }
    
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }
    
    private void loadImages()
    {
        //Start Image

        loadImage("start","/images/start/start.png");
        // Background
        loadImage("background2", "/images/background/background2.png");
        loadImage("background", "/images/background/background.png");
        
        // Player
        loadImage("player_default", "/images/player/player_default.png");
        loadImage("player_red", "/images/player/player_red.png");
        loadImage("player_blue", "/images/player/player_blue.png");
        loadImage("player_yellow", "/images/player/player_yellow.png");
        loadImage("player_green", "/images/player/player_green.png");
        loadImage("player_purple", "/images/player/player_purple.png");
        loadImage("player_shield", "/images/player/shield.png");
        
        // Enemies
        loadImage("enemy_normal", "/images/enemies/enemy_normal.png");
        loadImage("enemy_fire", "/images/enemies/enemy_fire.png");
        loadImage("enemy_ice", "/images/enemies/enemy_ice.png");
        loadImage("enemy_electric", "/images/enemies/enemy_electric.png");
        
        // Platforms
        loadImage("platform", "/images/platforms/platform.png");
        loadImage("platform_water", "/images/platforms/platform_water.png");
        
        // Colored Blocks
        loadImage("block_red", "/images/blocks/block_red.png");
        loadImage("block_blue", "/images/blocks/block_blue.png");
        loadImage("block_yellow", "/images/blocks/block_yellow.png");
        loadImage("block_green", "/images/blocks/block_green.png");
        loadImage("block_purple", "/images/blocks/block_purple.png");
        
        // Projectiles
        loadImage("projectile_fire", "/images/projectiles/projectile_fire.png");
        loadImage("projectile_ice", "/images/projectiles/projectile_ice.png");
        loadImage("projectile_electric", "/images/projectiles/projectile_electric.png");
        
        // Particles
        loadImage("particle_red", "/images/particles/particle_red.png");
        loadImage("particle_blue", "/images/particles/particle_blue.png");
        loadImage("particle_yellow", "/images/particles/particle_yellow.png");
        loadImage("particle_green", "/images/particles/particle_green.png");
        loadImage("particle_purple", "/images/particles/particle_purple.png");
        
        // Goal
        loadImage("goal", "/images/goal/goal.png");
        
        // UI
        loadImage("ui_power_indicator", "/images/ui/power_indicator.png");
    }
    
    private void loadImage(String key, String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                Image img = new Image(is);
                images.put(key, img);
            }
        } catch (Exception e) {
            System.out.println("Could not load image: " + path + " - " + e.getMessage());
        }
    }
    
    private void loadSounds() {
        loadSound("jump", "/sounds/jump.wav");
        loadSound("shoot", "/sounds/shoot.wav");
        loadSound("absorb", "/sounds/absorb.wav");
        loadSound("hit", "/sounds/hit.wav");
        loadSound("enemy_death", "/sounds/enemy_death.wav");
        loadSound("level_complete", "/sounds/level_complete.wav");
        loadSound("power_use", "/sounds/power_use.wav");
        loadSound("shield_activate", "/sounds/shield_activate.wav");
        loadSound("background_music", "/sounds/background_music.wav");
        loadSound("aag","/sounds/aag.wav");
    }
    
    private void loadSound(String key, String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
                AudioClip sound = new AudioClip(url.toExternalForm());
                if (key.equals("background_music")) 
                    {
                    sound.setCycleCount(AudioClip.INDEFINITE);
                }
                sounds.put(key, sound);
            }
        } catch (Exception e) {
            // Sound file not found - this is okay, game will work without sounds
        }
    }
    
    public Image getImage(String key) {
        return images.get(key);
    }
    
    public boolean hasImage(String key) {
        return images.containsKey(key) && images.get(key) != null;
    }
    
    public void playSound(String key) {
        AudioClip sound = sounds.get(key);
        if (sound != null) {
            sound.play();
        }
    }
    
    public void playSound(String key, double volume) {
        AudioClip sound = sounds.get(key);
        if (sound != null) {
            sound.setVolume(volume);
            sound.play();
        }
    }
    
    public void stopSound(String key) {
        AudioClip sound = sounds.get(key);
        if (sound != null) {
            sound.stop();
        }
    }
    
    // Fallback drawing methods when images are not available
    public void drawPlayerFallback(GraphicsContext gc, double x, double y, int size, Color color) {
        gc.setFill(color);
        gc.fillOval(x, y, size, size);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(x, y, size, size);
        
        // Eyes
        gc.setFill(Color.WHITE);
        gc.fillOval(x + 8, y + 10, 8, 8);
        gc.fillOval(x + 20, y + 10, 8, 8);
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 10, y + 12, 4, 4);
        gc.fillOval(x + 22, y + 12, 4, 4);
    }
    
    public void drawEnemyFallback(GraphicsContext gc, double x, double y, int size, Color color) {
        gc.setFill(color);
        gc.fillOval(x, y, size, size);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(x, y, size, size);
    }
    
    public void drawBlockFallback(GraphicsContext gc, int x, int y, int width, int height, Color color) {
        gc.setFill(color);
        gc.fillRect(x, y, width, height);
        gc.setStroke(color.brighter());
        gc.strokeRect(x, y, width, height);
    }
    
    public void drawPlatformFallback(GraphicsContext gc, int x, int y, int width, int height, Color color) {
        gc.setFill(color);
        gc.fillRect(x, y, width, height);
        gc.setStroke(color.brighter());
        gc.strokeRect(x, y, width, height);
    }
    
    public void drawProjectileFallback(GraphicsContext gc, double x, double y, int size, Color color) {
        gc.setFill(color);
        gc.fillOval(x - size/2, y - size/2, size, size);
        gc.setFill(color.brighter());
        gc.fillOval(x - size/2 + 2, y - size/2 + 2, size - 4, size - 4);
    }
    
    public void drawParticleFallback(GraphicsContext gc, double x, double y, int size, Color color, double alpha) {
        gc.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        gc.fillOval(x, y, size, size);
    }
    
    public void drawGoalFallback(GraphicsContext gc, int x, int y, int pulse) {
        gc.setFill(Color.YELLOW);
        double[] xPoints = {x + 25, x + 40 + pulse, x + 30, x + 35, x + 25, x + 15, x + 20, x + 10 - pulse};
        double[] yPoints = {y + pulse, y + 20, y + 25, y + 45, y + 35, y + 45, y + 25, y + 20};
        gc.fillPolygon(xPoints, yPoints, 8);
        gc.setStroke(Color.ORANGE);
        gc.strokePolygon(xPoints, yPoints, 8);
    }
}