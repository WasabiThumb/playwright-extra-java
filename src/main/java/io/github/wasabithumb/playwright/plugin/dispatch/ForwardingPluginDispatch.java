package io.github.wasabithumb.playwright.plugin.dispatch;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ApiStatus.Internal
public abstract class ForwardingPluginDispatch implements PluginDispatch {

    @ApiStatus.OverrideOnly
    protected abstract @NotNull Iterator<? extends PluginDispatch> targets();

    @ApiStatus.OverrideOnly
    protected abstract void onError(
            @NotNull String methodName,
            @NotNull PluginDispatch target,
            @NotNull Throwable error
    );

    protected void dispatch(@NotNull String methodName, @NotNull Consumer<PluginDispatch> task) {
        Iterator<? extends PluginDispatch> iter = this.targets();
        PluginDispatch next;

        while (iter.hasNext()) {
            next = iter.next();
            try {
                task.accept(next);
            } catch (Throwable error) {
                this.onError(methodName, next, error);
            }
        }
    }

    protected <T> void unaryDispatch(
            @NotNull String methodName,
            @NotNull BiConsumer<PluginDispatch, T> task,
            @NotNull T value
    ) {
        this.dispatch(methodName, (PluginDispatch pd) -> task.accept(pd, value));
    }

    //

    @Override
    public void beforeLaunch(@NotNull BrowserType.LaunchOptions options) {
        this.unaryDispatch("beforeLaunch", PluginDispatch::beforeLaunch, options);
    }

    @Override
    public void afterLaunch(@NotNull Browser browser) {
        this.unaryDispatch("afterLaunch", PluginDispatch::afterLaunch, browser);
    }

    @Override
    public void beforeConnect(@NotNull BrowserType.ConnectOptions options) {
        this.unaryDispatch("beforeConnect", PluginDispatch::beforeConnect, options);
    }

    @Override
    public void afterConnect(@NotNull Browser browser) {
        this.unaryDispatch("afterConnect", PluginDispatch::afterConnect, browser);
    }

    @Override
    public void onBrowser(@NotNull Browser browser) {
        this.unaryDispatch("onBrowser", PluginDispatch::onBrowser, browser);
    }

    @Override
    public void onPageCreated(@NotNull Page page) {
        this.unaryDispatch("onPageCreated", PluginDispatch::onPageCreated, page);
    }

    @Override
    public void onPageClose(@NotNull Page page) {
        this.unaryDispatch("onPageClose", PluginDispatch::onPageClose, page);
    }

    @Override
    public void onDisconnected(@NotNull Browser browser) {
        this.unaryDispatch("onDisconnected", PluginDispatch::onDisconnected, browser);
    }

    @Override
    public void beforeContext(@NotNull Browser.NewContextOptions options, @NotNull Browser browser) {
        this.dispatch("beforeContext", (PluginDispatch pd) -> pd.beforeContext(options, browser));
    }

    @Override
    public void onContextCreated(@NotNull BrowserContext context, @NotNull Browser.NewContextOptions options) {
        this.dispatch("onContextCreated", (PluginDispatch pd) -> pd.onContextCreated(context, options));
    }

}
