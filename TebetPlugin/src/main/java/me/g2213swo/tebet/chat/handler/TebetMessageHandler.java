package me.g2213swo.tebet.chat.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebet.chat.ChatMessageImpl;
import me.g2213swo.tebet.chat.integration.ChatApiClientImpl;
import me.g2213swo.tebet.data.ChatContextHolder;
import me.g2213swo.tebet.utils.AdventureUtil;
import me.g2213swo.tebet.utils.ComponentUtil;
import me.g2213swo.tebetapi.integration.ChatApiClient;
import me.g2213swo.tebetapi.integration.ChatResponse;
import me.g2213swo.tebetapi.model.ChatMessage;
import me.g2213swo.tebetapi.model.ChatUser;
import me.g2213swo.tebetapi.model.MessageRole;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;


public abstract class TebetMessageHandler {
    protected final ComponentLogger logger;

    private final ChatApiClient chatApiClient;
    private final Gson gson;
    private final TebetPlugin plugin;
    protected RequestDebouncer debouncer = new RequestDebouncer();
    protected final Random random = new Random();

    private static final Component TEBET_PREFIX = ComponentUtil
            .asComponent("<gradient:#00A6FF:#B3E5FC><b>Tebet</b></gradient><gray> >></gray><reset>");

    public TebetMessageHandler(TebetPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().registerTypeAdapter(ChatUser.class, (JsonSerializer<ChatUser>) (src, typeOfSrc, context) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", src.getName());
            jsonObject.addProperty("message", src.getMessage());

            return jsonObject;
        }).create();
        this.logger = ComponentLogger.logger("TebetChat");
        this.chatApiClient = ChatApiClientImpl.getINSTANCE();
    }

    protected void handleGPTMessage(ChatUser chatUser, Consumer<Component> sendMessage) {
        try {
            // 请求防抖
            if (!debouncer.shouldAllowRequest(chatUser.getUUID())) {
                return;
            }
            // 转换成用户Json
            String chatUserJson = gson.toJson(chatUser);
            List<String> assistantInputs = chatUser.getChatOption().getAssistantInputs(chatUser);
            // 保存消息
            ChatContextHolder.saveChatMessage(chatUser, new ChatMessageImpl(MessageRole.user, chatUserJson), assistantInputs);

            // 获取上下文
            List<ChatMessage> chatContext = ChatContextHolder.getChatContext(chatUser);

            chatContext.add(0, new ChatMessageImpl(MessageRole.system, chatUser.getChatOption().getSystemInput()));

            ChatResponse gptResponse = chatApiClient.chat(chatUser.getUUID(), chatContext, null);
            String reply = gptResponse.message().getContent();
            reply = "[" + reply + "]";

            // debug
            logger.info(reply);

            if (gptResponse.success() && gptResponse.message().getRole() == MessageRole.assistant) {
                ChatContextHolder.saveChatMessage(chatUser, gptResponse.message(), List.of());

                // 获取要发送的消息列表
                List<Component> singleMessages = handleOutputs(reply);
                // 4. send message
                sendMessages(chatUser, singleMessages, sendMessage);
            } else {
                logger.warn("message type not support");
                sendMessage.accept(Component.text("呜呜，Tebet脑子过载了༼ つ ◕_◕ ༽つ"));
                debouncer.onRequestFinished(chatUser.getUUID());
            }
        } catch (PathNotFoundException | IllegalArgumentException e) {
            sendMessage.accept(Component.text("呜呜，Tebet脑子过载了༼ つ ◕_◕ ༽つ"));
        } finally {
            debouncer.onRequestFinished(chatUser.getUUID());
        }
    }


    /**
     * 处理输出
     *
     * @param replay 输出
     * @return 消息链
     */
    protected List<Component> handleOutputs(String replay) {

        List<String> content = JsonPath.read(replay, "$..content");
        if (content.isEmpty()) {
            content = JsonPath.read(replay, "$..message");
        }

        //debug
        logger.info("content: " + content);

        if (content.isEmpty()) {
            throw new IllegalArgumentException("content is empty");
        }

        List<Component> components = new ArrayList<>();
        for (String s : content) {
            Component component = TEBET_PREFIX.append(Component.space())
                    .append(ComponentUtil.asComponent(s));
            components.add(component);
        }
        return components;
    }

    protected void sendMessages(ChatUser chatUser, List<Component> components, Consumer<Component> sendMessage) {
        sendMessagesWithDelay(chatUser, components, sendMessage, random.nextInt(3));
    }

    protected void sendMessagesWithDelay(ChatUser chatUser, List<Component> components, Consumer<Component> sendMessage, int delay) {
        Iterator<Component> iterator = components.iterator();
        while (iterator.hasNext()) {
            Component component = iterator.next();
            iterator.remove();
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                sendMessage.accept(component);
                Player player = chatUser.getPlayer();
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.BLOCK_BEEHIVE_ENTER, 1, 1);
                }
                if (components.isEmpty()) {
                    debouncer.onRequestFinished(chatUser.getUUID());
                }
                AdventureUtil.sendActionbarPacket(player, Component.text("Tebet正在发送...")
                        .color(TextColor.color(random.nextInt(256), random.nextInt(256), random.nextInt(256))));
            }, delay * 20L);
            delay = delay + random.nextInt(3) + ComponentUtil.asPlainText(component).length() / 10 + 1;
        }
        debouncer.onRequestFinished(chatUser.getUUID());
    }

}