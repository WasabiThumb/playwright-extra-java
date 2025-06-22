package io.github.wasabithumb.playwright;

import com.microsoft.playwright.Playwright;
import io.github.wasabithumb.playwright.plugin.PluginConstructor;
import io.github.wasabithumb.playwright.plugin.PluginManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A {@link Playwright} instance extended with
 * plugin support
 * @see #create(PlaywrightExtraOptions)
 */
@ApiStatus.NonExtendable
public interface PlaywrightExtra extends Playwright {

    /**
     * Canonical method to create a new playwright-extra instance.
     * Binds the logger and any plugins if specified.
     */
    @Contract("_ -> new")
    @CheckReturnValue
    static @NotNull PlaywrightExtra create(@NotNull PlaywrightExtraOptions options) {
        Logger logger = options.logger();
        Map<String, String> env = options.env();
        List<PluginConstructor<?>> plugins = options.plugins();

        Playwright base;
        if (logger != null) {
            base = PlaywrightExtraHacks.createImpl(env, logger);
        } else {
            Playwright.CreateOptions baseOptions = new Playwright.CreateOptions();
            if (env != null) baseOptions.setEnv(env);
            base = Playwright.create(baseOptions);
        }

        PlaywrightExtraImpl ret = new PlaywrightExtraImpl(base, logger);
        PluginManager manager = ret.plugins();

        try {
            for (PluginConstructor<?> con : plugins) {
                manager.register(con.create());
            }
        } catch (Throwable e) {
            try {
                ret.close();
            } catch (Throwable e1) {
                e.addSuppressed(e1);
            }
            throw e;
        }

        manager.seal();
        return ret;
    }

    /**
     * Wrapper for {@link #create(PlaywrightExtraOptions)}
     */
    @Contract("_ -> new")
    static @NotNull PlaywrightExtra create(@NotNull PlaywrightExtraOptions.Builder options) {
        return create(options.build());
    }

    /**
     * Wrapper for {@link Playwright#create()}
     * @see #create(PlaywrightExtraOptions)
     */
    @Contract("-> new")
    static @NotNull PlaywrightExtra create() {
        PlaywrightExtraImpl ret = new PlaywrightExtraImpl(Playwright.create(), null);
        ret.plugins().seal();
        return ret;
    }

    /**
     * Wrapper for {@link Playwright#create(CreateOptions)}
     * @see #create(PlaywrightExtraOptions)
     */
    @Contract("_ -> new")
    @ApiStatus.Obsolete
    static @NotNull PlaywrightExtra create(@NotNull Playwright.CreateOptions options) {
        PlaywrightExtraImpl ret = new PlaywrightExtraImpl(Playwright.create(options), null);
        ret.plugins().seal();
        return ret;
    }

    //

    @Contract(pure = true)
    @NotNull PluginManager plugins();

}
