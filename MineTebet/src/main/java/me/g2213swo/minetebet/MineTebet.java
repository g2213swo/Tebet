package me.g2213swo.minetebet;

import me.g2213swo.minetebet.sender.ServerInfoSender;
import me.g2213swo.minetebet.utils.JedisUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MineTebet extends JavaPlugin {

    public static MineTebet instance;

    private final ComponentLogger logger = ComponentLogger.logger("MineTebet");

    public static MineTebet getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger.info("MineTebet is enabled!");
        if (!JedisUtil.isPoolEnabled()) {
            JedisUtil.initializeRedis();
        }
        ServerInfoSender serverInfoSender = new ServerInfoSender();
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, serverInfoSender::sendServerInfo, 0, 20);
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        JedisUtil.closePool();
        logger.info("MineTebet is disabled!");
    }

}
