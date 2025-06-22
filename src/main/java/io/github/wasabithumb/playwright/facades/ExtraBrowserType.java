package io.github.wasabithumb.playwright.facades;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import io.github.wasabithumb.playwright.plugin.PluginManager;
import io.github.wasabithumb.playwright.plugin.dispatch.PluginDispatch;
import org.jetbrains.annotations.NotNull;

final class ExtraBrowserType extends FilterBrowserType {

    private final PluginManager plugins;

    ExtraBrowserType(@NotNull BrowserType handle, @NotNull PluginManager plugins) {
        super(handle);
        this.plugins = plugins;
    }

    //

    @Override
    public Browser launch(LaunchOptions options) {
        if (options == null) options = new LaunchOptions();
        PluginDispatch dispatch = this.plugins.dispatch();
        dispatch.beforeLaunch(options);
        Browser b = super.launch(options);
        dispatch.afterLaunch(b);
        dispatch.onBrowser(b);
        b.onDisconnected(dispatch::onDisconnected);
        return Facades.browser(b, this.plugins);
    }

    @Override
    public Browser connect(String wsEndpoint, ConnectOptions options) {
        if (options == null) options = new ConnectOptions();
        PluginDispatch dispatch = this.plugins.dispatch();
        dispatch.beforeConnect(options);
        Browser b = super.connect(wsEndpoint, options);
        dispatch.afterConnect(b);
        dispatch.onBrowser(b);
        b.onDisconnected(dispatch::onDisconnected);
        return Facades.browser(b, this.plugins);
    }

}
