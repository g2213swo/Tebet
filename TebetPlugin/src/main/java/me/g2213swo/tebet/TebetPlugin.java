package me.g2213swo.tebet;

import me.g2213swo.tebet.api.TebetAPIImpl;
import me.g2213swo.tebet.command.CommandManager;
import me.g2213swo.tebet.config.ConfigManager;
import me.g2213swo.tebet.listeners.ListenerManager;
import me.g2213swo.tebetapi.TebetAPI;
import me.g2213swo.tebetapi.TebetAPIProvider;
import me.g2213swo.tebetapi.manager.IManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class TebetPlugin extends JavaPlugin {
    private static TebetPlugin instance;
    public static TebetPlugin getInstance() {
        return instance;
    }
    private CommandManager commandManager;
    private ListenerManager listenerManager;
    private ConfigManager configManager;

    @Override
    public void onLoad() {
        instance = this;

        TebetAPIImpl instance = new TebetAPIImpl(this);
        getServer().getServicesManager().register(TebetAPI.class, instance, this, ServicePriority.Normal);
        TebetAPIProvider.register(instance);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        configManager = new ConfigManager(this);
        configManager.load();
        listenerManager = new ListenerManager(this);
        listenerManager.load();
        commandManager = new CommandManager();
        commandManager.load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        commandManager.unload();
        listenerManager.unload();
        configManager.unload();
        TebetAPIProvider.unregister();
    }

    public void reload() {
        commandManager.unload();
        listenerManager.unload();
        configManager.unload();
        configManager.load();
        listenerManager.load();
        commandManager.load();
    }

    public IManager getManager(Class<? extends IManager> clazz) {
        if (clazz == CommandManager.class) {
            return commandManager;
        }
        if (clazz == ListenerManager.class){
            return listenerManager;
        }
        if (clazz == ConfigManager.class){
            return configManager;
        }
        return null;
    }
}