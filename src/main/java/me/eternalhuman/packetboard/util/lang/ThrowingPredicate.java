package me.eternalhuman.packetboard.util.lang;

@FunctionalInterface
public interface ThrowingPredicate<T, E extends Throwable> {

    boolean test(T t) throws E;
}
