# Goose Survivor

A 2D survival game built with [libGDX](https://libgdx.com/) using the [Ashley](https://github.com/libgdx/ashley) Entity-Component-System (ECS) architecture.
![img.png](misc/gameplay.png)

## Links

| | |
| --- | --- |
| **Executable JAR** | [Download the latest release](https://github.com/ENG1C4T3/goose-survivor/releases/latest) |
| **GitHub repository** | [github.com/ENG1C4T3/goose-survivor](https://github.com/ENG1C4T3/goose-survivor) |

## Controls

| Key | Action |
| --- | --- |
| WASD / arrow keys | Move |
| E | Interact / pick up item (a `[E] ...` popup appears above whatever is in range) |
| 1-9 | Use the item in that hotbar slot |
| P | Pause / resume |

## Architecture

Diagrams referenced in the Architecture report (`Arch1.pdf`), numbered to match it. Click a diagram to expand it.

<details>
<summary><b>1.1</b></summary>
<img src="misc/architecture/1_1.png">
</details>

<details>
<summary><b>3.2</b></summary>
<img src="misc/architecture/3_2.png">
</details>

<details>
<summary><b>3.2.1</b></summary>
<img src="misc/architecture/3_2_1.png">
</details>

<details>
<summary><b>3.2.2</b></summary>
<img src="misc/architecture/3_2_2.png">
</details>

<details>
<summary><b>3.2.3</b></summary>
<img src="misc/architecture/3_2_3.png">
</details>

<details>
<summary><b>3.2.4</b></summary>
<img src="misc/architecture/3_2_4.png">
</details>

<details>
<summary><b>3.2.5</b></summary>
<img src="misc/architecture/3_2_5.png">
</details>

<details>
<summary><b>3.2.6</b></summary>
<img src="misc/architecture/3_2_6.png">
</details>

<details>
<summary><b>3.2.7</b></summary>
<img src="misc/architecture/3_2_7.png">
</details>

<details>
<summary><b>3.3.1</b></summary>
<img src="misc/architecture/3_3_1.png">
</details>

<details>
<summary><b>4.1</b></summary>
<img src="misc/architecture/4.1.jpg">
</details>

<details>
<summary><b>4.1.1</b></summary>
<img src="misc/architecture/4.1.1.png">
</details>

<details>
<summary><b>4.2</b></summary>
<img src="misc/architecture/4.2.jpg">
</details>

<details>
<summary><b>4.2.1</b></summary>
<img src="misc/architecture/4.2.1.png">
</details>

<details>
<summary><b>4.3</b></summary>
<img src="misc/architecture/4.3.jpg">
</details>

<details>
<summary><b>4.3.1</b></summary>
<img src="misc/architecture/4.3.1.jpg">
</details>

<details>
<summary><b>4.3.2</b></summary>
<img src="misc/architecture/4.3.2.jpg">
</details>

<details>
<summary><b>4.4</b></summary>
<img src="misc/architecture/4.4.jpg">
</details>

<details>
<summary><b>4.5.1</b></summary>
<img src="misc/architecture/4.5.1.jpg">
</details>

<details>
<summary><b>4.5.2</b></summary>
<img src="misc/architecture/4.5.2.jpg">
</details>

## Timeline

<details>
  <summary> <b>Initial Timeline</b></summary>
  <br>
  <img src="misc/timelines/init_timeline.svg" width="100%">
</details>

<details>
  <summary><b>14-10-2026</b></summary>
  <br>
  <img src="misc/timelines/14_10_26_timeline.svg" width="100%">
</details>

<details>
  <summary><b>21-10-2026</b></summary>
  <br>
  <img src="misc/timelines/21_10_26_timeline.svg" width="100%">
</details>

<details>
  <summary><b>28-10-2026</b></summary>
  <br>
  <img src="misc/timelines/28_10_26_timeline.svg" width="100%">
</details>

<details open>
  <summary><b>11-11-2026</b></summary>
  <br>
  <img src="misc/timelines/11_11_26_timeline.svg" width="100%">
</details>

## Handover

This section is everything a new team needs to pick the project up and keep developing it.

### 1. Getting set up

**Requirements**

- **JDK 17 or newer, set as `JAVA_HOME`.** The wrapper uses Gradle 9.6, which itself needs Java 17+ to *start*. The `java.toolchain` setting (Java 17) only covers compiling. With an older `JAVA_HOME` you get `Gradle requires JVM 17 or later to run`.
- Internet access the first time you build (Gradle downloads libGDX, Ashley, LWJGL and, if needed, a JDK 17 toolchain via the foojay resolver).
- Nothing else to install; libGDX natives come in as Gradle dependencies.

**Commands** (run from the `goose-survivor/` directory; on Windows use `gradlew.bat`):

```sh
cd goose-survivor
./gradlew run          # launch the game (working directory is assets/)
./gradlew test         # unit + integration tests (JUnit 5)
./gradlew checkstyle   # Google Java Style checks (checkstyleMain + checkstyleTest)
./gradlew build        # compile, test and checkstyle: what CI runs, must pass before merging
./gradlew dist         # runnable JAR with all dependencies: build/libs/goose-survivor-<version>-all.jar
```

**Releasing the JAR:** run `./gradlew dist`, then create a GitHub Release and attach `build/libs/goose-survivor-<version>-all.jar`. The **Executable JAR** link at the top of this README always points to the latest release. Run it with `java -jar goose-survivor-<version>-all.jar` (Java 17+).

Checkstyle reports go to `goose-survivor/build/reports/checkstyle/` and test reports to `goose-survivor/build/reports/tests/test/index.html`.

**IDE (IntelliJ IDEA recommended):** open the `goose-survivor/` folder (not the repo root) as a Gradle project, and set *Settings → Build Tools → Gradle → Gradle JVM* to a JDK 17+. `.idea/` is git-ignored, so everyone keeps their own IDE setup. Use 2-space indentation (Google style). Loading `config/checkstyle/google_checks.xml` into the CheckStyle-IDEA plugin shows violations as you type.

**Running the game:** it always opens **fullscreen** at the monitor's native resolution and draws a fixed 1280x720 world scaled to fit (`FitViewport`). To quit, press Esc/P and choose *Quit to Desktop*. On macOS the build adds `-XstartOnFirstThread` automatically.

### 2. Repository layout

```
.
├── .github/workflows/
│   ├── gradle.yml                 CI: ./gradlew build on every push / PR to main (JDK 17, Ubuntu)
│   └── manage-blocked-label.yml   Adds/removes a "blocked" label on issues with open blocking dependencies
├── goose-survivor/                Gradle project (all the code lives here)
│   ├── build.gradle.kts           Dependencies: libGDX 1.14.2, Ashley 1.7.4, LWJGL 3.3.6, JUnit 6
│   ├── assets/                    Textures (PNG); on the classpath and used as the run directory
│   ├── config/checkstyle/         google_checks.xml (zero warnings allowed)
│   └── src/
│       ├── main/java/com/eng1c4t3/goosesurvivor/
│       │   ├── GooseSurvivor.java     Entry point (main); owns and switches screens
│       │   ├── StartScreen.java       Title menu (Play / Quit)
│       │   ├── GameScreen.java        One playthrough: loads textures, builds the level, renders, pause/end menus
│       │   ├── GameSession.java       Game logic with no graphics: Ashley engine, systems, 5 min timer, win/lose state
│       │   ├── GameWorld.java         Thin wrapper over the engine's entity list
│       │   ├── SessionState.java      RUNNING / PAUSED / WON / LOST
│       │   ├── SessionStats.java      Items used, events triggered/completed, kills per enemy type, score, fountain visits
│       │   ├── TextureManager.java    Texture file name constants, loading and disposal (libGDX AssetManager)
│       │   ├── WorldRenderer.java     Draws the background and every entity with Transform + Texture
│       │   ├── components/            Data-only Ashley components (each has a static MAPPER)
│       │   ├── systems/               Game logic (Ashley EntitySystem / IteratingSystem)
│       │   ├── entities/              EntityFactory (builds every entity type) and SpawnDrop
│       │   ├── items/                 ItemType enum, InventorySlot, ItemEffect and its implementations
│       │   ├── ui/                    InventoryHud (HP, timer, hotbar, [E] prompts), Menu, MenuInput, MenuRenderer
│       │   └── util/                  WeightedTable (weighted random picks for spawners)
│       └── test/java/...              Tests, mirroring the main package layout
└── misc/
    ├── gameplay.png               Screenshot at the top of this README
    ├── architecture/              Architecture diagrams shown in the Architecture section (PNG/JPG)
    ├── timelines/                 Gantt charts (PlantUML .puml source + rendered .svg) for each review date
    └── uml/                       PlantUML diagrams: entities, events, inventory, inventory_flow
```

To update a diagram, edit the `.puml` file, render it with [PlantUML](https://plantuml.com/) (the IntelliJ PlantUML plugin, or `java -jar plantuml.jar -tsvg file.puml`), and commit both the `.puml` and the `.svg`.

### 3. How the code fits together

**Screens and session.** `GooseSurvivor` opens `StartScreen`. *Play* calls `startNewGame()`, which creates a fresh `GameSession` and a `GameScreen` for it. Restart and Main Menu replace the screen the same way, and the old screen disposes its resources in `hide()`.

**`GameSession` has no graphics on purpose**, so tests can build a session without an OpenGL context. Anything that needs textures is plugged in afterwards by `GameScreen` through `configureShooting`, `configureDecoys`, `configureHiddenEvents` and `configureMouseAim`. Each `configure...` method can only be called once.

**Each frame** (`GameScreen.render`): handle pause/menu input → `session.update(delta)` (advances the timer, then `engine.update`) → draw the world → draw the HUD → draw a menu if paused or ended. Nothing updates while paused, won or lost.

**System order** (lower priority number runs first; set in the `GameSession` constructor and the `configure...` methods):

| Priority | System | Job |
| --- | --- | --- |
| 0 | `InputSystem` | Reads keyboard/mouse; sets player velocity and aim; creates interact and use-item event entities |
| 0 | `DecoySystem` | Places decoys (popcorn) |
| 1 | `InteractionSystem` | Handles item pickups from interact events |
| 2 | `InventorySystem` | Uses items from use-item events; ticks per-slot cooldowns |
| 3 | `SpeedBoostSystem` | Times out temporary speed boosts |
| 4 | `EnemySystem` | Geese chase the nearest decoy, or the player if there is none |
| 4 | `FoxSystem` | Fox runs from the player until petted, then follows; kept inside the world |
| 5 | `ShooterSystem` | Fires projectiles (Long Boi's shots and the launchers' food streams) |
| 6 | `MovementSystem` | Applies velocity |
| 7 | `CollisionSystem` | Resolves SOLID overlaps; reports TRIGGER overlaps to listeners |
| 8 | `ProjectileSystem` | Projectile hits, damage, slowing down and despawning |
| 8 | `HiddenEventSystem` | Statue, music student, vending machines, fox, fountain, door; spawn-on-approach; drops on death |
| 9 | `HealthSystem` | Damage cooldowns; removes dead entities |
| last | `EventCleanupSystem` | Deletes all event entities so each event is handled exactly once |

`RenderSystem` exists in `systems/` but is **not registered**; drawing is done by `WorldRenderer`, outside the engine.

**Events are entities.** Systems don't call each other. To raise an event, a system creates a short-lived entity holding only a `TriggerEventComponent` (interact with something) or a `UseItemEventComponent` (use a hotbar slot). Later systems read it, and `EventCleanupSystem` removes it at the end of the frame (see `misc/uml/events.puml`). Systems also react to entities being removed through Ashley `EntityListener`s: player death → lose, enemy death → kill stats, boss death → complete the event / drop an item.

**Win / lose.** Win by opening the door with the key (`HiddenEventSystem.setOnDoorOpened` → `GameSession.win`). Lose when the player's health reaches 0 or the 300 s timer (`GameSession.TIME_LIMIT`) runs out.

### 4. Current game content

The level is built in `GameScreen.populateWorld()`. The player starts with the Food Stream Launcher.

**Enemies** (stats are in `EntityFactory.statsFor`):

| Enemy | HP | Speed | Contact dmg | Score | Notes |
| --- | --- | --- | --- | --- | --- |
| Goose | 100 | 70 | 10 | 10 | Can come out of vending machines (10% chance) |
| Fast goose | 100 | 100 | 5 | 15 | Placed at the start |
| Tank goose | 300 | 40 | 15 | 30 | Placed at the start |
| Gradient goose | 150 | 80 | 10 | 20 | Appears when the player gets within 150 px of the door; drops the Key |
| Long Boi | 500 | 100 | 20 | 100 | Boss summoned from the statue; shoots every 20 s; beating it gives a permanent +10% speed |

**Items** (`items.ItemType`):

| Item | Uses | Cooldown | Effect | Where from |
| --- | --- | --- | --- | --- |
| Coffee | 1 | 0.1 s | Heals to full | Vending machines |
| Energy drink | 1 | 0.1 s | ×1.1 speed for 45 s | Snack vending machine |
| Popcorn | 1 | 0.1 s | Places a decoy that geese attack instead of the player | Snack vending machine |
| Trumpet | Unlimited | 90 s | Knocks geese within 120 px back 150 px | Talk to the music student |
| Food Stream Launcher | Unlimited | 1.5 s | 1 s stream of seeds towards the mouse | Starting weapon |
| Food Scatter Launcher | Unlimited | 1.5 s | Fan of 5 nuts towards the mouse | Pet the fox |
| Key | Unlimited | 0.1 s | Nothing when used; opens the door | Gradient goose drop |

Effects available in `items/`: `HealEffect`, `KnockbackEffect`, `SpeedBoostEffect` (doesn't stack; using it again resets the timer).

**Speed** is split in `VelocityComponent`: `maxSpeed` is the permanent base (change it for upgrades and rewards) and `speedMultiplier` is for temporary effects. Movement uses `effectiveMaxSpeed()`, so a boost ending never cancels a permanent upgrade.

**Hidden events** (`HiddenEventComponent.EventType`, handled in `HiddenEventSystem`): `LONG_BOI_STATUE`, `MUSIC_STUDENT`, `SPAWNER` (vending machines), `FOX`, `FOUNTAIN` (only records a visit in `SessionStats`) and `DOOR`.

### 5. Adding features

- **New component:** a data-only class in `components/` implementing `com.badlogic.ashley.core.Component`, with a `public static final ComponentMapper<...> MAPPER`.
- **New system:** extend `IteratingSystem` or `EntitySystem` in `systems/`, give it a `priority` that fits the table above, and register it in the `GameSession` constructor. If it needs textures, add a `configure...` method to `GameSession` and call it from the `GameScreen` constructor.
- **New entity type:** add a `create...` method to `entities.EntityFactory`, then place it in `GameScreen.populateWorld()`.
- **New enemy:** add a value to `EntityFactory.EnemyType` and a `case` in `statsFor` (the switch must cover every value).
- **New asset:** put the PNG in `goose-survivor/assets/`, add its name to `assets/assets.txt`, then add a constant to `TextureManager` and an `assetManager.load(...)` line in `loadTextures()`.
- **New item:** add a constant to `items.ItemType` (names, uses or `UNLIMITED_USES`, cooldown, texture, pickup size, `ItemEffect`). Write a new `ItemEffect` in `items/` if none of the existing ones fit. Put it in the world with `EntityFactory.createItemPickup`, a spawner's `WeightedTable`, or a `DropOnDeathComponent`.
- **New interactable / hidden event:** give the entity an `InteractableComponent` whose prompt is only the action (e.g. `"Open"`; the HUD shows `[E] Open`) and a `HiddenEventComponent` with a new `EventType`, then handle that type in the `switch` in `HiddenEventSystem.update`. Record progress in `SessionStats`.
- **New spawner / loot table:** `EntityFactory.createSpawner(..., WeightedTable<SpawnDrop>, uses)`; `vendingMachineDrops()` is an example.

### 6. Testing, style and CI

- Tests are in `goose-survivor/src/test/java`, mirroring the main packages. Game logic is tested without a window by building a `GameSession` or `Engine` directly. The `*IntegrationTest` classes run whole frames through a session.
- When adding a system or an effect, add tests next to the existing ones (e.g. `systems/InventorySystemTest`, `items/ItemEffectTest`).
- Checkstyle runs with `maxWarnings = 0`, so **any** style warning fails the build, including missing Javadoc. Run `./gradlew checkstyle` before pushing.
- `./gradlew build` passes on the current code (checked 15-11-2026 with JDK 17).

### 7. Git workflow

- `main` is the stable branch. CI (`.github/workflows/gradle.yml`) runs `./gradlew build` on every push and pull request to it.
- Work on a branch open a PR into `main`, and merge only when CI is green.
