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
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class TebetMessageHandler {
    protected final MiraiLogger logger = Tebet.INSTANCE.getLogger();

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
    private final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);

    private final Random random = new Random();

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

            //转换成用户Json
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
            replay = replay.replaceAll("\n", "");


            //debug
            logger.info(replay);

            if (gptResponse.isSuccess() && gptResponse.getMessage().getRole() == MessageRole.assistant) {
                ChatContextHolder.saveChatMessage(chatUser, gptResponse.getMessage());

                //获取要发送的消息列表
                List<MessageChain> singleMessages = handleOutputs(replay);
                // 4. send message
                sendMessages(chatUser, singleMessages, sendMessage);
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
     * 处理输出
     *
     * @param replay 输出
     * @return 消息链
     */
    protected List<MessageChain> handleOutputs(String replay) {

        List<String> content = JsonPath.read(replay, "$..content");

        List<Integer> feeling = JsonPath.read(replay, "$..feeling");

        //debug
        logger.info("content: " + content);
        logger.info("feeling: " + feeling);

        if (content.size() != feeling.size()) {
            throw new IllegalArgumentException("content size not equal to feeling size");
        }

        List<MessageChain> messageChains = new ArrayList<>();
        for (int i = 0; i < content.size(); i++) {
            Feeling feelingEnum = Feeling.getFeeling(feeling.get(i));

            if (feelingEnum == null) {
                throw new IllegalArgumentException("feeling" + feeling.get(i) + "is null");
            }

            Image feelingImage = FeelingUtil.getFeelingImage(feelingEnum);

            MessageChain messageChain;
            if (feelingImage == null) {
                messageChain = new MessageChainBuilder()
                        .append(content.get(i))
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
                        .append(content.get(i))
                        .append("\n")
                        .append(feelingImageBuilder.build())
                        .append("\n")
                        .build();
            }
            messageChains.add(messageChain);
        }
        return messageChains;
    }

    /**
     * 隔随机秒数发送消息，直到列表里的消息全部发送完毕
     *
     * @param chatUser      用户
     * @param messageChains 消息列表
     * @param sendMessage   发送消息的方法
     */
    protected void sendMessages(ChatUser chatUser, List<MessageChain> messageChains, Consumer<MessageChain> sendMessage) {
        sendMessagesWithDelay(chatUser, messageChains, sendMessage, 0);
    }

    /**
     * 隔delay秒发送消息，直到列表里的消息全部发送完毕
     *
     * @param chatUser      用户
     * @param messageChains 消息列表
     * @param sendMessage   发送消息的方法
     * @param delay         延迟
     */
    protected void sendMessagesWithDelay(ChatUser chatUser, List<MessageChain> messageChains, Consumer<MessageChain> sendMessage, int delay) {
        // 消息发送完毕
        if (messageChains.size() == 0) {
            debouncer.onRequestFinished(chatUser.getQQ());
            return;
        }

        logger.info("delay: " + delay);

        schedule.schedule(() -> {
            sendMessage.accept(messageChains.get(0));
            int nextDelay = random.nextInt(5) + messageChains.get(0).contentToString().length() / 10 + 1;
            messageChains.remove(0);
            // 递归调用
            sendMessagesWithDelay(chatUser, messageChains, sendMessage, nextDelay);
        }, delay, TimeUnit.SECONDS);
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
     *
     * @param event    消息事件
     * @param chatUser 用户
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
                    ChatContextHolder.clearChatContext(chatUser);
                    break;
                case ANGRY_STOP:
                    messageChainBuilder.append("暴躁模式关闭成功");
                    isAngry = false;
                    ChatContextHolder.clearChatContext(chatUser);
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
