# Images and Sounds Implementation

## Overview
The game now uses image resources instead of hardcoded drawing, and includes a sound effects system.

## Generated Images

All images have been automatically generated and are located in `src/main/resources/images/`:

### Background
- `background/background.png` - Sky gradient with clouds

### Player
- `player/player_default.png` - Default gray player
- `player/player_red.png` - Red colored player
- `player/player_blue.png` - Blue colored player
- `player/player_yellow.png` - Yellow colored player
- `player/player_green.png` - Green colored player
- `player/player_purple.png` - Purple colored player
- `player/shield.png` - Shield effect overlay

### Enemies
- `enemies/enemy_normal.png` - Normal enemy (gray)
- `enemies/enemy_fire.png` - Fire enemy (red)
- `enemies/enemy_ice.png` - Ice enemy (cyan)
- `enemies/enemy_electric.png` - Electric enemy (yellow)

### Platforms
- `platforms/platform.png` - Regular platform
- `platforms/platform_water.png` - Water platform

### Colored Blocks
- `blocks/block_red.png` - Red color block
- `blocks/block_blue.png` - Blue color block
- `blocks/block_yellow.png` - Yellow color block
- `blocks/block_green.png` - Green color block
- `blocks/block_purple.png` - Purple color block

### Projectiles
- `projectiles/projectile_fire.png` - Fire projectile
- `projectiles/projectile_ice.png` - Ice projectile
- `projectiles/projectile_electric.png` - Electric projectile

### Particles
- `particles/particle_red.png` - Red particle
- `particles/particle_blue.png` - Blue particle
- `particles/particle_yellow.png` - Yellow particle
- `particles/particle_green.png` - Green particle
- `particles/particle_purple.png` - Purple particle

### Goal
- `goal/goal.png` - Star-shaped goal

### UI
- `ui/power_indicator.png` - Power indicator frame

## Sound Effects

Sound files should be placed in `src/main/resources/sounds/`:

### Required Sounds (Optional - game works without them):
1. **jump.wav** - Player jump sound
2. **shoot.wav** - Projectile shooting sound
3. **absorb.wav** - Color absorption sound
4. **hit.wav** - Collision/hit sound
5. **enemy_death.wav** - Enemy defeated sound
6. **level_complete.wav** - Level completion sound
7. **power_use.wav** - Power usage sound (heal/shield)
8. **shield_activate.wav** - Shield activation sound
9. **background_music.wav** - Background music (optional)

## How It Works

### ResourceManager
- Singleton class that loads all images and sounds at startup
- Provides fallback drawing methods if images are missing
- Handles sound playback with volume control

### Image Loading
- Images are loaded from resources on game initialization
- If an image is missing, the game falls back to programmatic drawing
- All images are cached for performance

### Sound System
- Sounds are loaded as AudioClip objects
- Sounds play automatically on game events (jump, shoot, absorb, etc.)
- Volume can be controlled per sound
- Game works perfectly without sound files

## Customizing Images

You can replace any generated image with your own custom artwork:
1. Keep the same filename
2. Place in the same directory structure
3. Recommended size:
   - Player: 35x35 pixels
   - Enemies: 30x30 pixels
   - Blocks: 50x50 pixels
   - Projectiles: 10x10 pixels
   - Particles: 5x5 pixels
   - Goal: 50x50 pixels
   - Background: 1200x700 pixels
   - Platforms: Variable (will be scaled)

## Regenerating Images

To regenerate all images, run:
```bash
mvn compile exec:java -Dexec.mainClass="com.escaperoomv2.ImageGenerator"
```

## Notes

- All images use PNG format with transparency support
- Images are automatically scaled to fit game objects
- The game maintains backward compatibility - if images are missing, it draws shapes instead
- Sound files are optional - the game runs perfectly without them

