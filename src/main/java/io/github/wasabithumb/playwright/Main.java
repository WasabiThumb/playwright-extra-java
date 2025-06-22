package io.github.wasabithumb.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import io.github.wasabithumb.playwright.plugin.impl.DebugPlugin;

public class Main {

    public static void main(String[] args) {
        PlaywrightExtraOptions options = PlaywrightExtraOptions.builder()
                .plugins(DebugPlugin.debugPlugin())
                .build();

        try (PlaywrightExtra playwright = PlaywrightExtra.create(options)) {
            Browser b = playwright.firefox().launch();
            Page p = b.newPage();
            p.navigate("https://google.com/");
            p.waitForTimeout(10_000d);
        }
    }

}
