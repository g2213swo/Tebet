package me.g2213swo.tebet.utils;

import me.g2213swo.tebet.Tebet;
import net.mamoe.mirai.Bot;
import redis.clients.jedis.JedisPubSub;

public class JedisSubPubUtil extends JedisPubSub {
    private final Bot TebetBot = Tebet.INSTANCE.getTebetBot();


    @Override
    public void onMessage(String channel, String message) {
        TebetBot.getFriend(2057581537).sendMessage("onMessage"+"---"+channel + ":" + message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        TebetBot.getFriend(2057581537).sendMessage("onSubscribe" + "---" + channel + ":" + subscribedChannels);
    }
}