package me.g2213swo.tebet.listener;

import me.g2213swo.tebet.ChatMode;
import me.g2213swo.tebet.utils.ChatGPTUtils;
import me.g2213swo.tebet.utils.JedisUtil;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutionException;

public class TebetMessage implements ListenerHost {
    private static final Jedis jedis = JedisUtil.getJedis();

    @EventHandler
    public void sendGPT(FriendMessageEvent event) {
        String message = event.getMessage().contentToString();

        try {
            String replay = ChatGPTUtils.generateGPTText(message, ChatMode.PRIVATE_ONLY, event.getFriend().getId()).get();
            event.getFriend().sendMessage(replay);
        } catch (ExecutionException | InterruptedException e) {
            event.getFriend().sendMessage("出错了");
        }
    }
}