package io.github.wasabithumb.playwright.plugin;

import io.github.wasabithumb.playwright.plugin.dispatch.ForwardingPluginDispatch;
import io.github.wasabithumb.playwright.plugin.dispatch.PluginDispatch;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PluginManager implements Iterable<Plugin> {

    private final Dispatch dispatch;
    private final Logger logger;
    private List<Plugin> registry;
    private boolean sealed;

    public PluginManager(@Nullable Logger logger) {
        this.dispatch = new Dispatch(this);
        this.logger = logger;
        this.registry = new LinkedList<>();
        this.sealed = false;
    }

    //

    @ApiStatus.Internal
    public synchronized void register(@NotNull Plugin plugin) {
        if (this.sealed) throw new IllegalStateException("Cannot register plugin (registry is sealed)");
        this.registry.add(plugin);
        plugin.onPluginRegistered(this);
    }

    @ApiStatus.Internal
    public synchronized void seal() {
        if (this.sealed) return;
        this.registry = Collections.unmodifiableList(this.registry);
        this.sealed = true;
    }

    @SuppressWarnings("unchecked")
    public <T extends Plugin> @NotNull T @NotNull [] findByType(@NotNull Class<T> clazz) {
        T[] ret = (T[]) Array.newInstance(clazz, 1);
        int capacity = 1;
        int len = 0;

        for (Plugin p : this) {
            if (!clazz.isInstance(p)) continue;
            if (len == capacity) {
                capacity <<= 1;
                T[] cpy = (T[]) Array.newInstance(clazz, capacity);
                System.arraycopy(ret, 0, cpy, 0, len);
                ret = cpy;
            }
            ret[len++] = clazz.cast(p);
        }

        if (len != capacity) {
            T[] shrink = (T[]) Array.newInstance(clazz, len);
            System.arraycopy(ret, 0, shrink, 0, len);
            ret = shrink;
        }

        return ret;
    }

    public <T extends Plugin> @NotNull T getByType(@NotNull Class<T> clazz) {
        T ret = null;

        for (Plugin p : this) {
            if (!clazz.isInstance(p)) continue;
            if (ret == null) {
                ret = clazz.cast(p);
            } else {
                throw new IllegalStateException("Multiple plugins registered for type " + clazz.getName());
            }
        }

        if (ret == null) {
            throw new IllegalStateException("No plugin registered for type " + clazz.getName());
        }

        return ret;
    }

    @Override
    public @NotNull Iterator<Plugin> iterator() {
        return this.registry.iterator();
    }

    @Contract(pure = true)
    public @NotNull PluginDispatch dispatch() {
        return this.dispatch;
    }

    //

    private static final class Dispatch extends ForwardingPluginDispatch {

        private final PluginManager parent;

        Dispatch(@NotNull PluginManager parent) {
            this.parent = parent;
        }

        //

        @Override
        protected @NotNull Iterator<? extends PluginDispatch> targets() {
            return this.parent.iterator();
        }

        @Override
        protected void onError(@NotNull String methodName, @NotNull PluginDispatch target, @NotNull Throwable error) {
            Logger logger = this.parent.logger;
            String message = "[" + target.getClass().getSimpleName() + "] Exception raised in " + methodName;

            if (logger != null) {
                logger.log(Level.SEVERE, message, error);
            } else {
                System.err.println(message);
                error.printStackTrace(System.err);
            }
        }

    }

}
