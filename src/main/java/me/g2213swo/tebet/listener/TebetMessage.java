package me.g2213swo.tebet.listener;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import me.g2213swo.tebet.Feeling;
import me.g2213swo.tebet.Tebet;
import me.g2213swo.tebet.exception.SpamException;
import me.g2213swo.tebet.integration.ChatApiClientImpl;
import me.g2213swo.tebet.model.ChatMessage;
import me.g2213swo.tebet.model.ChatUser;
import me.g2213swo.tebet.model.MessageRole;
import me.g2213swo.tebet.receiver.ServerInfoReceiver;
import me.g2213swo.tebet.utils.ChatContextHolder;
import me.g2213swo.tebet.utils.FeelingUtil;
import me.g2213swo.tebet.utils.RequestDebouncer;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.MiraiLogger;

import java.util.List;
import java.util.function.Consumer;

public class TebetMessage implements ListenerHost {

    private static final MiraiLogger logger = Tebet.INSTANCE.getLogger();


    private final ChatApiClientImpl chatApiClient = new ChatApiClientImpl();

    private final RequestDebouncer debouncer = new RequestDebouncer();

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
        ChatUser chatUser = new ChatUser.ChatUserBuilder()
                .setQQ(event.getFriend().getId())
                .setMessage(message)
                .build();
        handleGPTMessage(chatUser, messageChain -> event.getFriend().sendMessage(messageChain));
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
                    ChatUser.ChatUserBuilder chatUserBuilder = new ChatUser.ChatUserBuilder();
                    chatUserBuilder
                            .setQQ(event.getSender().getId())
                            .setMessage(messageWithoutPrefix);
                    ChatUser chatUser = chatUserBuilder.build();

                    handleGPTMessage(chatUser, messageChain -> event.getGroup().sendMessage(atChatUser.plus(messageChain)));
                }
            }
        }
    }

    /**
     * 处理消息
     *
     * @param chatUser    用户
     * @param sendMessage 发送消息
     */
    private void handleGPTMessage(ChatUser chatUser, Consumer<MessageChain> sendMessage) {
        try {
            //请求防抖
            if (!debouncer.shouldAllowRequest(chatUser.getQQ())) {
                throw new SpamException(chatUser.getQQ() + "请求过多！");
            }
            //保存消息
            ChatContextHolder.saveChatMessage(chatUser, new ChatMessage(MessageRole.user, chatUser.getMessage()));

            //获取上下文
            List<ChatMessage> chatContext = ChatContextHolder.getChatContext(chatUser);

            chatContext.add(0, new ChatMessage(MessageRole.system, chatUser.getChatOption().getSystemInput()));
            //处理服务器信息
            ServerInfoReceiver.ServerInfo serverInfo = chatUser.getServerInfo();
            System.out.println(serverInfo);
            if (serverInfo != null) {
                String serverInfoStr = serverInfo.toString();
                boolean serverInfoExists = false;
                for (ChatMessage message : chatContext) {
                    if (message.getRole() == MessageRole.assistant && message.getContent().startsWith("Server CPU")) {
                        message.setContent(serverInfoStr);
                        serverInfoExists = true;
                        break;
                    }
                }
                if (!serverInfoExists) {
                    chatContext.add(1, new ChatMessage(MessageRole.assistant, serverInfoStr));
                }
            }else {
                logger.warning("ServerInfo is null");
                String serverInfoStr = "Server is offline";
                chatContext.add(1, new ChatMessage(MessageRole.assistant, serverInfoStr));
            }

            ChatApiClientImpl.ChatResponse gptResponse = chatApiClient.chat(chatUser.getQQ(), chatContext, null);
            String replay = gptResponse.getMessage().getContent();

            //debug
            logger.info(replay);

            if (gptResponse.isSuccess() &&
                    gptResponse.getMessage().getRole() == MessageRole.assistant) {
                ChatContextHolder.saveChatMessage(chatUser, gptResponse.getMessage());

                // 4. send message
                String content;

                content = JsonPath.read(replay, "$.content");

                int feeling = JsonPath.read(replay, "$.feeling");
                Feeling feelingEnum = Feeling.getFeeling(feeling);

                if (feelingEnum == null) {
                    throw new IllegalArgumentException();
                }

                Image feelingImage = FeelingUtil.getFeelingImage(feelingEnum);
                MessageChain messageChain;
                if (feelingImage == null) {
                    messageChain = new MessageChainBuilder()
                            .append(content)
                            .append("\n")
                            .append("情绪：")
                            .append(feelingEnum.getFeelingName())
                            .build();
                } else {
                    Image.Builder feelingImageBuilder = Image.Builder.newBuilder(feelingImage.getImageId());
                    feelingImageBuilder.setEmoji(true);
                    feelingImageBuilder.setSize(233);
                    feelingImageBuilder.setHeight(feelingImage.getHeight());
                    feelingImageBuilder.setWidth(feelingImage.getWidth());
                    feelingImageBuilder.setType(feelingImage.getImageType());
                    messageChain = new MessageChainBuilder()
                            .append(content)
                            .append("\n")
                            .append(feelingImageBuilder.build())
                            .append("\n")
                            .build();
                }
                sendMessage.accept(messageChain);

                debouncer.onRequestFinished(chatUser.getQQ());
            } else {
                logger.warning("message type not support");
                debouncer.onRequestFinished(chatUser.getQQ());
            }
        } catch (PathNotFoundException |
                 IllegalArgumentException | SpamException e) {
            handleException(e, chatUser, sendMessage);
            debouncer.onRequestFinished(chatUser.getQQ());
        }
    }

    /**
     * 处理异常
     *
     * @param e           异常
     * @param chatUser    用户
     * @param sendMessage 发送消息
     */
    private void handleException(Exception e, ChatUser chatUser, Consumer<MessageChain> sendMessage) {
        if (e instanceof PathNotFoundException) {
            // 删除所有特殊字符
            String contentEscaped = chatUser.getMessage().replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5]", "");
            if (contentEscaped.length() == 0) {
                sendMessage.accept(new MessageChainBuilder().append("很抱歉，Tebet出错了").build());
            } else {
                sendMessage.accept(new MessageChainBuilder().append(contentEscaped).build());
            }
        } else if (e instanceof SpamException) {
            sendMessage.accept(new MessageChainBuilder().append("我还在思考QWQ").build());
        } else {
            sendMessage.accept(new MessageChainBuilder().append("很抱歉，Tebet出错了").build());
        }
    }
}