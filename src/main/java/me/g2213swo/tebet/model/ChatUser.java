package me.g2213swo.tebet.model;

import me.g2213swo.tebet.utils.ChatContextHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatUser {
    private final transient long qq;
    private final transient ChatMode chatMode = ChatMode.PRIVATE_ONLY;
    private String message = "";
    private final transient ChatOption chatOption = new ChatOption();

    private ChatUser(long qq) {
        this.qq = qq;
    }


    public long getQQ() {
        return qq;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatOption getChatOption() {
        return chatOption;
    }

    public void clear(){
        ChatContextHolder.clearChatContext(this);
    }

    public static class Factory {
        private static Factory instance;
        private final Map<Long, ChatUser> chatUsers;

        private Factory() {
            chatUsers = new ConcurrentHashMap<>();
        }

        public static Factory getInstance() {
            if (instance == null) {
                instance = new Factory();
            }
            return instance;
        }

        public ChatUser getChatUser(long qq) {
            if (chatUsers.containsKey(qq)) {
                return chatUsers.get(qq);
            } else {
                ChatUser newUser = new ChatUser(qq);
                chatUsers.put(qq, newUser);
                return newUser;
            }
        }
    }
}
