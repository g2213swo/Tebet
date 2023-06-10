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
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.MiraiLogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public abstract class TebetMessageHandler {
    protected final MiraiLogger logger = Tebet.INSTANCE.getLogger();

    private final Bot bot = Tebet.INSTANCE.getTebetBot();
    private final ChatApiClientImpl chatApiClient = new ChatApiClientImpl();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(ChatUser.class, (JsonSerializer<ChatUser>) (src, typeOfSrc, context) -> {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.getNickName());
        jsonObject.addProperty("message", src.getMessage());

        return jsonObject;
    }).create();

    private final RequestDebouncer debouncer = new RequestDebouncer();
    private final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(8);

    private final Random random = new Random();

    protected void handleGPTMessage(ChatUser chatUser, MessageEvent event, Consumer<MessageChain> sendMessage) {
        handleGPTMessage(chatUser, event, sendMessage, false);
    }

    protected void handleGPTMessage(ChatUser chatUser, MessageEvent event, Consumer<MessageChain> sendMessage, boolean isSecondSend) {
        try {
            if (chatUser.getQQ() == bot.getId()) {
                return;
            }

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
            String reply = gptResponse.getMessage().getContent();

            reply = "[" + reply + "]";
            reply = reply.replaceAll("\n", "");


            //debug
            logger.info(reply);

            if (gptResponse.isSuccess() && gptResponse.getMessage().getRole() == MessageRole.assistant) {
                ChatContextHolder.saveChatMessage(chatUser, gptResponse.getMessage());

                //获取要发送的消息列表
                List<MessageChain> singleMessages = handleOutputs(reply);
                // 4. send message
                sendMessages(chatUser, singleMessages, sendMessage);
            } else {
                logger.warning("message type not support");
                sendMessage.accept(new MessageChainBuilder().append("呜呜，Tebet脑子过载了༼ つ ◕_◕ ༽つ").build());
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
        if (content.isEmpty()) {
            content = JsonPath.read(replay, "$..message");
        }

        List<Integer> feeling = JsonPath.read(replay, "$..feeling");

        //debug
        logger.info("content: " + content);
        logger.info("feeling: " + feeling);

        if (feeling.isEmpty()){
            for (int i = 0; i < content.size(); i++) {
                feeling.add(0);
            }
        }

        if (content.isEmpty() || content.size() != feeling.size()) {
            throw new IllegalArgumentException("content size is not equal to feeling size");
        }

        List<MessageChain> messageChains = new ArrayList<>();
        for (int i = 0; i < content.size(); i++) {
            Feeling feelingEnum = Feeling.getFeeling(feeling.get(i));

            if (feelingEnum == null) {
                throw new IllegalArgumentException("feeling" + feeling.get(i) + "is null");
            }

            Image feelingImage = FeelingUtil.getFeelingImage(feelingEnum);

            MessageChain messageChain;
            //随机概率发送表情
            if (feelingImage == null || random.nextInt(100) < 40) {
                messageChain = new MessageChainBuilder()
                        .append(content.get(i))
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
        sendMessagesWithDelay(chatUser, messageChains, sendMessage, random.nextInt(3), TimeUnit.SECONDS);
    }

    /**
     * 隔delay秒发送消息，直到列表里的消息全部发送完毕
     *
     * @param chatUser      用户
     * @param messageChains 消息列表
     * @param sendMessage   发送消息的方法
     * @param delay         延迟
     */

    protected void sendMessagesWithDelay(ChatUser chatUser, List<MessageChain> messageChains, Consumer<MessageChain> sendMessage, int delay, TimeUnit timeUnit) {
        Iterator<MessageChain> iterator = messageChains.iterator();
        while (iterator.hasNext()) {
            MessageChain message = iterator.next();
            iterator.remove();
            schedule.schedule(() -> {
                sendMessage.accept(message);
            }, delay, timeUnit);
            delay = random.nextInt(10) + message.contentToString().length() / 8 + 1;
        }
        debouncer.onRequestFinished(chatUser.getQQ());
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
                sendMessage.accept(new MessageChainBuilder().append("呜呜，Tebet脑子过载了༼ つ ◕_◕ ༽つ").build());
            } else {
                // 重新发送
                chatUser.setMessage(contentEscaped);
                handleGPTMessage(chatUser, null, sendMessage, true);
            }
        } else if (e instanceof SpamException) {
            sendMessage.accept(new MessageChainBuilder().append("我还在思考QWQ").build());
            logger.warning(e.getMessage());
        } else {
            sendMessage.accept(new MessageChainBuilder().append("呜呜，Tebet脑子过载了༼ つ ◕_◕ ༽つ").build());
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
                case CLEAR:
                    chatUser.clear();
                    messageChainBuilder.append("Tebet突然失去了与你的记忆UWU");
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
