package me.g2213swo.minetebet.sender;

import com.google.gson.Gson;
import me.g2213swo.minetebet.MineTebet;
import me.g2213swo.minetebet.info.ServerInfo;
import me.g2213swo.minetebet.utils.JedisUtil;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

public class ServerInfoSender {
    private static final Jedis jedis = JedisUtil.getJedis();
    private static final MineTebet plugin = MineTebet.getInstance();

    private static final Gson gson = new Gson();

    public void sendServerInfo() {
        String serverInfoJson = gson.toJson(new ServerInfo());
        jedis.setex("server_info", 20, serverInfoJson);
    }
}
