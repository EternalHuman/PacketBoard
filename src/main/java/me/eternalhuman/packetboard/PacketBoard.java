package me.eternalhuman.packetboard;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.eternalhuman.packetboard.text.TextIterator;
import me.eternalhuman.packetboard.text.TextProvider;
import me.eternalhuman.packetboard.text.provider.AdventureTextProvider;
import me.eternalhuman.packetboard.text.provider.BungeeCordChatTextProvider;
import me.eternalhuman.packetboard.text.provider.MiniMessageTextProvider;
import me.eternalhuman.packetboard.text.provider.MiniPlaceholdersTextProvider;
import me.eternalhuman.packetboard.util.lang.ThrowingFunction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@UtilityClass
public class PacketBoard {

    public <R, P> Board.Builder<R, P> builder() {
        return Board.builder();
    }

    public <R, P> Board<R, P> newSidebar(
            @NonNull R title,
            @NonNull Plugin plugin,
            @NonNull TextProvider<R> textProvider,
            @NonNull ThrowingFunction<Player, P, Throwable> playerFunction
    ) {
        return Board.<R, P>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(textProvider)
                .playerFunction(playerFunction)
                .build();
    }

    public <R> Board<R, Player> newSidebar(
            @NonNull R title,
            @NonNull Plugin plugin,
            @NonNull TextProvider<R> textProvider
    ) {
        return Board.<R, Player>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(textProvider)
                .build();
    }

    public <R, P> Board<R, P> newSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin,
            @NonNull TextProvider<R> textProvider,
            @NonNull ThrowingFunction<Player, P, Throwable> playerFunction
    ) {
        return Board.<R, P>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(textProvider)
                .playerFunction(playerFunction)
                .build();
    }

    public <R> Board<R, Player> newSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin,
            @NonNull TextProvider<R> textProvider
    ) {
        return Board.<R, Player>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(textProvider)
                .build();
    }

    public <P> Board<String, P> newMiniMessageSidebar(
            @NonNull String title,
            @NonNull Plugin plugin,
            @NonNull MiniMessage miniMessage,
            @NonNull ThrowingFunction<Player, P, Throwable> playerFunction
    ) {
        return Board.<String, P>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new MiniMessageTextProvider(miniMessage))
                .playerFunction(playerFunction)
                .build();
    }

    public Board<String, Player> newMiniMessageSidebar(
            @NonNull String title,
            @NonNull Plugin plugin,
            @NonNull MiniMessage miniMessage
    ) {
        return Board.<String, Player>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new MiniMessageTextProvider(miniMessage))
                .build();
    }

    public <P> Board<String, P> newMiniplaceholdersSidebar(
            @NonNull String title,
            @NonNull Plugin plugin,
            @NonNull MiniMessage miniMessage,
            @NonNull ThrowingFunction<Player, P, Throwable> playerFunction
    ) {
        return Board.<String, P>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new MiniPlaceholdersTextProvider(miniMessage))
                .playerFunction(playerFunction)
                .build();
    }

    public Board<String, Player> newMiniplaceholdersSidebar(
            @NonNull String title,
            @NonNull Plugin plugin,
            @NonNull MiniMessage miniMessage
    ) {
        return Board.<String, Player>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new MiniPlaceholdersTextProvider(miniMessage))
                .build();
    }

    public <P> Board<String, P> newMiniplaceholdersSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin,
            @NonNull MiniMessage miniMessage,
            @NonNull ThrowingFunction<Player, P, Throwable> playerFunction
    ) {
        return Board.<String, P>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new MiniPlaceholdersTextProvider(miniMessage))
                .playerFunction(playerFunction)
                .build();
    }

    public Board<String, Player> newMiniplaceholdersSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin,
            @NonNull MiniMessage miniMessage
    ) {
        return Board.<String, Player>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new MiniPlaceholdersTextProvider(miniMessage))
                .build();
    }

    public <P> Board<String, P> newMiniMessageSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin,
            @NonNull MiniMessage miniMessage,
            @NonNull ThrowingFunction<Player, P, Throwable> playerFunction
    ) {
        return Board.<String, P>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new MiniMessageTextProvider(miniMessage))
                .playerFunction(playerFunction)
                .build();
    }

    public Board<String, Player> newMiniMessageSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin,
            @NonNull MiniMessage miniMessage
    ) {
        return Board.<String, Player>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new MiniMessageTextProvider(miniMessage))
                .build();
    }

    public <P> Board<String, P> newMiniMessageSidebar(
            @NonNull String title,
            @NonNull Plugin plugin,
            @NonNull ThrowingFunction<Player, P, Throwable> playerFunction
    ) {
        return Board.<String, P>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new MiniMessageTextProvider(MiniMessage.miniMessage()))
                .playerFunction(playerFunction)
                .build();
    }

    public Board<String, Player> newMiniMessageSidebar(
            @NonNull String title,
            @NonNull Plugin plugin
    ) {
        return Board.<String, Player>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new MiniMessageTextProvider(MiniMessage.miniMessage()))
                .build();
    }

    public <P> Board<String, P> newMiniMessageSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin,
            @NonNull ThrowingFunction<Player, P, Throwable> playerFunction
    ) {
        return Board.<String, P>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new MiniMessageTextProvider(MiniMessage.miniMessage()))
                .playerFunction(playerFunction)
                .build();
    }

    public Board<String, Player> newMiniMessageSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin
    ) {
        return Board.<String, Player>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new MiniMessageTextProvider(MiniMessage.miniMessage()))
                .build();
    }

    public <P> Board<Component, P> newAdventureSidebar(
            @NonNull Component title,
            @NonNull Plugin plugin,
            @NonNull ThrowingFunction<Player, P, Throwable> playerFunction
    ) {
        return Board.<Component, P>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new AdventureTextProvider())
                .playerFunction(playerFunction)
                .build();
    }

    public Board<Component, Player> newAdventureSidebar(
            @NonNull Component title,
            @NonNull Plugin plugin
    ) {
        return Board.<Component, Player>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new AdventureTextProvider())
                .build();
    }

    public <P> Board<Component, P> newAdventureSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin,
            @NonNull ThrowingFunction<Player, P, Throwable> playerFunction
    ) {
        return Board.<Component, P>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new AdventureTextProvider())
                .playerFunction(playerFunction)
                .build();
    }

    public Board<Component, Player> newAdventureSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin
    ) {
        return Board.<Component, Player>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new AdventureTextProvider())
                .build();
    }

    public <P> Board<BaseComponent[], P> newBungeeChatSidebar(
            @NonNull BaseComponent[] title,
            @NonNull Plugin plugin,
            @NonNull ThrowingFunction<Player, P, Throwable> playerFunction
    ) {
        return Board.<BaseComponent[], P>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new BungeeCordChatTextProvider())
                .playerFunction(playerFunction)
                .build();
    }

    public Board<BaseComponent[], Player> newBungeeChatSidebar(
            @NonNull BaseComponent[] title,
            @NonNull Plugin plugin
    ) {
        return Board.<BaseComponent[], Player>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new BungeeCordChatTextProvider())
                .build();
    }

    public <P> Board<BaseComponent[], P> newBungeeChatSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin,
            @NonNull ThrowingFunction<Player, P, Throwable> playerFunction
    ) {
        return Board.<BaseComponent[], P>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new BungeeCordChatTextProvider())
                .playerFunction(playerFunction)
                .build();
    }

    public Board<BaseComponent[], Player> newBungeeChatSidebar(
            @NonNull TextIterator title,
            @NonNull Plugin plugin
    ) {
        return Board.<BaseComponent[], Player>builder()
                .title(title)
                .plugin(plugin)
                .textProvider(new BungeeCordChatTextProvider())
                .build();
    }
}