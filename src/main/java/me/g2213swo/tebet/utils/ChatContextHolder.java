package me.g2213swo.tebet.utils;

import me.g2213swo.tebet.model.LRUCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ChatContextHolder {
    /**
     * chatId -> latest chat messages
     */
    private static final Map<Long, LRUCache.LRUChatSet> CHAT_CONTEXTS = new ConcurrentHashMap<>();

    public static void saveChatMessage(Long chatId, String chatMessage) {
        // default context size is 4, contains user and assistant messages
        LRUCache.LRUChatSet context = CHAT_CONTEXTS.getOrDefault(chatId, new LRUCache.LRUChatSet(4));
        context.add(chatMessage);
        CHAT_CONTEXTS.put(chatId, context);
    }
    public static List<String> getChatContext(long chatId) {
        return new ArrayList<>(Optional.ofNullable(CHAT_CONTEXTS.get(chatId))
                .orElse(new LRUCache.LRUChatSet(1))
                .keySet());
    }

}
