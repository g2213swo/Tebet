package me.g2213swo.tebet.listener;

import me.g2213swo.tebet.Tebet;
import me.g2213swo.tebet.receiver.ReceiverManager;
import me.g2213swo.tebet.receiver.ServerInfoReceiver;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.BotOnlineEvent;

public class TebetOnline implements ListenerHost {

    private final Tebet instance = Tebet.INSTANCE;

    @EventHandler
    public void onOnline(BotOnlineEvent event) {
        Bot bot = event.getBot();
        ServerInfoReceiver serverInfoReceiver = new ServerInfoReceiver();
        if (bot.getId() == 1038796824)
        {
//            bot.getFriend(2057581537).sendMessage("我上线了");
            ReceiverManager.startReceiver(serverInfoReceiver);
        }
    }
}
