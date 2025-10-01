package me.eternalhuman.packetboard.util.lang;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

    T get() throws E;
}
