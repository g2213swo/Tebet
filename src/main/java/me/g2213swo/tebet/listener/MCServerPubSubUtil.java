package me.g2213swo.tebet.listener;

import me.g2213swo.tebet.Tebet;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.MiraiLogger;
import redis.clients.jedis.JedisPubSub;

public class MCServerPubSubUtil extends JedisPubSub {
    private final MiraiLogger logger = Tebet.INSTANCE.getLogger();

    private final Bot bot = Tebet.INSTANCE.getTebetBot();

    private final MCServerMessage mcServerMessage = new MCServerMessage();
    @Override
    public void onMessage(String channel, String message) {
        logger.info("收到消息：channel=" + channel + ", message=" + message);
        //g2213swo->awa
        String sender = message.split("->")[0];

        String msg = message.split("->")[1];

//        MessageChain replay = mcServerMessage.handleGPTMessage(msg);
        for (MessageChain messageChain : mcServerMessage.handleGPTMessage(msg)) {
            bot.getGroup(361392400).sendMessage(messageChain);
        }
    }

}
