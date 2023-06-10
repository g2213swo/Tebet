package me.g2213swo.tebet.model;

import me.g2213swo.tebet.Tebet;
import me.g2213swo.tebet.utils.ChatContextHolder;
import net.mamoe.mirai.Bot;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class ChatUser {
    private final transient long qq;
    private final transient ChatMode chatMode = ChatMode.PRIVATE_ONLY;
    private String message = "";
    private final transient ChatOption chatOption = new ChatOption();

    private String nickName;

    private ChatUser(long qq) {
        this.qq = qq;
        try {
            Bot bot = Tebet.INSTANCE.getTebetBot();
            this.nickName = bot.getFriendOrFail(qq).getNick();
        }catch (NoSuchElementException e) {
            this.nickName = "Unknown";
        }
    }

    private ChatUser(long qq, String nickName) {
        this.qq = qq;
        this.nickName = nickName;
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

    public String getNickName() {
        return nickName;
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

        public ChatUser getChatUserWithNick(long qq, String nickName) {
            if (chatUsers.containsKey(qq)) {
                return chatUsers.get(qq);
            } else {
                ChatUser newUser = new ChatUser(qq, nickName);
                chatUsers.put(qq, newUser);
                return newUser;
            }
        }
    }
}
