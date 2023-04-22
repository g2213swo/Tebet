package me.g2213swo.minetebet.sender;

import com.google.gson.Gson;
import me.g2213swo.minetebet.info.ServerInfo;
import me.g2213swo.minetebet.utils.JedisUtil;
import redis.clients.jedis.Jedis;

public final class ServerInfoSender {
    private static final Jedis jedis = JedisUtil.getJedis();

    private static final Gson gson = new Gson();

    public static void sendServerInfo() {
        String serverInfoJson = gson.toJson(new ServerInfo());
        jedis.setex("server_info", 3, serverInfoJson);
    }
}
