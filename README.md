# Goose Survivor

A 2D survival game built with [libGDX](https://libgdx.com/) using the [Ashley](https://github.com/libgdx/ashley) Entity-Component-System (ECS) architecture.

## Getting started

Requires **JDK 17** (Gradle will download it via toolchains if missing). All commands are run from the `goose-survivor/` directory:

```sh
cd goose-survivor
./gradlew run          # launch the game
./gradlew test         # run unit tests
./gradlew checkstyle   # run style checks (Google Java Style)
./gradlew build        # compile, test and check (what CI runs)
```

On Windows use `gradlew.bat` instead of `./gradlew`.

## Project structure

```
.
├── .github/workflows/          CI (build + test + checkstyle on push/PR to main)
├── goose-survivor/             Gradle project
│   ├── assets/                 Game assets (textures etc.), on the classpath and used as the run directory
│   ├── config/checkstyle/      Checkstyle rules
│   └── src/
│       ├── main/java/com/eng1c4t3/goosesurvivor/
│       │   ├── GooseSurvivor.java   Entry point and game loop
│       │   ├── components/          Data-only components implementing Ashley Component
│       │   ├── systems/             Logic systems extending Ashley IteratingSystem / EntitySystem
│       │   ├── entities/            EntityFactory for building preconfigured entities
│       │   ├── items/               Item definitions (ItemType), effects and inventory slots
│       │   ├── ui/                  Temporary HUD (health, inventory hotbar, pickup labels)
│       │   └── TextureManager.java  TextureManager for loading and disposing assets
│       └── test/java/...            Unit tests, mirroring the main package layout
└── misc/                       Project timelines and UML diagrams (PlantUML)
```

### Adding features

- **New component:** add a data-only class in `components/` implementing `com.badlogic.ashley.core.Component`.
- **New system:** extend `IteratingSystem` or `EntitySystem` in `systems/` and register it in `GooseSurvivor#registerSystems`.
- **New entity type:** add a `create...` method to `entities.EntityFactory`.
- **New asset:** put the file in `assets/` and load it in `assets.TextureManager`.
- **New interactable:** add an `InteractableComponent` whose `promptText` is just the action (e.g. `"Open"`); the HUD adds the key, showing `[E] Open` when the player is in range.
- **New item:** add a constant to `items.ItemType` with its uses (a positive number or `ItemType.UNLIMITED_USES`), cooldown, texture and `ItemEffect`. Add a new `ItemEffect` class in `items/` if none of the existing ones fit. Place it in the world with `EntityFactory.createItemPickup`.

## Controls

| Key | Action |
| --- | --- |
| WASD / arrow keys | Move |
| E | Interact / pick up item (a `[E] ...` popup appears above whatever is in range) |
| 1-9 | Use the item in that hotbar slot |
| P | Pause / resume |

## Inventory

The player has a 5-slot hotbar. Every item can be used, and has either a fixed number of uses (removed from the hotbar when used up) or unlimited uses. Every slot has its own cooldown, so two copies of the same item cool down independently. See [`misc/uml/inventory.puml`](misc/uml/inventory.puml) and [`misc/uml/inventory_flow.puml`](misc/uml/inventory_flow.puml).

| Item | Uses | Cooldown | Effect |
| --- | --- | --- | --- |
| Coffee | 1 | 0.1s | Heals to full |
| Trumpet | Unlimited | 90s | No damage; knocks geese within 120 units 150 units away |

The player starts with an empty inventory. The demo stage has a Coffee and a Trumpet to pick up; Coffee is the only way to heal.

Available item effects (in `items/`): `HealEffect(amount)`, `KnockbackEffect(radius, distance)`, `AreaDamageEffect(radius, damage)` and `SpeedBoostEffect(multiplier, seconds)`. Speed boosts don't stack; using one while boosted refreshes the timer.

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

<details open>
  <summary><b>28-10-2026</b></summary>
  <br>
  <img src="misc/timelines/28_10_26_timeline.svg" width="100%">
</details>
