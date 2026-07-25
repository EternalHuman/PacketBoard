# PacketBoard

A modern library for creating scoreboards in Minecraft using packets with support for all versions from 1.12.2 to 26.2.

## ✨ Advantages

- **🔄 Full version compatibility** — works on 1.12.2 - 26.2 without additional configuration
- **📦 Packet-based approach** — uses packets directly, without version compatibility issues
- **☕ Java 17** — minimal requirements allow using the library on any server version
- **⚡ Folia support** — full compatibility with multi-threaded server
- **🎨 Multiple text providers** — MiniMessage, Adventure, BungeeCordChat, MiniPlaceholders
- **✨ Built-in animations** — slide effects and custom text animations
- **🎯 Type safety** — Generic types for working with custom player objects
- **🔄 Dynamic updates** — line updates based on conditions and timers
- **📄 Pager system** — automatic switching between multiple scoreboards
- **🎮 Simple API** — intuitive fluent interface

## 📦 Installation

> `tcoded-repo` and `viaversion-repo` are required for FoliaLib and ViaVersion NBT dependencies.

<details>
<summary><b>Maven</b></summary>

```xml
<repositories>
    <repository>
        <id>tcoded-repo</id>
        <name>tcoded-repo</name>
        <url>https://repo.tcoded.com/releases</url>
    </repository>
    <repository>
        <id>viaversion-repo</id>
        <name>viaversion-repo</name>
        <url>https://repo.viaversion.com</url>
    </repository>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.EternalHuman</groupId>
        <artifactId>PacketBoard</artifactId>
        <version>master-SNAPSHOT</version>
    </dependency>
</dependencies>
```

</details>

<details>
<summary><b>Gradle (Groovy)</b></summary>

```groovy
repositories {
    maven {
        name = 'tcoded-repo'
        url = uri('https://repo.tcoded.com/releases')
    }
    maven {
        name = 'viaversion-repo'
        url = uri('https://repo.viaversion.com')
    }
    maven { url 'https://jitpack.io' }
}

dependencies {
     implementation('com.github.EternalHuman:PacketBoard:master-SNAPSHOT') {
        changing = true
    }
}
```

</details>

<details>
<summary><b>Gradle (Kotlin DSL)</b></summary>

```kotlin
repositories {
    maven {
        name = "tcoded-repo"
        url = uri("https://repo.tcoded.com/releases")
    }
    maven {
        name = "viaversion-repo"
        url = uri("https://repo.viaversion.com")
    }
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.EternalHuman:PacketBoard:master-SNAPSHOT") {
        isChanging = true
    }
}
```

</details>

## 🚀 Quick Start

### Basic Example

```java
Board<String, Player> board = PacketBoard.newMiniMessagePacketBoard(
    "<gradient:#ff0000:#00ff00>My Server</gradient>",
    plugin
);

board.addTextLine("<gold>Online:");
board.addUpdatableLine(player -> 
    "<white>" + Bukkit.getOnlinePlayers().size()
);
board.addBlankLine();
board.addTextLine("<gray>play.myserver.com");

board.addViewer(player);
board.updateLinesPeriodically(0, 20); // update every second
```

## 📚 Usage Examples

<details>
<summary><b>With Custom Player Object</b></summary>

```java
public class GamePlayer {
    private final Player bukkitPlayer;
    private int kills;
    private int wins;
    private int level;
}

Board<Component, GamePlayer> board = Board.<Component, GamePlayer>builder()
    .title(gamePlayer -> Component.text("Statistics"))
    .plugin(plugin)
    .textProvider(new AdventureTextProvider())
    .playerFunction(player -> GamePlayerManager.get(player))
    .build();

board.addUpdatableLine(gamePlayer -> 
    Component.text("Kills: " + gamePlayer.getKills())
);

board.addUpdatableLine(gamePlayer -> 
    Component.text("Wins: " + gamePlayer.getWins())
);

board.addUpdatableLine(gamePlayer -> 
    Component.text("Level: " + gamePlayer.getLevel())
);

board.addViewer(player);
board.updateLinesPeriodically(0, 20);
```

</details>

<details>
<summary><b>Animated Title</b></summary>

```java
TextIterator titleAnimation = new TextSlideAnimation(
    "Welcome!",
    ChatColor.GOLD,
    TextSlideAnimation.SlideDirection.LEFT_TO_RIGHT,
    2
);

Board<String, Player> board = PacketBoard.newMiniMessagePacketBoard(
    titleAnimation,
    plugin
);
```

</details>

<details>
<summary><b>Conditional Lines</b></summary>

```java
// Show line only if player has permission
board.addConditionalLine(
    player -> "<green>VIP Bonuses Active",
    player -> player.hasPermission("server.vip")
);

// Show different lines depending on conditions
BoardLine<String, Player> line = board.addUpdatableLine(player -> {
    if (player.getWorld().getName().equals("world_nether")) {
        return "<red>You're in the Nether!";
    } else if (player.getWorld().getName().equals("world_the_end")) {
        return "<light_purple>You're in the End!";
    } else {
        return "<green>Normal World";
    }
});
```

</details>

<details>
<summary><b>Dynamic Line Management</b></summary>

```java
Board<String, Player> board = PacketBoard.newMiniMessagePacketBoard(
    "<gradient:#ff0000:#00ff00>PvP Arena</gradient>",
    plugin
);

BoardLine<String, Player> killsLine = board.addUpdatableLine(player -> 
    "<gold>Kills: " + getKills(player)
);

BoardLine<String, Player> streakLine = board.addUpdatableLine(player -> 
    "<yellow>Streak: " + getStreak(player)
);

// Remove line
board.removeLine(killsLine);

// Update specific line
board.updateLine(streakLine);

// Move line
board.shiftLine(streakLine, 0); // move to beginning
```

</details>

<details>
<summary><b>BoardPager - Switching Scoreboards</b></summary>

```java
Board<String, Player> infoBoard = PacketBoard.newMiniMessagePacketBoard(
    "<aqua>Information</aqua>",
    plugin
);
infoBoard.addTextLine("<gold>Mode: SkyWars");
infoBoard.addTextLine("<gray>Map: Islands");

Board<String, Player> statsBoard = PacketBoard.newMiniMessagePacketBoard(
    "<green>Statistics</green>",
    plugin
);
statsBoard.addUpdatableLine(p -> "<white>Kills: " + getKills(p));
statsBoard.addUpdatableLine(p -> "<white>Deaths: " + getDeaths(p));

// Create pager with auto-switch every 5 seconds (100 ticks)
BoardPager<String, Player> pager = new BoardPager<>(
    Arrays.asList(infoBoard, statsBoard),
    100, // 5 seconds
    plugin
);

// Add page indicator to all scoreboards
pager.addPageLine((page, maxPage, board) -> {
    board.addTextLine("<gray>Page " + page + "/" + maxPage);
});

// Apply setting to all scoreboards
pager.applyToAll(board -> board.updateLinesPeriodically(0, 20));

pager.show(player);
```

</details>

<details>
<summary><b>With MiniPlaceholders</b></summary>

```java
Board<String, Player> board = PacketBoard.newMiniplaceholdersPacketBoard(
    "<gradient:#ff0000:#00ff00>Server</gradient>",
    plugin,
    MiniMessage.miniMessage()
);

board.addTextLine("<gold>Player: <white><player_name>");
board.addTextLine("<aqua>Balance: <green>$<vault_eco_balance_fixed>");
board.addTextLine("<yellow>TPS: <server_tps>");

board.addViewer(player);
```

</details>

<details>
<summary><b>Dynamic Title</b></summary>

```java
Board<String, GamePlayer> board = Board.<String, GamePlayer>builder()
    .title(gamePlayer -> {
        if (gamePlayer.isInCombat()) {
            return "<red><bold>COMBAT!</bold></red>";
        }
        return "<gradient:#ff0000:#00ff00>My Server</gradient>";
    })
    .plugin(plugin)
    .textProvider(new MiniMessageTextProvider(MiniMessage.miniMessage()))
    .playerFunction(player -> GamePlayerManager.get(player))
    .build();
```

</details>

## 🎯 API Reference

<details>
<summary><b>PacketBoard (factory)</b></summary>

Utility class for quick scoreboard creation:

- `newMiniMessagePacketBoard()` — with MiniMessage support
- `newMiniplaceholdersPacketBoard()` — with MiniPlaceholders support
- `newAdventurePacketBoard()` — with Adventure Components
- `newBungeeChatPacketBoard()` — with BungeeCord BaseComponent[]
- `newPacketBoard()` — with custom TextProvider

</details>

<details>
<summary><b>Board</b></summary>

**Viewer Management:**
- `addViewer(Player)` — add player
- `removeViewer(Player)` — remove player
- `removeViewers()` — remove all players
- `getViewers()` — get viewer list

**Line Management:**
- `addTextLine(String)` — static line
- `addUpdatableLine(Function)` — dynamic line
- `addConditionalLine(Function, Predicate)` — conditional line
- `addBlankLine()` — blank line
- `removeLine(BoardLine)` — remove line
- `updateLine(BoardLine)` — update line
- `updateAllLines()` — update all lines
- `shiftLine(BoardLine, offset)` — move line

**Title Management:**
- `setTitle(R)` — static title
- `setTitle(TextIterator)` — animated title
- `setTitle(Function)` — dynamic title

**Updates:**
- `updateLinesPeriodically(delay, period)` — periodic updates
- `bindWrappedTask(WrappedTask)` — bind task to lifecycle

**Cleanup:**
- `destroy()` — full cleanup and resource release

</details>

<details>
<summary><b>BoardPager</b></summary>

- `show(Player)` — show pager to player
- `hide(Player)` — hide pager from player
- `switchPage()` — manually switch to next page
- `addPageLine(PageConsumer)` — add page indicator
- `applyToAll(Consumer)` — apply action to all scoreboards
- `destroy()` — cleanup all scoreboards

</details>

## 🔧 Supported Versions

- ✅ Bukkit/Spigot/Paper: **1.12.2 - 26.2**
- ✅ Folia/Adventure: full support
- ✅ Java: **17+**

## 📝 License

**MIT License**

## 🤝 Support

Found a bug or have a suggestion? Create an Issue!
