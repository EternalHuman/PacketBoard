package me.eternalhuman.packetboard;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import io.netty.buffer.ByteBuf;
import lombok.*;
import me.eternalhuman.packetboard.protocol.ChannelInjector;
import me.eternalhuman.packetboard.protocol.ScoreNumberFormat;
import me.eternalhuman.packetboard.protocol.ScoreboardPackets;
import me.eternalhuman.packetboard.text.TextProvider;
import me.eternalhuman.packetboard.util.lang.ThrowingFunction;
import me.eternalhuman.packetboard.util.lang.ThrowingPredicate;
import me.eternalhuman.packetboard.util.lang.ThrowingSupplier;
import org.bukkit.entity.Player;

@Getter
@ToString
public class BoardLine<R, P> {

    private final String teamName;

    @Setter(AccessLevel.PACKAGE)
    private int score = -1;

    private final int index;
    private final boolean staticText;

    // for internal use
    WrappedTask updateTask;

    private ThrowingFunction<P, R, Throwable> updater;
    private ThrowingPredicate<P, Throwable> displayCondition;
    private final TextProvider<R> textProvider;
    private ScoreNumberFormat scoreNumberFormat;
    private Function<P, R> scoreNumberFormatter;

    BoardLine(@NonNull ThrowingFunction<P, R, Throwable> updater,
              @NonNull String teamName,
              boolean staticText,
              int index,
              @NonNull TextProvider<R> textProvider,
              @NonNull ThrowingPredicate<P, Throwable> displayCondition) {
        this.updater = updater;
        this.teamName = teamName;
        this.staticText = staticText;
        this.index = index;
        this.displayCondition = displayCondition;
        this.textProvider = textProvider;
    }

    public BoardLine<R, P> scoreNumberFormatBlank() {
        this.scoreNumberFormat = null;
        this.scoreNumberFormatter = null;
        return this;
    }

    public BoardLine<R, P> scoreNumberFormatFixed(@NonNull Function<P, R> scoreNumberFormatter) {
        this.scoreNumberFormat = ScoreNumberFormat.FIXED;
        this.scoreNumberFormatter = scoreNumberFormatter;
        return this;
    }

    public BoardLine<R, P> scoreNumberFormatStyled(@NonNull Function<P, R> scoreNumberFormatter) {
        this.scoreNumberFormat = ScoreNumberFormat.STYLED;
        this.scoreNumberFormatter = scoreNumberFormatter;
        return this;
    }

    public WrappedTask updatePeriodically(long delay, long period, @NonNull Board<R, P> board) {
        Preconditions.checkState(!isStaticText(), "Cannot set updater for static text line");

        if (updateTask != null) {
            Preconditions.checkState(updateTask.isCancelled(),
                    "Update task for line %s is already running. Cancel it first.", this);
            board.getFoliaLib().getScheduler().cancelTask(updateTask);
            board.tasks.remove(updateTask);
        }

        WrappedTask task = board.getFoliaLib().getScheduler().runTimerAsync(() -> board.updateLine(this), delay, period);

        this.updateTask = task;

        board.bindWrappedTask(task);

        return task;
    }

    /**
     * Sets visibility predicate for this line. Visibility predicate is a function that takes player
     * as an argument and returns boolean value. If predicate returns true, line will be visible for
     * this player, otherwise - invisible.
     *
     * @param displayCondition - visibility predicate
     */
    public void setDisplayCondition(@NonNull ThrowingPredicate<P, Throwable> displayCondition) {
        this.displayCondition = displayCondition;
    }

    /**
     * Sets updater for this line. Updater is a function that takes player as an argument and returns
     * text that will be displayed for this player.
     *
     * @param updater - updater function
     */
    public void setUpdater(@NonNull ThrowingFunction<P, R, Throwable> updater) {
        Preconditions.checkState(!isStaticText(), "Cannot set updater for static text line");
        this.updater = updater;
    }

    /**
     * Sets updater for this line without player parameter
     *
     * @param updater - updater function
     */
    public void setUpdater(@NonNull ThrowingSupplier<R, Throwable> updater) {
        Preconditions.checkState(!isStaticText(), "Cannot set updater for static text line");
        this.updater = player -> updater.get();
    }

    void updateTeam(@NonNull Player bukkitPlayer, @NonNull P customPlayer, @NonNull String objective) throws Throwable {
        boolean visible = displayCondition.test(customPlayer);

        if (!isStaticText() && visible) {
            R text = updater.apply(customPlayer);
            sendPacket(bukkitPlayer, ScoreboardPackets.createTeamPacket(
                    ScoreboardPackets.TEAM_UPDATED, index, teamName,
                    bukkitPlayer, text, textProvider));
        }

        if (!visible) {
            // if player doesn't meet display condition, remove score
            sendPacket(bukkitPlayer, ScoreboardPackets.createScorePacket(
                    bukkitPlayer, 1, objective, score, index, textProvider, scoreNumberFormat, null));
            return;
        }

        sendPacket(bukkitPlayer, ScoreboardPackets.createScorePacket(
                bukkitPlayer, 0, objective, score, index, textProvider, scoreNumberFormat,
                scoreNumberFormatter != null ? player -> scoreNumberFormatter.apply(customPlayer) : null));
    }

    void removeTeam(@NonNull Player bukkitPlayer, @NonNull String objective) {
        sendPacket(bukkitPlayer, ScoreboardPackets.createScorePacket(
                bukkitPlayer, 1, objective, score, index, textProvider, null, null));

        sendPacket(bukkitPlayer, ScoreboardPackets.createTeamPacket(ScoreboardPackets.TEAM_REMOVED, index, teamName,
                bukkitPlayer, null, textProvider));
    }

    void createTeam(@NonNull Player bukkitPlayer, @NonNull P customPlayer, @NonNull String objective) throws Throwable {
        boolean visible = displayCondition.test(customPlayer);

        R text = visible ? updater.apply(customPlayer) : textProvider.emptyMessage();

        sendPacket(bukkitPlayer, ScoreboardPackets.createTeamPacket(ScoreboardPackets.TEAM_CREATED, index, teamName,
                bukkitPlayer, text, textProvider));

        if (visible) {
            sendPacket(bukkitPlayer, ScoreboardPackets.createScorePacket(
                    bukkitPlayer, 0, objective, score, index, textProvider, scoreNumberFormat,
                    scoreNumberFormatter != null ? player -> scoreNumberFormatter.apply(customPlayer) : null));
        }
    }

    @SneakyThrows
    static void sendPacket(@NonNull Player player, @NonNull ByteBuf packet) {
        ChannelInjector.IMP.sendPacket(player, packet);
    }
}