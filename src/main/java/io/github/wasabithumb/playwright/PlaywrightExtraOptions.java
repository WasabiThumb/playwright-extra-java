package io.github.wasabithumb.playwright;

import io.github.wasabithumb.playwright.plugin.Plugin;
import io.github.wasabithumb.playwright.plugin.PluginConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

public final class PlaywrightExtraOptions {

    @Contract("-> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    //

    private final Map<String, String> env;
    private final List<PluginConstructor<?>> plugins;
    private final Logger logger;

    private PlaywrightExtraOptions(
            @Nullable Map<String, String> env,
            @NotNull List<PluginConstructor<?>> plugins,
            @Nullable Logger logger
    ) {
        this.env = env;
        this.plugins = plugins;
        this.logger = logger;
    }

    //

    /**
     * Stand-in for {@link com.microsoft.playwright.Playwright.CreateOptions#env}
     */
    public @Nullable Map<String, String> env() {
        return this.env;
    }

    /**
     * Plugins to register
     */
    public @NotNull List<PluginConstructor<?>> plugins() {
        return this.plugins;
    }

    /**
     * Logger to use; if non-null, some aggressive hacks are done
     * to reroute hardcoded stdout/stderr
     */
    public @Nullable Logger logger() {
        return this.logger;
    }

    //

    public static final class Builder {

        private Map<String, String> env = null;
        private final List<PluginConstructor<?>> plugins = new LinkedList<>();
        private Logger logger = null;

        //

        /**
         * Stand-in for {@link com.microsoft.playwright.Playwright.CreateOptions#setEnv(Map)}
         */
        @Contract("_ -> this")
        public @NotNull Builder env(@Nullable Map<String, String> env) {
            this.env = env;
            return this;
        }

        /**
         * Sets the plugins to register
         */
        @Contract("_ -> this")
        public @NotNull Builder plugins(@Nullable Collection<? extends PluginConstructor<?>> plugins) {
            this.plugins.clear();
            if (plugins != null) {
                this.plugins.addAll(plugins);
            }
            return this;
        }

        /**
         * Sets the plugins to register
         */
        @Contract("_ -> this")
        public @NotNull Builder plugins(@NotNull PluginConstructor<?> @Nullable ... plugins) {
            this.plugins.clear();
            if (plugins != null) {
                this.plugins.addAll(Arrays.asList(plugins));
            }
            return this;
        }

        /**
         * Sets the plugins to register
         */
        @SafeVarargs
        @Contract("_ -> this")
        public final @NotNull Builder plugins(@NotNull Class<? extends Plugin> @Nullable ... plugins) {
            this.plugins.clear();
            if (plugins != null) {
                for (Class<? extends Plugin> clazz : plugins) {
                    this.plugins.add(PluginConstructor.auto(clazz));
                }
            }
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addPlugin(@NotNull PluginConstructor<?> plugin) {
            this.plugins.add(plugin);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addPlugin(@NotNull Class<? extends Plugin> plugin) {
            this.plugins.add(PluginConstructor.auto(plugin));
            return this;
        }

        /**
         * Sets the logger to use. If non-null, some aggressive hacks are done
         * to reroute hardcoded stdout/stderr
         */
        @Contract("_ -> this")
        public @NotNull Builder logger(@Nullable Logger logger) {
            this.logger = logger;
            return this;
        }

        @Contract("-> new")
        public @NotNull PlaywrightExtraOptions build() {
            return new PlaywrightExtraOptions(
                    (this.env != null) ? Collections.unmodifiableMap(new HashMap<>(this.env)) : null,
                    Collections.unmodifiableList(new ArrayList<>(this.plugins)),
                    this.logger
            );
        }

    }

}
