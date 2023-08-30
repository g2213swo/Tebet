package me.g2213swo.tebet.config;

import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebetapi.manager.IManager;
import org.jetbrains.annotations.NotNull;

public class ConfigManager implements IManager {

    private final TebetPlugin plugin;

    private static String apiKey;

    public ConfigManager(TebetPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        plugin.saveDefaultConfig();
        apiKey = plugin.getConfig().getString("api_key");
    }

    @Override
    public void unload() {
        plugin.saveConfig();
    }

    @NotNull public static String getApiKey() {
        return apiKey;
    }
}
