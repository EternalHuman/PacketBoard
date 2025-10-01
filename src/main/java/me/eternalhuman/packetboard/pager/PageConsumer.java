package me.eternalhuman.packetboard.pager;

import me.eternalhuman.packetboard.Board;

@FunctionalInterface
public interface PageConsumer<R, P> {

    void accept(int page, int maxPage, Board<R, P> board);
}