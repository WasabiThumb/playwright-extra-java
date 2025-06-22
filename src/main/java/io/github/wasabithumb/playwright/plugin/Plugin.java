package io.github.wasabithumb.playwright.plugin;

import io.github.wasabithumb.playwright.plugin.dispatch.PluginDispatch;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.OverrideOnly
public interface Plugin extends PluginDispatch {

    void onPluginRegistered(@NotNull PluginManager ctx);

}
