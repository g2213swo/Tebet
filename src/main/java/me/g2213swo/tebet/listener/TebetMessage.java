package me.g2213swo.tebet.listener;

import me.g2213swo.tebet.model.ChatUser;
import me.g2213swo.tebet.utils.ImageUtil;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TebetMessage extends TebetMessageHandler implements ListenerHost {
    private final ChatUser.Factory chatUserFactory = ChatUser.Factory.getInstance();

    //群内前后消息
    private final List<String> messages = new ArrayList<>();

    /**
     * 私聊
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void sendGPT(FriendMessageEvent event) {
        // 获取消息
        MessageChain message = event.getMessage();
        // 获取图片
        List<Image> images = ImageUtil.getImages(message);
        // 获取用户
        ChatUser chatUser = chatUserFactory.getChatUser(event.getFriend().getId());

        chatUser.setMessage(message.contentToString());
        handleGPTMessage(chatUser, event, messageChain -> event.getFriend().sendMessage(messageChain));
    }


    /**
     * 群内消息
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void sendGPT(GroupMessageEvent event) {
        long groupId = event.getGroup().getId();
        MessageChain groupMessageChain = event.getMessage();
        // 获取图片
        List<Image> images = ImageUtil.getImages(groupMessageChain);
        //获取用户
        ChatUser chatUser = chatUserFactory.getChatUser(event.getSender().getId());

        if (groupId != 361392400 && groupId != 795130802) {
            return;
        }

        String message = groupMessageChain.contentToString();

        //判断是否是机器人被AT
        for (Message atMessage : event.getMessage()) {
            if (atMessage instanceof At) {
                At at = (At) atMessage;
                if (at.getTarget() == event.getBot().getId()) {
                    //去除@机器人的前缀
                    String messageWithoutPrefix = message.replaceFirst("^@[1-9][0-9]{4,10}", "");
                    //获取用户
                    At atChatUser = new At(event.getSender().getId());
                    chatUser.setMessage(messageWithoutPrefix);

                    handleGPTMessage(chatUser, event, messageChain -> event.getGroup().sendMessage(atChatUser.plus(messageChain)));
                    return;
                }
            }
        }

        if (messages.size() == (new Random().nextInt(10 - 5 + 1) + 5)) {  //当达到5~10条消息时，GPT将会自动回复
            chatUser.setMessage(messages.toString());

            handleGPTMessage(chatUser, event, messageChain -> event.getGroup().sendMessage(messageChain));
            messages.clear();
        } else {
            messages.add(message);
            //debug
            logger.info(messages.toString());
        }
    }
}


