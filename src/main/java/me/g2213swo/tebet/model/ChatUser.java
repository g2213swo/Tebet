package me.g2213swo.tebet.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatUser {
    private final transient long qq;
    private transient ChatMode chatMode = ChatMode.PRIVATE_ONLY;
    private String message = "";
    private transient ChatOption chatOption = new ChatOption();

    private ChatUser(long qq) {
        this.qq = qq;
    }


    public long getQQ() {
        return qq;
    }

    public ChatMode getChatMode() {
        return chatMode;
    }

    public void setChatMode(ChatMode chatMode) {
        this.chatMode = chatMode;
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

    public void setChatOption(ChatOption chatOption) {
        this.chatOption = chatOption;
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
