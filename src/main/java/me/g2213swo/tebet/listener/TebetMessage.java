package me.g2213swo.tebet.listener;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import me.g2213swo.tebet.ChatMode;
import me.g2213swo.tebet.Feeling;
import me.g2213swo.tebet.Tebet;
import me.g2213swo.tebet.utils.ChatGPTUtils;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.MiraiLogger;

import java.util.concurrent.ExecutionException;

public class TebetMessage implements ListenerHost {


    private static final MiraiLogger logger = Tebet.instance.getLogger();

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
            String content = JsonPath.read(replay, "$.content");
            int feeling = JsonPath.read(replay, "$.feeling");
            Feeling feelingEnum = Feeling.values()[feeling + 3];
            MessageChain messageChain = new MessageChainBuilder()
                    .append(content)
                    .append("\n")
                    .append("情绪：")
                    .append(feelingEnum.getFeelingName())
                    .build();
            event.getFriend().sendMessage(messageChain);
        } catch (ExecutionException | InterruptedException | PathNotFoundException e) {
            if (e instanceof PathNotFoundException) {
                //删除所有特殊字符
                String contentEscaped = message.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5]", "");
                if (contentEscaped.length() == 0) {
                    event.getFriend().sendMessage("很抱歉，Tebet出错了");
                }else {
                    event.getFriend().sendMessage(contentEscaped);
                }
            }
            else {
                event.getFriend().sendMessage("很抱歉，Tebet出错了");
            }
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
                        event.getGroup().sendMessage("很抱歉，Tebet出错了😟");
                    }
                }
            }
        }
    }
}