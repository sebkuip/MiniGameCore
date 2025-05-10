# MiniGameCore

**MiniGameCore** is a central plugin for managing minigame lobbies in Minecraft Paper Servers. It handles hosting, joining, starting, and stopping games – with animated scoreboards, statistics, and multiverse support.

---

## 🔧 Installation

1. Download the latest `.jar` from [Releases](https://github.com/YourGitUser/MiniGameCore/releases).
2. Place the file in the `plugins/` folder of your Minecraft server.
3. Ensure the following plugins are installed:
[LuckPerms](https://luckperms.net/) (for permissions management)
4. Restart the server once.


---
## 📜 Commands & Permissions

| Command | Description | Permission |
|----------------------------------------|------------------------------------------------|-------------------------------|
| `/mg host <game>` | Creates a new lobby | `mgcore.host` |
| `/mg join <lobby-id>` | Join the lobby | `mgcore.join` |
| `/mg leave` | Leave the lobby or game | `mgcore.leave` |
| `/mg start` | Start the game manually (if allowed) | `mgcore.start` |
| `/mg spectate <lobby-id\|player>` | Join a game as a spectator | `mgcore.spectate` |
| `/mg reload` | Reload the plugin | `mgcore.admin` |
| `/mg stop <lobby-id>` | Stop a specific game | `mgcore.admin` |
| `/mg stopall` | Stop all active games | `mgcore.admin` |
| `/mg ban <player>` | Ban the player from using /mg commands | `mgcore.admin` |

---

## ⚙️ Plugin-Configurations

Plugin Configurations are located in the folder: `./plugins/MiniGameCore`. The main configuration file is `config.yml`.

### config.yml
Example config:
```
available-games:
- Game1
- Game2
- Game3
banned-players:
- player1
- player2
- player3
```

### Loading Game-Worlds & Configuring them
The Folder for Gameworlds and configs is `./MiniGameCore`. Every Game World should be named like this: `<Game name>_world`. The world configs are named `config.yml` and should be located in the Game's world folder.

All available options:
| Key                   | Type                              | Description                                                                 | Needed                     |
|-----------------------|-----------------------------------|-----------------------------------------------------------------------------|----------------------------|
| `name`                | `String`                          | Display name of the game, e.g. in the scoreboard or at `/mg host`           | ✅ Yes                     |
| `maxPlayers`          | `Integer`                         | Maximum number of players for this game instance                            | ✅ Yes                     |
| `teams`               | `Integer`                         | Maximum number of teams                                                     | ❌ No (default: 0)         |
| `spawnPoints`         | `List<Location>`                  | Default spawn points for players without a team                             | ✅ Depends                 |
| `teamSpawnPoints`     | `Map<Team, List<Location>>`       | Spawn points per team (e.g. `0: [...]`, `1: [...]`)                         | ✅ Depends                 |
| `inventory`           | `List<ItemStack>`                 | Starting items at game start (e.g. `["WOODEN_SHOVEL"]`)                     | ❌ No                      |
| `allowed_break_blocks`| `List<String>`                    | Which blocks can be broken (e.g. `["SNOW_BLOCK"]`)                          | ❌ No                      |
| `respawnMode`         | `String`                          | Control of respawn behavior: `"true"` or `"false"`                          | ❌ No (default: false)     |
| `respawnDelay`        | `Integer`                         | Seconds delay until respawn (if enabled)                                    | ❌ No (default: 0)         |


Example config for an 8 player Spleef-Game:
```
game:
  name: Spleef
  maxPlayers: 8
  teams: 0
  spawnPoints:
    spawn1:
      x: -11
      y: 64
      z: 0
    spawn2:
      x: -7
      y: 64
      z: -7
    spawn3:
      x: 0
      y: 64
      z: -11
    spawn4:
      x: 7
      y: 64
      z: -7
    spawn5:
      x: 11
      y: 64
      z: 0
    spawn6:
      x: 7
      y: 64
      z: 7
    spawn7:
      x: 0
      y: 64
      z: 11
    spawn8:
      x: -7
      y: 64
      z: 7
  inventory:
    - "iron_shovel"
  allowed_break_blocks:
    - "snow_block"
  respawnMode: false
```
