package io.github.wasabithumb.playwright.facades;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

abstract class FilterBrowserType implements BrowserType {

    protected final BrowserType handle;

    FilterBrowserType(@NotNull BrowserType handle) {
        this.handle = handle;
    }

    //

    @Override
    public Browser connect(String wsEndpoint, ConnectOptions options) {
        return this.handle.connect(wsEndpoint, options);
    }

    @Override
    public Browser connectOverCDP(String endpointURL, ConnectOverCDPOptions options) {
        return this.handle.connectOverCDP(endpointURL, options);
    }

    @Override
    public String executablePath() {
        return this.handle.executablePath();
    }

    @Override
    public Browser launch(LaunchOptions options) {
        return this.handle.launch(options);
    }

    @Override
    public BrowserContext launchPersistentContext(Path userDataDir, LaunchPersistentContextOptions options) {
        return this.handle.launchPersistentContext(userDataDir, options);
    }

    @Override
    public String name() {
        return this.handle.name();
    }

}
