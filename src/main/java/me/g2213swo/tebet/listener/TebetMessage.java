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

    private int random = new Random().nextInt(5) + 2;
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
        ChatUser chatUser = chatUserFactory.getChatUserWithNick(event.getFriend().getId(), event.getFriend().getNick());

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

        //处理图片
        if (images.size() > 0) {
            return;
        }

        //获取用户
        ChatUser chatUser = chatUserFactory.getChatUserWithNick(event.getSender().getId(), event.getSender().getNick());

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
                    chatUser.setMessage(messageWithoutPrefix);
                    messages.clear();
                    handleGPTMessage(chatUser, event, messageChain -> event.getGroup().sendMessage(messageChain));
                    return;
                }
            }
        }

        if (messages.size() == random) {  //当达到5~10条消息时，GPT将会自动回复
            chatUser.setMessage(messages.toString());
            messages.clear();
            random = new Random().nextInt(5) + 2;
            handleGPTMessage(chatUser, event, messageChain -> event.getGroup().sendMessage(messageChain));
        } else {
            if (message.length() > 100){
                message = message.substring(0, 100);
            }
            messages.add(message);
            random = new Random().nextInt(5) + 2;
            //debug
//            logger.info(messages.toString());
        }
    }
}


