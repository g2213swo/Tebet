package me.g2213swo.tebet.listener;

import me.g2213swo.tebet.model.ChatUser;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;

public class TebetMessage extends TebetMessageHandler implements ListenerHost {
    private final ChatUser.Factory chatUserFactory = ChatUser.Factory.getInstance();

    /**
     * 私聊
     *
     * @param event 私聊事件
     */
    @EventHandler
    public void sendGPT(FriendMessageEvent event) {
        //获取消息
        String message = event.getMessage().contentToString();
        //获取用户
        ChatUser chatUser = chatUserFactory.getChatUser(event.getFriend().getId());
        chatUser.setMessage(message);

        handleGPTMessage(chatUser, event, messageChain -> event.getFriend().sendMessage(messageChain));
    }

    /**
     * 群内被AT
     */
    @EventHandler
    public void sendGPT(GroupMessageEvent event) {
        String message = event.getMessage().contentToString();
        for (Message atMessage : event.getMessage()) {
            if (atMessage instanceof At) {
                At at = (At) atMessage;
                //判断是否是机器人被AT
                if (at.getTarget() == event.getBot().getId()) {
                    //去除@机器人的前缀
                    String messageWithoutPrefix = message.replaceFirst("^@[1-9][0-9]{4,10}", "");
                    //获取用户
                    At atChatUser = new At(event.getSender().getId());
                    ChatUser chatUser = chatUserFactory.getChatUser(event.getSender().getId());
                    chatUser.setMessage(messageWithoutPrefix);

                    handleGPTMessage(chatUser, event, messageChain -> event.getGroup().sendMessage(atChatUser.plus(messageChain)));
                }
            }
        }
    }
}

