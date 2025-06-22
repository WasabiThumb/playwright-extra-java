package io.github.wasabithumb.playwright.facades;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import io.github.wasabithumb.playwright.plugin.PluginManager;
import io.github.wasabithumb.playwright.plugin.dispatch.PluginDispatch;
import org.jetbrains.annotations.NotNull;

final class ExtraBrowser extends FilterBrowser {

    private final PluginManager plugins;

    ExtraBrowser(@NotNull Browser handle, @NotNull PluginManager plugins) {
        super(handle);
        this.plugins = plugins;
    }

    //

    @Override
    public BrowserType browserType() {
        return Facades.browserType(super.browserType(), this.plugins);
    }

    @Override
    public BrowserContext newContext(NewContextOptions options) {
        if (options == null) options = new NewContextOptions();
        PluginDispatch dispatch = this.plugins.dispatch();
        dispatch.beforeContext(options, this);
        BrowserContext ret = super.newContext(options);
        dispatch.onContextCreated(ret, options);
        return ret;
    }

    @Override
    public Page newPage(NewPageOptions options) {
        if (options == null) options = new NewPageOptions();
        PluginDispatch dispatch = this.plugins.dispatch();
        Page page = super.newPage(options);
        dispatch.onPageCreated(page);
        page.onClose(dispatch::onPageClose);
        return page;
    }

}
