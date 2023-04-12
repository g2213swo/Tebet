package me.g2213swo.tebet.receiver;

import me.g2213swo.tebet.Tebet;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.MiraiLogger;

public class ServerInfoReceiver extends ReceiverImpl{
    private final Tebet instance = Tebet.instance;

    private final MiraiLogger logger = Tebet.instance.getLogger();
    @Override
    public void receive() {
        String serverInfo = jedis.get("server_info");
        if (serverInfo != null) {
            Bot TebetBot = instance.getTebetBot();
            TebetBot.getFriend(2057581537).sendMessage(serverInfo);
        }else {
            Bot TebetBot = instance.getTebetBot();
            TebetBot.getFriend(2057581537).sendMessage("没东西awa");
        }
    }

}