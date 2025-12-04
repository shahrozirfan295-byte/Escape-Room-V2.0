package com.escaperoomv2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageGenerator {
    
    public static void generateAllImages() {
        try {
            // Create directories
            createDirectories();
            
            // Generate images
            generateBackground();
            generatePlayerImages();
            generateEnemyImages();
            generatePlatformImages();
            generateBlockImages();
            generateProjectileImages();
            generateParticleImages();
            generateGoalImage();
            generateUIImages();
            
            System.out.println("All images generated successfully!");
        } catch (Exception e) {
            System.err.println("Error generating images: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createDirectories() throws IOException {
        String[] dirs = {
            "src/main/resources/images/background",
            "src/main/resources/images/player",
            "src/main/resources/images/enemies",
            "src/main/resources/images/platforms",
            "src/main/resources/images/blocks",
            "src/main/resources/images/projectiles",
            "src/main/resources/images/particles",
            "src/main/resources/images/goal",
            "src/main/resources/images/ui"
        };
        
        for (String dir : dirs) {
            Path path = Paths.get(dir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        }
    }
    
    private static void generateBackground() throws IOException {
        BufferedImage img = new BufferedImage(1200, 700, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Gradient background
        GradientPaint gradient = new GradientPaint(0, 0, new java.awt.Color(135, 206, 235), 
                                                   0, 700, new java.awt.Color(200, 200, 200));
        g.setPaint(gradient);
        g.fillRect(0, 0, 1200, 700);
        
        // Add some clouds
        g.setColor(new java.awt.Color(255, 255, 255, 150));
        drawCloud(g, 200, 100, 80);
        drawCloud(g, 500, 150, 100);
        drawCloud(g, 800, 80, 90);
        drawCloud(g, 1000, 120, 85);
        
        g.dispose();
        saveImage(img, "src/main/resources/images/background/background.png");
    }
    
    private static void drawCloud(Graphics2D g, int x, int y, int size) {
        g.fillOval(x, y, size, size);
        g.fillOval(x + size/2, y - size/3, size, size);
        g.fillOval(x + size, y, size, size);
    }
    
    private static void generatePlayerImages() throws IOException {
        // Default player (gray)
        generatePlayerBall("player_default.png", new java.awt.Color(128, 128, 128));
        
        // Colored players
        generatePlayerBall("player_red.png", java.awt.Color.RED);
        generatePlayerBall("player_blue.png", java.awt.Color.BLUE);
        generatePlayerBall("player_yellow.png", java.awt.Color.YELLOW);
        generatePlayerBall("player_green.png", java.awt.Color.GREEN);
        generatePlayerBall("player_purple.png", new java.awt.Color(148, 0, 211));
        
        // Shield effect
        BufferedImage shield = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = shield.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new java.awt.Color(148, 0, 211, 100));
        g.setStroke(new BasicStroke(3.0f));
        g.drawOval(5, 5, 50, 50);
        g.drawOval(8, 8, 44, 44);
        g.dispose();
        saveImage(shield, "src/main/resources/images/player/shield.png");
    }
    
    private static void generatePlayerBall(String filename, java.awt.Color color) throws IOException {
        BufferedImage img = new BufferedImage(35, 35, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Main body with gradient
        GradientPaint gradient = new GradientPaint(0, 0, color.brighter(), 
                                                   35, 35, color.darker());
        g.setPaint(gradient);
        g.fillOval(0, 0, 35, 35);
        
        // Border
        g.setColor(java.awt.Color.BLACK);
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(0, 0, 35, 35);
        
        // Eyes
        g.setColor(java.awt.Color.WHITE);
        g.fillOval(8, 10, 8, 8);
        g.fillOval(20, 10, 8, 8);
        g.setColor(java.awt.Color.BLACK);
        g.fillOval(10, 12, 4, 4);
        g.fillOval(22, 12, 4, 4);
        
        g.dispose();
        saveImage(img, "src/main/resources/images/player/" + filename);
    }
    
    private static void generateEnemyImages() throws IOException {
        generateEnemyBall("enemy_normal.png", new java.awt.Color(64, 64, 64), 30);
        generateEnemyBall("enemy_fire.png", java.awt.Color.RED, 30);
        generateEnemyBall("enemy_ice.png", java.awt.Color.CYAN, 30);
        generateEnemyBall("enemy_electric.png", java.awt.Color.YELLOW, 30);
    }
    
    private static void generateEnemyBall(String filename, java.awt.Color color, int size) throws IOException {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Main body
        GradientPaint gradient = new GradientPaint(0, 0, color.brighter(), 
                                                   size, size, color.darker());
        g.setPaint(gradient);
        g.fillOval(0, 0, size, size);
        
        // Border
        g.setColor(java.awt.Color.BLACK);
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(0, 0, size, size);
        
        // Angry eyes
        g.setColor(java.awt.Color.WHITE);
        g.fillOval(6, 8, 6, 6);
        g.fillOval(18, 8, 6, 6);
        g.setColor(java.awt.Color.BLACK);
        g.fillOval(7, 9, 4, 4);
        g.fillOval(19, 9, 4, 4);
        
        // Mouth
        g.setColor(java.awt.Color.BLACK);
        g.setStroke(new BasicStroke(2.0f));
        g.drawArc(8, 18, 14, 8, 0, -180);
        
        g.dispose();
        saveImage(img, "src/main/resources/images/enemies/" + filename);
    }
    
    private static void generatePlatformImages() throws IOException {
        // Regular platform
        BufferedImage platform = new BufferedImage(150, 20, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = platform.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        GradientPaint gradient = new GradientPaint(0, 0, new java.awt.Color(120, 120, 120), 
                                                   0, 20, new java.awt.Color(80, 80, 80));
        g.setPaint(gradient);
        g.fillRect(0, 0, 150, 20);
        
        // Top highlight
        g.setColor(new java.awt.Color(150, 150, 150));
        g.fillRect(0, 0, 150, 3);
        
        g.dispose();
        saveImage(platform, "src/main/resources/images/platforms/platform.png");
        
        // Water platform
        BufferedImage water = new BufferedImage(120, 50, BufferedImage.TYPE_INT_ARGB);
        g = water.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(new java.awt.Color(50, 150, 255, 180));
        g.fillRect(0, 0, 120, 50);
        
        // Water waves
        g.setColor(new java.awt.Color(100, 200, 255, 150));
        for (int i = 0; i < 120; i += 20) {
            g.drawArc(i, 25, 20, 10, 0, 180);
        }
        
        g.dispose();
        saveImage(water, "src/main/resources/images/platforms/platform_water.png");
    }
    
    private static void generateBlockImages() throws IOException {
        generateColoredBlock("block_red.png", java.awt.Color.RED);
        generateColoredBlock("block_blue.png", java.awt.Color.BLUE);
        generateColoredBlock("block_yellow.png", java.awt.Color.YELLOW);
        generateColoredBlock("block_green.png", java.awt.Color.GREEN);
        generateColoredBlock("block_purple.png", new java.awt.Color(148, 0, 211));
    }
    
    private static void generateColoredBlock(String filename, java.awt.Color color) throws IOException {
        BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Glow effect
        g.setColor(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        g.fillOval(-10, -10, 70, 70);
        
        // Main block
        GradientPaint gradient = new GradientPaint(0, 0, color.brighter(), 
                                                   50, 50, color.darker());
        g.setPaint(gradient);
        g.fillRoundRect(0, 0, 50, 50, 5, 5);
        
        // Border
        g.setColor(color.brighter());
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(0, 0, 50, 50, 5, 5);
        
        // Shine
        g.setColor(new java.awt.Color(255, 255, 255, 100));
        g.fillOval(10, 10, 20, 20);
        
        g.dispose();
        saveImage(img, "src/main/resources/images/blocks/" + filename);
    }
    
    private static void generateProjectileImages() throws IOException {
        generateProjectile("projectile_fire.png", java.awt.Color.RED, 10);
        generateProjectile("projectile_ice.png", java.awt.Color.CYAN, 10);
        generateProjectile("projectile_electric.png", java.awt.Color.YELLOW, 10);
    }
    
    private static void generateProjectile(String filename, java.awt.Color color, int size) throws IOException {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Outer glow
        g.setColor(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
        g.fillOval(0, 0, size, size);
        
        // Inner core
        g.setColor(color.brighter());
        g.fillOval(2, 2, size - 4, size - 4);
        
        // Bright center
        g.setColor(java.awt.Color.WHITE);
        g.fillOval(3, 3, size - 6, size - 6);
        
        g.dispose();
        saveImage(img, "src/main/resources/images/projectiles/" + filename);
    }
    
    private static void generateParticleImages() throws IOException {
        generateParticle("particle_red.png", java.awt.Color.RED);
        generateParticle("particle_blue.png", java.awt.Color.BLUE);
        generateParticle("particle_yellow.png", java.awt.Color.YELLOW);
        generateParticle("particle_green.png", java.awt.Color.GREEN);
        generateParticle("particle_purple.png", new java.awt.Color(148, 0, 211));
    }
    
    private static void generateParticle(String filename, java.awt.Color color) throws IOException {
        BufferedImage img = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(color);
        g.fillOval(0, 0, 5, 5);
        
        g.dispose();
        saveImage(img, "src/main/resources/images/particles/" + filename);
    }
    
    private static void generateGoalImage() throws IOException {
        BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Star shape
        int[] xPoints = {25, 30, 40, 30, 35, 25, 15, 20, 10, 20};
        int[] yPoints = {5, 15, 20, 25, 40, 30, 40, 25, 20, 15};
        
        // Glow
        g.setColor(new java.awt.Color(255, 255, 0, 100));
        g.fillPolygon(xPoints, yPoints, 10);
        
        // Main star
        GradientPaint gradient = new GradientPaint(0, 0, java.awt.Color.YELLOW, 
                                                   50, 50, java.awt.Color.ORANGE);
        g.setPaint(gradient);
        g.fillPolygon(xPoints, yPoints, 10);
        
        // Border
        g.setColor(java.awt.Color.ORANGE);
        g.setStroke(new BasicStroke(2.0f));
        g.drawPolygon(xPoints, yPoints, 10);
        
        g.dispose();
        saveImage(img, "src/main/resources/images/goal/goal.png");
    }
    
    private static void generateUIImages() throws IOException {
        BufferedImage indicator = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = indicator.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(new java.awt.Color(200, 200, 200, 200));
        g.fillRoundRect(0, 0, 30, 30, 5, 5);
        g.setColor(java.awt.Color.BLACK);
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(0, 0, 30, 30, 5, 5);
        
        g.dispose();
        saveImage(indicator, "src/main/resources/images/ui/power_indicator.png");
    }
    
    private static void saveImage(BufferedImage img, String path) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        javax.imageio.ImageIO.write(img, "png", file);
    }
    
    public static void main(String[] args) {
        generateAllImages();
    }
}

