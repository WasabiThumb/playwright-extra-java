package io.github.wasabithumb.playwright.plugin.impl;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import io.github.wasabithumb.playwright.plugin.AbstractPlugin;
import io.github.wasabithumb.playwright.plugin.PluginConstructor;
import io.github.wasabithumb.playwright.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public class DebugPlugin extends AbstractPlugin {

    public static @NotNull PluginConstructor<DebugPlugin> debugPlugin() {
        return PluginConstructor.auto(DebugPlugin.class);
    }

    //

    @Override
    public void onPluginRegistered(@NotNull PluginManager ctx) {
        System.out.println(ctx);
        super.onPluginRegistered(ctx);
    }

    @Override
    public void beforeLaunch(@NotNull BrowserType.LaunchOptions options) {
        options.setHeadless(false);
        super.beforeLaunch(options);
    }

    @Override
    public void afterLaunch(@NotNull Browser browser) {
        super.afterLaunch(browser);
    }

    @Override
    public void beforeConnect(@NotNull BrowserType.ConnectOptions options) {
        super.beforeConnect(options);
    }

    @Override
    public void afterConnect(@NotNull Browser browser) {
        super.afterConnect(browser);
    }

    @Override
    public void onBrowser(@NotNull Browser browser) {
        throw new AssertionError("foobar");
    }

    @Override
    public void onPageCreated(@NotNull Page page) {
        super.onPageCreated(page);
    }

    @Override
    public void onPageClose(@NotNull Page page) {
        super.onPageClose(page);
    }

    @Override
    public void onDisconnected(@NotNull Browser browser) {
        super.onDisconnected(browser);
    }

    @Override
    public void beforeContext(@NotNull Browser.NewContextOptions options, @NotNull Browser browser) {
        super.beforeContext(options, browser);
    }

    @Override
    public void onContextCreated(@NotNull BrowserContext context, @NotNull Browser.NewContextOptions options) {
        super.onContextCreated(context, options);
    }

}
