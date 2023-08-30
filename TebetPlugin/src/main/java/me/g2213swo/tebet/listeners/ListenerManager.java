package me.g2213swo.tebet.listeners;

import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebetapi.manager.IManager;
import org.bukkit.event.HandlerList;

public class ListenerManager implements IManager {

    private final TebetPlugin plugin;

    private ChatListener chatListener;

    public ListenerManager(TebetPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        chatListener = new ChatListener(plugin);
        plugin.getServer().getPluginManager().registerEvents(chatListener, plugin);
    }

    @Override
    public void unload() {
        plugin.getServer().getScheduler().cancelTask(chatListener.getWaitPlayerTaskId());
        HandlerList.unregisterAll(chatListener);
    }

    public ChatListener getChatListener() {
        return chatListener;
    }
}
