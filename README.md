# PacketBoard

Современная библиотека для создания скорбордов в Minecraft через пакеты с поддержкой всех версий от 1.12.2 до 1.21.9.

## ✨ Преимущества

- **🔄 Полная совместимость версий** — работает на 1.12.2 - 1.21.9 без дополнительной настройки
- **📦 Packet-based подход** — использует пакеты напрямую, без проблем с версиями
- **☕ Java 11** — минимальные требования позволяют использовать на любой версии сервера
- **⚡ Поддержка Folia** — полная совместимость с многопоточным сервером
- **🎨 Множество text providers** — MiniMessage, Adventure, BungeeCordChat, MiniPlaceholders
- **✨ Встроенные анимации** — слайд-эффекты и кастомные анимации текста
- **🎯 Типобезопасность** — Generic типы для работы с кастомными объектами игроков
- **🔄 Динамическое обновление** — обновление линий по условиям и таймерам
- **📄 Pager система** — автоматическое переключение между несколькими скорбордами
- **🎮 Простой API** — интуитивно понятный fluent interface

## 📦 Установка

<details>
<summary><b>Maven</b></summary>

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
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
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.EternalHuman:PacketBoard:master-SNAPSHOT'
}
```

</details>

<details>
<summary><b>Gradle (Kotlin DSL)</b></summary>

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.EternalHuman:PacketBoard:master-SNAPSHOT")
}
```

</details>

## 🚀 Быстрый старт

### Базовый пример

```java
Board<String, Player> board = PacketBoard.newMiniMessageSidebar(
    "<gradient:#ff0000:#00ff00>Мой сервер</gradient>",
    plugin
);

board.addTextLine("<gold>Онлайн:");
board.addUpdatableLine(player -> 
    "<white>" + Bukkit.getOnlinePlayers().size()
);
board.addBlankLine();
board.addTextLine("<gray>play.myserver.com");

board.addViewer(player);
board.updateLinesPeriodically(0, 20); // обновление каждую секунду
```

## 📚 Примеры использования

<details>
<summary><b>С кастомным объектом игрока</b></summary>

```java
public class GamePlayer {
    private final Player bukkitPlayer;
    private int kills;
    private int wins;
    private int level;
}

Board<Component, GamePlayer> board = Board.<Component, GamePlayer>builder()
    .title(gamePlayer -> Component.text("Статистика"))
    .plugin(plugin)
    .textProvider(new AdventureTextProvider())
    .playerFunction(player -> GamePlayerManager.get(player))
    .build();

board.addUpdatableLine(gamePlayer -> 
    Component.text("Убийств: " + gamePlayer.getKills())
);

board.addUpdatableLine(gamePlayer -> 
    Component.text("Побед: " + gamePlayer.getWins())
);

board.addUpdatableLine(gamePlayer -> 
    Component.text("Уровень: " + gamePlayer.getLevel())
);

board.addViewer(player);
board.updateLinesPeriodically(0, 20);
```

</details>

<details>
<summary><b>Анимированный заголовок</b></summary>

```java
TextIterator titleAnimation = new TextSlideAnimation(
    "Добро пожаловать!",
    ChatColor.GOLD,
    TextSlideAnimation.SlideDirection.LEFT_TO_RIGHT,
    2
);

Board<String, Player> board = PacketBoard.newMiniMessageSidebar(
    titleAnimation,
    plugin
);
```

</details>

<details>
<summary><b>Условные линии</b></summary>

```java
// Показывать линию только если у игрока есть права
board.addConditionalLine(
    player -> "<green>VIP Бонусы активны",
    player -> player.hasPermission("server.vip")
);

// Показывать разные линии в зависимости от условий
BoardLine<String, Player> line = board.addUpdatableLine(player -> {
    if (player.getWorld().getName().equals("world_nether")) {
        return "<red>Вы в Аду!";
    } else if (player.getWorld().getName().equals("world_the_end")) {
        return "<light_purple>Вы в Крае!";
    } else {
        return "<green>Обычный мир";
    }
});
```

</details>

<details>
<summary><b>Динамическое управление линиями</b></summary>

```java
Board<String, Player> board = PacketBoard.newMiniMessageSidebar(
    "<gradient:#ff0000:#00ff00>PvP Арена</gradient>",
    plugin
);

BoardLine<String, Player> killsLine = board.addUpdatableLine(player -> 
    "<gold>Убийств: " + getKills(player)
);

BoardLine<String, Player> streakLine = board.addUpdatableLine(player -> 
    "<yellow>Серия: " + getStreak(player)
);

// Удаление линии
board.removeLine(killsLine);

// Обновление конкретной линии
board.updateLine(streakLine);

// Перемещение линии
board.shiftLine(streakLine, 0); // переместить в начало
```

</details>

<details>
<summary><b>SidebarPager - переключение скорбордов</b></summary>

```java
Board<String, Player> infoBoard = PacketBoard.newMiniMessageSidebar(
    "<aqua>Информация</aqua>",
    plugin
);
infoBoard.addTextLine("<gold>Режим: SkyWars");
infoBoard.addTextLine("<gray>Карта: Islands");

Board<String, Player> statsBoard = PacketBoard.newMiniMessageSidebar(
    "<green>Статистика</green>",
    plugin
);
statsBoard.addUpdatableLine(p -> "<white>Убийств: " + getKills(p));
statsBoard.addUpdatableLine(p -> "<white>Смертей: " + getDeaths(p));

// Создание pager с автопереключением каждые 5 секунд (100 тиков)
SidebarPager<String, Player> pager = new SidebarPager<>(
    Arrays.asList(infoBoard, statsBoard),
    100, // 5 секунд
    plugin
);

// Добавление индикатора страниц на все скорборды
pager.addPageLine((page, maxPage, board) -> {
    board.addTextLine("<gray>Страница " + page + "/" + maxPage);
});

// Применить настройку ко всем скорбордам
pager.applyToAll(board -> board.updateLinesPeriodically(0, 20));

pager.show(player);
```

</details>

<details>
<summary><b>С MiniPlaceholders</b></summary>

```java
Board<String, Player> board = PacketBoard.newMiniplaceholdersSidebar(
    "<gradient:#ff0000:#00ff00>Сервер</gradient>",
    plugin,
    MiniMessage.miniMessage()
);

board.addTextLine("<gold>Игрок: <white><player_name>");
board.addTextLine("<aqua>Баланс: <green>$<vault_eco_balance_fixed>");
board.addTextLine("<yellow>TPS: <server_tps>");

board.addViewer(player);
```

</details>

<details>
<summary><b>Динамический заголовок</b></summary>

```java
Board<String, GamePlayer> board = Board.<String, GamePlayer>builder()
    .title(gamePlayer -> {
        if (gamePlayer.isInCombat()) {
            return "<red><bold>БОЙ!</bold></red>";
        }
        return "<gradient:#ff0000:#00ff00>Мой сервер</gradient>";
    })
    .plugin(plugin)
    .textProvider(new MiniMessageTextProvider(MiniMessage.miniMessage()))
    .playerFunction(player -> GamePlayerManager.get(player))
    .build();
```

</details>

## 🎯 API Reference

<details>
<summary><b>PacketBoard (фабрика)</b></summary>

Утилитный класс для быстрого создания скорбордов:

- `newMiniMessageSidebar()` — с поддержкой MiniMessage
- `newMiniplaceholdersSidebar()` — с поддержкой MiniPlaceholders
- `newAdventureSidebar()` — с Adventure Components
- `newBungeeChatSidebar()` — с BungeeCord BaseComponent[]
- `newSidebar()` — с кастомным TextProvider

</details>

<details>
<summary><b>Board</b></summary>

**Управление просмотрщиками:**
- `addViewer(Player)` — добавить игрока
- `removeViewer(Player)` — удалить игрока
- `removeViewers()` — удалить всех игроков
- `getViewers()` — получить список просмотрщиков

**Управление линиями:**
- `addTextLine(String)` — статичная линия
- `addUpdatableLine(Function)` — динамическая линия
- `addConditionalLine(Function, Predicate)` — условная линия
- `addBlankLine()` — пустая линия
- `removeLine(BoardLine)` — удалить линию
- `updateLine(BoardLine)` — обновить линию
- `updateAllLines()` — обновить все линии
- `shiftLine(BoardLine, offset)` — переместить линию

**Управление заголовком:**
- `setTitle(R)` — статичный заголовок
- `setTitle(TextIterator)` — анимированный заголовок
- `setTitle(Function)` — динамический заголовок

**Обновление:**
- `updateLinesPeriodically(delay, period)` — периодическое обновление
- `bindWrappedTask(WrappedTask)` — привязать задачу к жизненному циклу

**Очистка:**
- `destroy()` — полная очистка и освобождение ресурсов

</details>

<details>
<summary><b>SidebarPager</b></summary>

- `show(Player)` — показать pager игроку
- `hide(Player)` — скрыть pager от игрока
- `switchPage()` — переключить на следующую страницу вручную
- `addPageLine(PageConsumer)` — добавить индикатор страниц
- `applyToAll(Consumer)` — применить действие ко всем скорбордам
- `destroy()` — очистить все скорборды

</details>

## 🔧 Поддерживаемые версии

- ✅ Bukkit/Spigot/Paper: **1.12.2 - 1.21.9**
- ✅ Folia: полная поддержка
- ✅ Java: **11+**

## 📝 Лицензия

**MIT License**

## 🤝 Поддержка

Нашли баг или есть предложение? Создайте Issue!
