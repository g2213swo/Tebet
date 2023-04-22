package me.g2213swo.tebet.utils;

import me.g2213swo.tebet.model.ChatMessage;
import me.g2213swo.tebet.model.ChatUser;
import me.g2213swo.tebet.model.LRUCache;
import me.g2213swo.tebet.model.MessageRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ChatContextHolder {
    /**
     * chatId -> latest chat messages
     */
    private static final Map<Long, LRUCache.LRUChatSet> CHAT_CONTEXTS = new ConcurrentHashMap<>();

    public static void saveChatMessage(ChatUser chatUser, ChatMessage chatMessage) {
        saveChatMessage(chatUser, chatMessage, false); // 默认情况下，pinMessage 为 false
    }

    public static void saveChatMessage(ChatUser chatUser, ChatMessage chatMessage, boolean pinMessage) {
        // default context size is 4, contains user and assistant messages
        LRUCache.LRUChatSet context = CHAT_CONTEXTS.get(chatUser.getQQ());

        //初始化引导
        if (context == null) {
            List<String> assistantInputs = chatUser.getChatOption().getAssistantInputs(chatUser);
            context = new LRUCache.LRUChatSet(chatUser.getChatOption().default_context_size + assistantInputs.size());
            for (String string : assistantInputs) {
                context.add(new ChatMessage(MessageRole.assistant, string));
            }
        }

        context.add(chatMessage);
        CHAT_CONTEXTS.put(chatUser.getQQ(), context);

        if (pinMessage) {
            pinChatMessage(chatUser, chatMessage);
        }
    }

    public static List<ChatMessage> getChatContext(ChatUser chatUser) {
        LRUCache.LRUChatSet chatSet = CHAT_CONTEXTS.get(chatUser.getQQ());
        if (chatSet == null) {
            chatSet = new LRUCache.LRUChatSet(1);
        }
        return new ArrayList<>(chatSet.keySet());
    }

    public static void clearChatContext(ChatUser chatUser) {
        CHAT_CONTEXTS.remove(chatUser.getQQ());
    }


    public static void pinChatMessage(ChatUser chatUser, ChatMessage chatMessage) {
        LRUCache.LRUChatSet context = CHAT_CONTEXTS.get(chatUser.getQQ());

        if (context != null) {
            context.pinKey(chatMessage);
        }
    }

    public static void unpinChatMessage(ChatUser chatUser, ChatMessage chatMessage) {
        LRUCache.LRUChatSet context = CHAT_CONTEXTS.get(chatUser.getQQ());

        if (context != null) {
            context.unpinKey(chatMessage);
        }
    }
}
