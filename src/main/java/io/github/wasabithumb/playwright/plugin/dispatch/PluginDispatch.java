package io.github.wasabithumb.playwright.plugin.dispatch;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface PluginDispatch {

    void beforeLaunch(@NotNull BrowserType.LaunchOptions options);

    void afterLaunch(@NotNull Browser browser);

    void beforeConnect(@NotNull BrowserType.ConnectOptions options);

    void afterConnect(@NotNull Browser browser);

    void onBrowser(@NotNull Browser browser);

    void onPageCreated(@NotNull Page page);

    void onPageClose(@NotNull Page page);

    void onDisconnected(@NotNull Browser browser);

    void beforeContext(@NotNull Browser.NewContextOptions options, @NotNull Browser browser);

    void onContextCreated(@NotNull BrowserContext context, @NotNull Browser.NewContextOptions options);

}
