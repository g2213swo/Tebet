package me.g2213swo.tebet.data;


import me.g2213swo.tebet.chat.ChatMessageImpl;
import me.g2213swo.tebetapi.model.ChatMessage;
import me.g2213swo.tebetapi.model.ChatUser;
import me.g2213swo.tebetapi.model.MessageRole;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ChatContextHolder {
    /**
     * chatId -> latest chat messages
     */
    private static final Map<UUID, LRUCache.LRUChatSet> CHAT_CONTEXTS = new ConcurrentHashMap<>();

    public static void saveChatMessage(ChatUser chatUser, ChatMessage chatMessage, @NotNull List<String> assistantInputs) {
        LRUCache.LRUChatSet context = CHAT_CONTEXTS.get(chatUser.getUUID());
        // 初始化引导
        // TODO: 添加微调API的支持
        if (context == null || context.isEmpty() && !assistantInputs.isEmpty()) {
            context = new LRUCache.LRUChatSet(chatUser.getChatOption().default_context_size + assistantInputs.size());
            // 添加助手消息
            for (String assistantInput : assistantInputs) {
                context.add(new ChatMessageImpl(MessageRole.assistant, assistantInput));
            }
        }


        context.add(chatMessage);
        CHAT_CONTEXTS.put(chatUser.getUUID(), context);
    }

    @NotNull
    public static List<ChatMessage> getChatContext(ChatUser chatUser) {
        LRUCache.LRUChatSet chatSet = CHAT_CONTEXTS.get(chatUser.getUUID());
        if (chatSet == null) {
            chatSet = new LRUCache.LRUChatSet(1);
        }
        return new ArrayList<>(chatSet.keySet());
    }

    public static void clearChatContext(ChatUser chatUser) {
        CHAT_CONTEXTS.remove(chatUser.getUUID());
    }

}