package me.g2213swo.tebet.listener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
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
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.MiraiLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class TebetMessageHandler {
    private static final MiraiLogger logger = Tebet.INSTANCE.getLogger();

    private final ChatApiClientImpl chatApiClient = new ChatApiClientImpl();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(ChatUser.class, (JsonSerializer<ChatUser>) (src, typeOfSrc, context) -> {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", src.getMessage());

        if (src.shouldSendAngryStrOnce()) {
            jsonObject.addProperty("angryStr", src.getAngryStr());
            src.setSendAngryStrOnce(false);
        }

        return jsonObject;
    }).create();

    private final RequestDebouncer debouncer = new RequestDebouncer();

    public static boolean isAngry = false;

    protected void handleGPTMessage(ChatUser chatUser, MessageEvent event, Consumer<MessageChain> sendMessage) {
        handleGPTMessage(chatUser, event, sendMessage, false);
    }

    protected void handleGPTMessage(ChatUser chatUser, MessageEvent event, Consumer<MessageChain> sendMessage, boolean isSecondSend) {
        try {
            //请求防抖
            if (!debouncer.shouldAllowRequest(chatUser.getQQ()) && !isSecondSend) {
                throw new SpamException(chatUser.getQQ() + "请求过多！");
            }

            //处理特定消息
            if (handleSpecialMessage(event, chatUser)) {
                debouncer.onRequestFinished(chatUser.getQQ());
                return;
            }

            String chatUserJson = gson.toJson(chatUser);


            //保存消息
            if (!isSecondSend) {
                ChatContextHolder.saveChatMessage(chatUser, new ChatMessage(MessageRole.user, chatUserJson));
            }

            //获取上下文
            List<ChatMessage> chatContext = ChatContextHolder.getChatContext(chatUser);

            chatContext.add(0, new ChatMessage(MessageRole.system, chatUser.getChatOption().getSystemInput()));

            ChatApiClientImpl.ChatResponse gptResponse = chatApiClient.chat(chatUser.getQQ(), chatContext, null);
            String replay = gptResponse.getMessage().getContent();

            replay = "[" + replay + "]";

            //debug
            logger.info(replay);

            if (gptResponse.isSuccess() && gptResponse.getMessage().getRole() == MessageRole.assistant) {
                ChatContextHolder.saveChatMessage(chatUser, gptResponse.getMessage());

                // 4. send message
                List<String> content = JsonPath.read(replay, "$..content");

                List<Integer> feeling = JsonPath.read(replay, "$..feeling");
                Feeling feelingEnum = Feeling.getFeeling(feeling.get(0));

                if (feelingEnum == null) {
                    throw new IllegalArgumentException();
                }

                Image feelingImage = FeelingUtil.getFeelingImage(feelingEnum);
                MessageChain messageChain;
                if (feelingImage == null) {
                    messageChain = new MessageChainBuilder().append(content.toString()).append("\n").append("情绪：").append(feelingEnum.getFeelingName()).build();
                } else {
                    Image.Builder feelingImageBuilder = Image.Builder.newBuilder(feelingImage.getImageId());
                    feelingImageBuilder.setEmoji(true);
                    feelingImageBuilder.setSize(233);
                    feelingImageBuilder.setHeight(feelingImage.getHeight());
                    feelingImageBuilder.setWidth(feelingImage.getWidth());
                    feelingImageBuilder.setType(feelingImage.getImageType());
                    messageChain = new MessageChainBuilder().append(content.toString()).append("\n").append(feelingImageBuilder.build()).append("\n").build();
                }
                sendMessage.accept(messageChain);

                debouncer.onRequestFinished(chatUser.getQQ());
            } else {
                logger.warning("message type not support");
                sendMessage.accept(new MessageChainBuilder().append("很抱歉，Tebet出错了").build());
                debouncer.onRequestFinished(chatUser.getQQ());
            }
        } catch (PathNotFoundException | IllegalArgumentException | SpamException e) {
            handleException(e, chatUser, sendMessage);
        } finally {
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
                // 重新发送
                chatUser.setMessage(contentEscaped);
                handleGPTMessage(chatUser, null, sendMessage, true);
            }
        } else if (e instanceof SpamException) {
            sendMessage.accept(new MessageChainBuilder().append("我还在思考QWQ").build());
            logger.warning(e.getMessage());
        } else {
            sendMessage.accept(new MessageChainBuilder().append("很抱歉，Tebet出错了").build());
        }
    }

    /**
     * 处理特定消息
     */
    private boolean handleSpecialMessage(MessageEvent event, ChatUser chatUser) {
        String message = event.getMessage().contentToString();
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();

        if (event.getSubject() instanceof Group) {
            messageChainBuilder.append(new At(event.getSender().getId()));
        }

        UserCommand command = UserCommand.fromString(message);

        if (command != null) {
            switch (command) {
                case ANGRY_START:
                    messageChainBuilder.append("暴躁模式启动成功");
                    chatUser.setSendAngryStrOnce(true);
                    isAngry = true;
                    break;
                case ANGRY_STOP:
                    messageChainBuilder.append("暴躁模式关闭成功");
                    isAngry = false;
                    break;
                case ANGRY_STATUS:
                    if (isAngry) {
                        messageChainBuilder.append("暴躁模式开启中");
                    } else {
                        messageChainBuilder.append("暴躁模式关闭中");
                    }
                    break;
                case ANGRY_HELP:
                    messageChainBuilder.append("暴躁模式启动：暴躁模式启动\n" + "暴躁模式关闭：暴躁模式关闭\n" + "暴躁模式状态：暴躁模式状态\n" + "暴躁模式帮助：暴躁模式帮助");
                    break;
                case SERVER_INFO:
                    List<ChatMessage> serverInfoChatContext = new ArrayList<>(List.of(new ChatMessage(MessageRole.system, chatUser.getChatOption().getSystemInput())));
                    if (ServerInfoReceiver.getServerInfo() != null) {
                        serverInfoChatContext.add(new ChatMessage(MessageRole.assistant, ServerInfoReceiver.getServerInfo().toString()));
                    } else {
                        serverInfoChatContext.add(new ChatMessage(MessageRole.assistant, "Server is offline"));
                    }
                    serverInfoChatContext.add(new ChatMessage(MessageRole.user, "服务器信息是什么"));
                    ChatApiClientImpl.ChatResponse gptResponse = chatApiClient.chat(chatUser.getQQ(), serverInfoChatContext, null);
                    String replay = gptResponse.getMessage().getContent();
                    String content = JsonPath.read(replay, "$.content");
                    messageChainBuilder.append(content);
                    break;
                default:
                    break;
            }
            event.getSubject().sendMessage(messageChainBuilder.build());
            return true;
        }
        return false;
    }
}
