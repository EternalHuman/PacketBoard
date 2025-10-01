package me.eternalhuman.packetboard;

import com.google.common.base.Preconditions;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.eternalhuman.packetboard.text.TextIterator;
import me.eternalhuman.packetboard.text.TextProvider;
import me.eternalhuman.packetboard.text.provider.AdventureTextProvider;
import me.eternalhuman.packetboard.util.RandomString;
import me.eternalhuman.packetboard.util.lang.ThrowingConsumer;
import me.eternalhuman.packetboard.util.lang.ThrowingFunction;
import me.eternalhuman.packetboard.util.lang.ThrowingPredicate;
import me.eternalhuman.packetboard.util.lang.ThrowingSupplier;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

@FieldDefaults(level = AccessLevel.PACKAGE)
public class Board<R, P> {

    private static final String OBJECTIVE_PREFIX = "PB-";
    private static final int MAX_LINES_COUNT = 15;

    private final Set<UUID> viewers = Collections.synchronizedSet(new HashSet<>());
    private final List<BoardLine<R, P>> lines = new ArrayList<>();
    @Getter
    private final ScoreboardObjective<R> objective;

    private TextIterator titleText;
    private WrappedTask titleUpdater;
    private ThrowingFunction<P, R, Throwable> titleFunction;

    final Set<WrappedTask> tasks = new HashSet<>();
    final TextProvider<R> textProvider;
    final ThrowingFunction<Player, P, Throwable> playerFunction;

    @Getter
    private final Plugin plugin;

    @Getter
    private final FoliaLib foliaLib;

    Board(@NonNull R title, @NonNull Plugin plugin, @NonNull TextProvider<R> textProvider,
          ThrowingFunction<Player, P, Throwable> playerFunction) {
        this.plugin = plugin;
        this.foliaLib = new FoliaLib(plugin);
        this.textProvider = textProvider;
        this.playerFunction = playerFunction;
        this.objective = new ScoreboardObjective<>(OBJECTIVE_PREFIX + RandomString.generate(3), title, textProvider);
    }

    Board(@NonNull TextIterator titleIterator, @NonNull Plugin plugin, @NonNull TextProvider<R> textProvider,
          ThrowingFunction<Player, P, Throwable> playerFunction) {
        this.plugin = plugin;
        this.foliaLib = new FoliaLib(plugin);
        this.textProvider = textProvider;
        this.playerFunction = playerFunction;

        this.objective = new ScoreboardObjective<>(
                OBJECTIVE_PREFIX + RandomString.generate(3),
                textProvider.fromLegacyMessage(titleIterator.next()),
                textProvider);

        setTitleIter(titleIterator);
    }

    Board(@NonNull ThrowingFunction<P, R, Throwable> titleFunction, @NonNull Plugin plugin,
          @NonNull TextProvider<R> textProvider, ThrowingFunction<Player, P, Throwable> playerFunction) {
        this.plugin = plugin;
        this.foliaLib = new FoliaLib(plugin);
        this.textProvider = textProvider;
        this.playerFunction = playerFunction;
        this.titleFunction = titleFunction;
        this.objective = new ScoreboardObjective<>(
                OBJECTIVE_PREFIX + RandomString.generate(3),
                null,
                textProvider);
    }

    public static void main(String[] args) { //EXAMPLE
        //Test

        Board<Component, GamePlayer> board = Board.<Component, GamePlayer>builder()
                .title(gamePlayer -> Component.text("Title"))
                .plugin(null)
                .textProvider(new AdventureTextProvider())
                .build();

        board.addUpdatableLine(gamePlayer -> {
            return Component.text("Kills: " + gamePlayer.kills);
        });

        board.addUpdatableLine(gamePlayer -> {
            return Component.text("Wins: " + gamePlayer.wins);
        });

    }

    private static class GamePlayer {
        private int kills, wins;
        private Player bukkitPlayer;
    }

    private static class GamePlayerManager {
        private static final Map<UUID, GamePlayer> gamePlayerMap = Map.of();

        public static GamePlayer getGamePlayer(Player player) {
            return gamePlayerMap.get(player.getUniqueId());
        }
    }

    public static <R, P> Builder<R, P> builder() {
        return new Builder<>();
    }

    public ThrowingFunction<Player, R, Throwable> toLineUpdater(@NonNull TextIterator iterator) {
        return player -> textProvider.fromLegacyMessage(iterator.next());
    }

    public void setTitle(@NonNull R title) {
        cancelTitleUpdater();
        this.titleFunction = null;

        objective.setDisplayName(title);
        broadcast(objective::updateValue);
    }

    public void setTitle(@NonNull TextIterator iterator) {
        setTitleIter(iterator);
    }

    public void setTitle(@NonNull ThrowingFunction<P, R, Throwable> titleFunction) {
        cancelTitleUpdater();
        this.titleFunction = titleFunction;

        broadcastWithConversion((bukkitPlayer, customPlayer) -> {
            R title = titleFunction.apply(customPlayer);
            objective.updateValue(bukkitPlayer, title);
        });
    }

    private void cancelTitleUpdater() {
        if (titleUpdater != null) {
            titleUpdater.cancel();
            titleUpdater = null;
        }

        this.titleText = null;
    }

    private void setTitleIter(@NonNull TextIterator iterator) {
        cancelTitleUpdater();
        this.titleFunction = null;

        this.titleText = iterator;
        this.titleUpdater = foliaLib.getScheduler().runTimerAsync(() -> {
            String next = titleText.next();

            objective.setDisplayName(textProvider.fromLegacyMessage(next));
            broadcast(objective::updateValue);
        }, 0, 1);
    }

    public void shiftLine(BoardLine<R, P> line, int offset) {
        synchronized (lines) {
            lines.remove(line);
            lines.add(offset, line);
        }

        updateAllLines();
    }

    public WrappedTask bindWrappedTask(@NonNull WrappedTask task) {
        this.tasks.add(task);
        return task;
    }

    public WrappedTask updateLinesPeriodically(long delay, long period) {
        return updateLinesPeriodically(delay, period, true);
    }

    public WrappedTask updateLinesPeriodically(long delay, long period, boolean async) {
        return async ?
                bindWrappedTask(foliaLib.getScheduler()
                        .runTimerAsync(this::updateAllLines, delay, period)) :
                bindWrappedTask(foliaLib.getScheduler()
                        .runTimer(this::updateAllLines, delay, period));
    }

    public BoardLine<R, P> addConditionalLine(@NonNull ThrowingFunction<P, R, Throwable> updater,
                                              @NonNull ThrowingPredicate<P, Throwable> condition) {
        return addLine(updater, false, condition);
    }

    public BoardLine<R, P> addTextLine(@NonNull String text) {
        return addLine(textProvider.fromLegacyMessage(text));
    }

    public BoardLine<R, P> addUpdatableLine(@NonNull ThrowingFunction<P, R, Throwable> updater) {
        return addLine(updater, false, x -> true);
    }

    public BoardLine<R, P> addUpdatableLine(ThrowingSupplier<R, Throwable> updater) {
        return addUpdatableLine(player -> updater.get());
    }

    public BoardLine<R, P> addLine(@NonNull R text) {
        return addLine(x -> text, true, x -> true);
    }

    public BoardLine<R, P> addBlankLine() {
        return addTextLine("");
    }

    private BoardLine<R, P> addLine(@NonNull ThrowingFunction<P, R, Throwable> updater, boolean staticText,
                                    @NonNull ThrowingPredicate<P, Throwable> predicate) {
        synchronized (lines) {
            Preconditions.checkArgument(
                    lines.size() <= MAX_LINES_COUNT, "Cannot add more than %s lines to a sidebar", MAX_LINES_COUNT);

            BoardLine<R, P> line = new BoardLine<>(
                    updater, objective.getName() + lines.size(),
                    staticText, lines.size(), textProvider, predicate);

            lines.add(line);
            return line;
        }
    }

    public void removeLine(@NonNull BoardLine<R, P> line) {
        synchronized (lines) {
            if (lines.remove(line) && line.getScore() != -1) {
                broadcast(p -> line.removeTeam(p, objective.getName()));
                updateAllLines();
            }
        }
    }

    public Optional<BoardLine<R, P>> maxLine() {
        synchronized (lines) {
            return lines.stream()
                    .filter(line -> line.getScore() != -1)
                    .max(Comparator.comparingInt(BoardLine::getScore));
        }
    }

    public Optional<BoardLine<R, P>> minLine() {
        synchronized (lines) {
            return lines.stream()
                    .filter(line -> line.getScore() != -1)
                    .min(Comparator.comparingInt(BoardLine::getScore));
        }
    }

    public void updateLine(@NonNull BoardLine<R, P> line) {
        synchronized (lines) {
            Preconditions.checkArgument(lines.contains(line), "Line %s is not a part of this sidebar", line);

            broadcastWithConversion((bukkitPlayer, customPlayer) ->
                    line.updateTeam(bukkitPlayer, customPlayer, objective.getName()));
        }
    }

    public void updateAllLines() {
        synchronized (lines) {
            int index = lines.size();

            for (BoardLine<R, P> line : lines) {
                if (line.getScore() == -1) {
                    line.setScore(index--);
                    broadcastWithConversion((bukkitPlayer, customPlayer) ->
                            line.createTeam(bukkitPlayer, customPlayer, objective.getName()));
                    continue;
                }

                if (line.updateTask != null && !line.updateTask.isCancelled()) {
                    continue;
                }

                line.setScore(index--);

                broadcastWithConversion((bukkitPlayer, customPlayer) ->
                        line.updateTeam(bukkitPlayer, customPlayer, objective.getName()));
            }
        }
    }

    public void removeViewers() {
        synchronized (viewers) {
            for (Iterator<UUID> iterator = viewers.iterator(); iterator.hasNext(); ) {
                UUID uuid = iterator.next();
                Player player = Bukkit.getPlayer(uuid);

                if (player != null) {
                    removeViewer0(player);
                }

                iterator.remove();
            }
        }
    }

    public void destroy() {
        cancelTitleUpdater();

        for (WrappedTask task : tasks) {
            foliaLib.getScheduler().cancelTask(task);
        }

        removeViewers();

        synchronized (lines) {
            lines.clear();
        }

        tasks.clear();
    }

    @SneakyThrows
    public void addViewer(@NonNull Player player) {
        if (!viewers.contains(player.getUniqueId())) {
            P customPlayer = convertPlayer(player);

            if (titleFunction != null) {
                R title = titleFunction.apply(customPlayer);
                objective.create(player, title);
            } else {
                objective.create(player);
            }

            synchronized (lines) {
                for (BoardLine<R, P> line : lines) {
                    line.createTeam(player, customPlayer, objective.getName());
                }
            }

            objective.display(player);

            viewers.add(player.getUniqueId());
        }
    }

    public void removeViewer(@NonNull Player player) {
        synchronized (viewers) {
            if (viewers.remove(player.getUniqueId())) {
                removeViewer0(player);
            }
        }
    }

    private void removeViewer0(@NonNull Player player) {
        lines.forEach(line -> line.removeTeam(player, objective.getName()));
        objective.remove(player);
    }

    public Set<UUID> getViewers() {
        return Collections.unmodifiableSet(viewers);
    }

    public List<BoardLine<R, P>> getLines() {
        synchronized (lines) {
            return Collections.unmodifiableList(lines);
        }
    }

    @SuppressWarnings("unchecked")
    private P convertPlayer(@NonNull Player player) throws Throwable {
        return playerFunction != null ? playerFunction.apply(player) : (P) player;
    }

    private void broadcast(@NonNull ThrowingConsumer<Player, Throwable> consumer) {
        synchronized (viewers) {
            viewers.removeIf(uuid -> Bukkit.getPlayer(uuid) == null);

            for (UUID id : viewers) {
                Player player = Bukkit.getPlayer(id);
                if (player == null) {
                    continue;
                }

                try {
                    consumer.accept(player);
                } catch (Throwable e) {
                    throw new RuntimeException("An error occurred while updating sidebar for player: " + player.getName(),
                            e);
                }
            }
        }
    }

    private void broadcastWithConversion(@NonNull BiThrowingConsumer<Player, P> consumer) {
        synchronized (viewers) {
            viewers.removeIf(uuid -> Bukkit.getPlayer(uuid) == null);

            for (UUID id : viewers) {
                Player bukkitPlayer = Bukkit.getPlayer(id);
                if (bukkitPlayer == null) {
                    continue;
                }

                try {
                    P customPlayer = convertPlayer(bukkitPlayer);
                    consumer.accept(bukkitPlayer, customPlayer);
                } catch (Throwable e) {
                    throw new RuntimeException("An error occurred while updating sidebar for player: " + bukkitPlayer.getName(),
                            e);
                }
            }
        }
    }

    @FunctionalInterface
    private interface BiThrowingConsumer<T, U> {
        void accept(T t, U u) throws Throwable;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Builder<R, P> {
        R title;
        TextIterator titleIterator;
        ThrowingFunction<P, R, Throwable> titleFunction;
        Plugin plugin;
        TextProvider<R> textProvider;
        ThrowingFunction<Player, P, Throwable> playerFunction;

        public Builder<R, P> title(@NonNull R title) {
            this.title = title;
            this.titleIterator = null;
            this.titleFunction = null;
            return this;
        }

        public Builder<R, P> title(@NonNull TextIterator titleIterator) {
            this.titleIterator = titleIterator;
            this.title = null;
            this.titleFunction = null;
            return this;
        }

        public Builder<R, P> title(@NonNull ThrowingFunction<P, R, Throwable> titleFunction) {
            this.titleFunction = titleFunction;
            this.title = null;
            this.titleIterator = null;
            return this;
        }

        public Builder<R, P> plugin(@NonNull Plugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder<R, P> textProvider(@NonNull TextProvider<R> textProvider) {
            this.textProvider = textProvider;
            return this;
        }

        public Builder<R, P> playerFunction(@NonNull ThrowingFunction<Player, P, Throwable> playerFunction) {
            this.playerFunction = playerFunction;
            return this;
        }

        public Board<R, P> build() {
            Preconditions.checkNotNull(plugin, "Plugin cannot be null");
            Preconditions.checkNotNull(textProvider, "TextProvider cannot be null");
            Preconditions.checkArgument(title != null || titleIterator != null || titleFunction != null,
                    "Title, titleIterator, or titleFunction must be set");

            if (titleFunction != null) {
                return new Board<>(titleFunction, plugin, textProvider, playerFunction);
            }

            return titleIterator != null
                    ? new Board<>(titleIterator, plugin, textProvider, playerFunction)
                    : new Board<>(title, plugin, textProvider, playerFunction);
        }
    }
}