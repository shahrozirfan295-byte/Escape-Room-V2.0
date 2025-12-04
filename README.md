# Escape Room V2.0 (JavaFX)

A modernized JavaFX edition of the Escape Room game. This version replaces
Swing rendering with a Canvas-based renderer, uses FXML for layout, adds image
and audio resources, slows the overall pacing, and introduces a three-life
system with a restartable game-over flow.

## Key Gameplay Features
- **Smoother pacing**: player acceleration, projectiles, particles, and enemy
  patrol speeds were reduced for a more deliberate feel.
- **Three-life system**: the HUD shows up to three hearts. Enemy collisions
  deduct lives; after the last heart is gone the game fades out, resets to
  level 1, and refills lives automatically.
- **Backdoor (Shift+L)**: adds a life (max three) for debugging or as a hidden
  cheat.
- **Collisions fixed on level 1**: enemy contact now reliably decrements lives
  so you can no longer pass through opponents without consequence.
- **Per-level persistence**: lives are preserved when advancing to the next
  stage, encouraging careful play.

## Controls
- `A / Left Arrow` – Move left
- `D / Right Arrow` – Move right
- `W / Up Arrow` – Jump
- `SPACE` – Shoot or trigger the currently held power
- `E` – Absorb the closest color block in range
- `SHIFT + L` – Secret “back door” that grants one extra life (up to three)

## Building and Running
Requirements: Java 11+ and Maven 3.6+

```bash
# Compile everything (generates resources and images too)
mvn clean compile

# Launch the JavaFX application
mvn javafx:run
```

To run the generated JAR manually:
```bash
mvn clean package
java --module-path /path/to/javafx/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     -cp target/classes com.escaperoomv2.ColorAbsorptionApp
```

## Detailed File / Class Guide

### `pom.xml`
- Declares JavaFX modules (`controls`, `fxml`, `media`) and the OpenJFX Maven
  plugin so `mvn javafx:run` works out-of-the-box.
- Configures Java 11 compilation targets.

### `src/main/java/com/escaperoomv2/ColorAbsorptionApp.java`
- `ColorAbsorptionApp` (package `com.escaperoomv2`) extends `javafx.application.Application`.
  - `start(Stage)` loads `game.fxml`, wires the controller, and displays the
    fixed-size stage.
  - `main(String[])` simply launches JavaFX.

### `src/main/java/com/escaperoomv2/GameController.java`
- Acts as the FXML controller for `game.fxml`.
- Fields: `Canvas gameCanvas`, `ColorAbsorptionGame game`, booleans to track
  held movement keys, and the `AnimationTimer` loop.
- Methods:
  - `initialize(...)`: instantiates the `ColorAbsorptionGame`, registers key
    listeners, and starts the render/update loop.
  - `handleKeyPressed/Released(KeyEvent)`: implements movement, actions, and
    the `Shift+L` life backdoor while guarding against game-over states.

### `src/main/java/com/escaperoomv2/ColorAbsorptionGame.java`
Core gameplay engine. Important members:
- Global constants for board size, physics tuning (gravity, friction, jump and
  move speeds), life caps, and timers for the game-over fade.
- Public fields used by the controller: `player`, `coloredBlocks`,
  `enemies`, `platforms`, `projectiles`, `particles`, `goal`, `level`,
  `gameWon`.
- Game flow methods:
  - `initLevel()`: spawns a fresh player, clears entities, and builds the
    currently selected level layout.
  - `render(GraphicsContext)`: draws the background, every entity, HUD, and
    the win/game-over overlays using image resources with fallbacks.
  - `update()`: advances physics, handles collisions, slows the game when
    complete, decrements lives, and schedules the automatic restart after a
    wipe.
  - `handlePlayerHit()`: centralizes damage resolution, respawns the player,
    decrements lives, and triggers the game-over timer when necessary.
  - `grantBonusLife()`: public hook invoked by the controller for the cheat.
  - `isGameOver()`: exposes the current state so input can be ignored.
- Nested classes model all game entities:
  - `Player`: handles movement, gravity, powers, image rendering, and emits
    projectiles. Movement/jump magnitudes reference the new tuning constants.
  - `ColoredBlock`: draws pulsating color sources via cached sprites.
  - `Enemy`: patrols horizontally at the slower base speed, bounces off
    bounds, tracks health, and draws a health bar.
  - `Projectile`: lightweight bullets with per-color sprites and slower
    velocities.
  - `Particle`, `Platform`, `Goal`: lightweight visual systems that now render
    through the `ResourceManager` where applicable.

### `src/main/java/com/escaperoomv2/ResourceManager.java`
- Singleton that loads all PNG sprites and WAV placeholders from
  `src/main/resources`.
- Methods include `getImage`, `hasImage`, `playSound`, `stopSound`, and
  fallback drawing helpers used when an image asset is missing.
- Gracefully skips sounds that are not present so the game still runs silently.

### `src/main/java/com/escaperoomv2/ImageGenerator.java`
- Utility you can run (`mvn compile exec:java -Dexec.mainClass=...`) to
  regenerate every sprite procedurally. Produces gradients for players,
  enemies, blocks, platforms, projectiles, particles, goals, and the
  background.

### `src/main/java/com/escaperoomv2/ColorAbsorptionGame.java` (root copy)
- The original Swing implementation kept for archival/reference purposes. It
  is not used by the JavaFX runtime but can be compared against for logic
  parity or future ports.

### `src/main/resources/game.fxml`
- Minimal FXML layout that declares a single `Canvas` and binds it to
  `GameController`.

### `src/main/resources/images/**`
- Generated PNG art assets grouped by entity type (players, enemies, blocks,
  platforms, projectiles, particles, goals, UI).

### `src/main/resources/sounds/`
- Drop-in folder for `.wav` or `.mp3` effects. See `sounds/README.md` for the
  list of expected filenames (jump, shoot, hit, etc.). Sounds are optional.

### `IMAGES_AND_SOUNDS.md`
- Documents how images and sounds are produced, recommended sprite sizes, and
  how to regenerate or replace them with custom art/audio.

## Troubleshooting
- **No sounds**: ensure WAV files exist under `src/main/resources/sounds/`.
  Missing files are ignored, so silence just means the assets aren’t there.
- **Canvas not focused**: click the window once to regain keyboard focus if
  keys don’t seem responsive.
- **Performance**: the slower pacing is governed by constants near the top of
  `ColorAbsorptionGame`. Adjust them if you want a zippier feel.

Enjoy experimenting with new mechanics, the slower tempo, and the upgraded
presentation!
