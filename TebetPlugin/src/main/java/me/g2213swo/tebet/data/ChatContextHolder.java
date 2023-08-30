package me.g2213swo.tebet.data;


import me.g2213swo.tebet.chat.ChatMessageImpl;
import me.g2213swo.tebet.data.LRUCache;
import me.g2213swo.tebetapi.model.ChatMessage;
import me.g2213swo.tebetapi.model.ChatUser;
import me.g2213swo.tebetapi.model.MessageRole;

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

    public static void saveChatMessage(ChatUser chatUser, ChatMessage chatMessage) {
        saveChatMessage(chatUser, chatMessage, false); // 默认情况下，pinMessage 为 false
    }

    public static void saveChatMessage(ChatUser chatUser, ChatMessage chatMessage, boolean pinMessage) {
        LRUCache.LRUChatSet context = CHAT_CONTEXTS.get(chatUser.getUUID());

        //初始化引导
        if (context == null || context.isEmpty()) {
            List<String> assistantInputs = chatUser.getChatOption().getAssistantInputs(chatUser);
            context = new LRUCache.LRUChatSet(chatUser.getChatOption().default_context_size + assistantInputs.size());
            for (String string : assistantInputs) {
                context.add(new ChatMessageImpl(MessageRole.assistant, string));
            }
        }

        context.add(chatMessage);
        CHAT_CONTEXTS.put(chatUser.getUUID(), context);

        if (pinMessage) {
            pinChatMessage(chatUser, chatMessage);
        }
    }

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


    public static void pinChatMessage(ChatUser chatUser, ChatMessage chatMessage) {
        LRUCache.LRUChatSet context = CHAT_CONTEXTS.get(chatUser.getUUID());

        if (context != null) {
            context.pinKey(chatMessage);
        }
    }

    public static void unpinChatMessage(ChatUser chatUser, ChatMessage chatMessage) {
        LRUCache.LRUChatSet context = CHAT_CONTEXTS.get(chatUser.getUUID());

        if (context != null) {
            context.unpinKey(chatMessage);
        }
    }
}