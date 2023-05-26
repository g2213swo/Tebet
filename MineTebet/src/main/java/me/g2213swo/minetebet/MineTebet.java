package me.g2213swo.minetebet;

import me.g2213swo.minetebet.commands.MineTebetCommand;
import me.g2213swo.minetebet.sender.ServerInfoSender;
import me.g2213swo.minetebet.utils.JedisUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

public class MineTebet extends JavaPlugin {

    private static MineTebet instance;

    public final ComponentLogger logger = ComponentLogger.logger("MineTebet");

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public final Jedis jedis = JedisUtil.getJedis();
    public final Component prefix = miniMessage.deserialize("<gray>[<dark_aqua>MineTebet<gray>]<reset> ");

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
        //处理指令
        getCommand("minetebet").setExecutor(new MineTebetCommand());

        //发送服务器信息
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, ServerInfoSender::sendServerInfo, 0, 20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        JedisUtil.closePool();
        logger.info("MineTebet is disabled!");
    }

}
