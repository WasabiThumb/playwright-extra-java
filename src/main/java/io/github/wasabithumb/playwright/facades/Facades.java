package io.github.wasabithumb.playwright.facades;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import io.github.wasabithumb.playwright.plugin.PluginManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class Facades {

    public static @NotNull BrowserType browserType(@NotNull BrowserType base, @NotNull PluginManager plugins) {
        if (base instanceof ExtraBrowserType) return base;
        return new ExtraBrowserType(base, plugins);
    }

    public static @NotNull Browser browser(@NotNull Browser base, @NotNull PluginManager plugins) {
        if (base instanceof ExtraBrowser) return base;
        return new ExtraBrowser(base, plugins);
    }

    //

    private Facades() { }

}
