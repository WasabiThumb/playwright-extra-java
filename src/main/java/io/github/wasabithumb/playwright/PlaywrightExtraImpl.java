package io.github.wasabithumb.playwright;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Selectors;
import io.github.wasabithumb.playwright.facades.Facades;
import io.github.wasabithumb.playwright.plugin.PluginManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

@ApiStatus.Internal
final class PlaywrightExtraImpl implements PlaywrightExtra {

    private final Playwright backing;
    private final Logger logger;
    private final PluginManager plugins;

    PlaywrightExtraImpl(@NotNull Playwright backing, @Nullable Logger logger) {
        this.backing = backing;
        this.logger = logger;
        this.plugins = new PluginManager(logger);
    }

    //

    @Override
    public @NotNull PluginManager plugins() {
        return this.plugins;
    }

    @Override
    public BrowserType chromium() {
        return Facades.browserType(this.backing.chromium(), this.plugins);
    }

    @Override
    public BrowserType firefox() {
        return Facades.browserType(this.backing.firefox(), this.plugins);
    }

    @Override
    public APIRequest request() {
        return this.backing.request();
    }

    @Override
    public Selectors selectors() {
        return this.backing.selectors();
    }

    @Override
    public BrowserType webkit() {
        return Facades.browserType(this.backing.webkit(), this.plugins);
    }

    @Override
    public void close() {
        if (this.logger != null) {
            PlaywrightExtraHacks.closeImpl(this.backing, this.logger);
        } else {
            this.backing.close();
        }
    }

}
