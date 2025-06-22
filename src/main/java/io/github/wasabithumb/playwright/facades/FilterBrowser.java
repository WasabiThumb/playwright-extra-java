package io.github.wasabithumb.playwright.facades;

import com.microsoft.playwright.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

abstract class FilterBrowser implements Browser {

    protected final Browser handle;

    FilterBrowser(@NotNull Browser handle) {
        this.handle = handle;
    }

    //

    @Override
    public void onDisconnected(Consumer<Browser> handler) {
        this.handle.onDisconnected(handler);
    }

    @Override
    public void offDisconnected(Consumer<Browser> handler) {
        this.handle.offDisconnected(handler);
    }

    @Override
    public BrowserType browserType() {
        return this.handle.browserType();
    }

    @Override
    public void close(CloseOptions options) {
        this.handle.close(options);
    }

    @Override
    public List<BrowserContext> contexts() {
        return this.handle.contexts();
    }

    @Override
    public boolean isConnected() {
        return this.handle.isConnected();
    }

    @Override
    public CDPSession newBrowserCDPSession() {
        return this.handle.newBrowserCDPSession();
    }

    @Override
    public BrowserContext newContext(NewContextOptions options) {
        return this.handle.newContext();
    }

    @Override
    public Page newPage(NewPageOptions options) {
        return this.handle.newPage(options);
    }

    @Override
    public void startTracing(Page page, StartTracingOptions options) {
        this.handle.startTracing(page, options);
    }

    @Override
    public byte[] stopTracing() {
        return this.handle.stopTracing();
    }

    @Override
    public String version() {
        return this.handle.version();
    }

}
