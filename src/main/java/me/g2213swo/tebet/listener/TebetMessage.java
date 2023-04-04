package me.g2213swo.tebet.listener;

import me.g2213swo.tebet.ChatMode;
import me.g2213swo.tebet.utils.ChatGPTUtils;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;

import java.util.concurrent.ExecutionException;

public class TebetMessage implements ListenerHost {


    /**
     * 私聊
     *
     * @param event 私聊事件
     */
    @EventHandler
    public void sendGPT(FriendMessageEvent event) {
        //获取消息
        String message = event.getMessage().contentToString();
        try {
            String replay = ChatGPTUtils.generateGPTText(message, ChatMode.PRIVATE_ONLY, event.getFriend().getId()).get();
            event.getFriend().sendMessage(replay);
        } catch (ExecutionException | InterruptedException e) {
            event.getFriend().sendMessage("出错了");
        }
    }


    //群内被AT事件
    @EventHandler
    public void sendGPT(GroupMessageEvent event) {
        String message = event.getMessage().contentToString();
        for (Message atMessage : event.getMessage()) {
            if (atMessage instanceof At) {
                //判断是否是机器人被AT
                At at = (At) atMessage;
                if (at.getTarget() == event.getBot().getId()) {
                    //获取引用消息
                    QuoteReply quoteReply = event.getMessage().contains(QuoteReply.Key) ? event.getMessage().get(QuoteReply.Key) : null;
                    try {
                        String messageWithoutPrefix = message.replaceFirst("^@[1-9][0-9]{4,10}", "");
                        String replay;
                        if (quoteReply != null) {
                            replay = ChatGPTUtils.generateGPTText("之前的话：" + quoteReply.getSource().getOriginalMessage() + "。" + messageWithoutPrefix,
                                    ChatMode.GROUP_ONLY, event.getGroup().getId()).get();
                        } else {
                            replay = ChatGPTUtils.generateGPTText(messageWithoutPrefix,
                                    ChatMode.GROUP_ONLY, event.getGroup().getId()).get();
                        }
                        MessageChain messageChain = new MessageChainBuilder()
                                .append(new At(event.getSender().getId()))
                                .append(" ")
                                .append(replay)
                                .build();
                        event.getGroup().sendMessage(messageChain);

                    } catch (ExecutionException | InterruptedException e) {
                        event.getGroup().sendMessage("出错了");
                    }
                }
            }
        }
    }
}