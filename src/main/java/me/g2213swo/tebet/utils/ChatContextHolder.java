package me.g2213swo.tebet.utils;

import me.g2213swo.tebet.model.*;

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
        // default context size is 4, contains user and assistant messages
        LRUCache.LRUChatSet context = CHAT_CONTEXTS.get(chatUser.getQQ());

        //初始化引导
        if (context == null) {
            List<String> assistantInputs = ChatOption.getAssistantInputs(chatUser.isAngry());
            context = new LRUCache.LRUChatSet(4 + assistantInputs.size());
            for (String string : assistantInputs) {
                context.add(new ChatMessage(MessageRole.assistant, string));
            }
        }

        context.add(chatMessage);
        CHAT_CONTEXTS.put(chatUser.getQQ(), context);
    }

    public static List<ChatMessage> getChatContext(ChatUser chatUser) {
        LRUCache.LRUChatSet chatSet = CHAT_CONTEXTS.get(chatUser.getQQ());
        if (chatSet == null) {
            chatSet = new LRUCache.LRUChatSet(1);
        }
        return new ArrayList<>(chatSet.keySet());
    }

}
