package io.github.wasabithumb.playwright.plugin;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.OverrideOnly
public abstract class AbstractPlugin implements Plugin {

    @Override
    public void onPluginRegistered(@NotNull PluginManager ctx) { }

    @Override
    public void beforeLaunch(@NotNull BrowserType.LaunchOptions options) { }

    @Override
    public void afterLaunch(@NotNull Browser browser) { }

    @Override
    public void beforeConnect(@NotNull BrowserType.ConnectOptions options) { }

    @Override
    public void afterConnect(@NotNull Browser browser) { }

    @Override
    public void onBrowser(@NotNull Browser browser) { }

    @Override
    public void onPageCreated(@NotNull Page page) { }

    @Override
    public void onPageClose(@NotNull Page page) { }

    @Override
    public void onDisconnected(@NotNull Browser browser) { }

    @Override
    public void beforeContext(@NotNull Browser.NewContextOptions options, @NotNull Browser browser) { }

    @Override
    public void onContextCreated(@NotNull BrowserContext context, @NotNull Browser.NewContextOptions options) { }

}
