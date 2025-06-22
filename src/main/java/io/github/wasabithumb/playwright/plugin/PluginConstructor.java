package io.github.wasabithumb.playwright.plugin;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

@FunctionalInterface
public interface PluginConstructor<T extends Plugin> {

    static <O extends Plugin> @NotNull PluginConstructor<O> auto(@NotNull Class<O> clazz) {
        return new Auto<>(clazz);
    }

    //

    @Contract("-> new")
    @NotNull T create();

    //

    final class Auto<T extends Plugin> implements PluginConstructor<T> {

        private final Class<T> clazz;

        public Auto(@NotNull Class<T> clazz) {
            int mod = clazz.getModifiers();
            if (Modifier.isInterface(mod) || Modifier.isAbstract(mod)) {
                throw new IllegalArgumentException("Invalid plugin class " + clazz.getName() + " (is abstract)");
            }
            this.clazz = clazz;
        }

        //

        @Override
        public @NotNull T create() {
            Constructor<T> con;
            try {
                con = this.clazz.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Plugin class " + this.clazz.getName() + " has no primary constructor");
            }

            try {
                con.setAccessible(true);
            } catch (Exception ignored) { }

            T ret;
            try {
                ret = con.newInstance();
            } catch (InvocationTargetException | ExceptionInInitializerError e) {
                Throwable cause = e.getCause();
                if (cause == null) cause = e;
                if (cause instanceof RuntimeException) throw (RuntimeException) cause;
                throw new IllegalStateException("Failed to initialize plugin class " + this.clazz.getName(), e);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError("Unexpected reflection error", e);
            }

            return ret;
        }

    }

}
