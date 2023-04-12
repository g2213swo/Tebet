package me.g2213swo.minetebet.sender;

import me.g2213swo.minetebet.MineTebet;
import me.g2213swo.minetebet.utils.JedisUtil;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

public class ServerInfoSender {
    private static final Jedis jedis = JedisUtil.getJedis();
    private static final MineTebet plugin = MineTebet.getINSTANCE();

    public void sendServerInfo() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> jedis.setex("server_info",20, "test"), 0, 20);
    }
}
