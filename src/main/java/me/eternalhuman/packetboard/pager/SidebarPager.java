package me.eternalhuman.packetboard.pager;

import com.google.common.collect.Iterators;
import lombok.Getter;
import lombok.NonNull;
import me.eternalhuman.packetboard.Board;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.function.Consumer;

public class SidebarPager<R, P> {

    private final List<Board<R, P>> boards;
    private final Iterator<Board<R, P>> pageIterator;
    private final Set<UUID> viewers;
    private final BukkitTask switchTask;
    @Getter
    private Board<R, P> currentPage;

    /**
     * Creates a new sidebar pager.
     *
     * @param boards         - list of sidebars to use
     * @param switchDelayTicks - delay between page switches in ticks (if value is 0, pages will not be switched automatically)
     * @param plugin           - plugin instance
     */
    public SidebarPager(@NonNull List<Board<R, P>> boards, long switchDelayTicks, @NonNull Plugin plugin) {
        this.boards = boards;
        this.viewers = new HashSet<>();
        this.pageIterator = Iterators.cycle(boards);
        this.currentPage = pageIterator.next();

        if (switchDelayTicks > 0) {
            this.switchTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::switchPage, switchDelayTicks, switchDelayTicks);
        } else {
            this.switchTask = null;
        }
    }

    public void applyToAll(Consumer<Board<R, P>> consumer) {
        boards.forEach(consumer);
    }

    /**
     * Switches to the next page.
     * Note: this method is called automatically by the scheduler.
     */
    public void switchPage() {
        currentPage.removeViewers();

        currentPage = pageIterator.next();

        for (UUID viewer : viewers) {
            Player player = Bukkit.getPlayer(viewer);
            if (player != null) {
                currentPage.addViewer(player);
            }
        }
    }

    public Set<UUID> getViewers() {
        return Collections.unmodifiableSet(viewers);
    }

    public List<Board<R, P>> getSidebars() {
        return Collections.unmodifiableList(boards);
    }

    /**
     * Adds a page status line to all sidebars in pager.
     */
    public void addPageLine(PageConsumer<R, P> consumer) {
        int page = 1;
        int maxPage = boards.size();

        for (Board<R, P> board : boards) {
            consumer.accept(page, maxPage, board);
            page++;
        }
    }

    /**
     * Destroy all sidebars in pager.
     * Note: pager object will be unusable after this method call.
     */
    public void destroy() {
        if (switchTask != null) {
            switchTask.cancel();
        }
        for (Board<R, P> board : boards) {
            board.destroy();
        }
        boards.clear();
        viewers.clear();
    }

    /**
     * Start showing all sidebars in pager to the player.
     *
     * @param player - player to show sidebars to
     */
    public void show(@NonNull Player player) {
        synchronized (viewers) {
            viewers.add(player.getUniqueId());
        }
        currentPage.addViewer(player);
    }

    /**
     * Stop showing all sidebars in pager to the player.
     *
     * @param player - player to stop showing sidebars to
     */
    public void hide(@NonNull Player player) {
        synchronized (viewers) {
            viewers.remove(player.getUniqueId());
        }
        currentPage.removeViewer(player);
    }
}