package me.g2213swo.tebet.chat;

import me.g2213swo.tebet.data.ChatContextHolder;
import me.g2213swo.tebetapi.model.ChatOption;
import me.g2213swo.tebetapi.model.ChatUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatUserImpl implements ChatUser {
    private final transient UUID uuid;
    private String message = "";
    private final transient ChatOption chatOption = new ChatOptionImpl();

    private final String nickName;

    private ChatUserImpl(UUID qq, String nickName) {
        this.uuid = qq;
        this.nickName = nickName;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getMessage() {
        return message;
    }
    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getName() {
        return nickName;
    }

    @Override
    public ChatOption getChatOption() {
        return chatOption;
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public void clearChatContext() {
        ChatContextHolder.clearChatContext(this);
    }

    public static class Factory {
        private static Factory instance;
        private final Map<UUID, ChatUser> chatUsers;

        private Factory() {
            chatUsers = new ConcurrentHashMap<>();
        }

        public static Factory getInstance() {
            if (instance == null) {
                instance = new Factory();
            }
            return instance;
        }

        public ChatUser getChatUserWithNick(UUID uuid, String nickName) {
            if (chatUsers.containsKey(uuid)) {
                return chatUsers.get(uuid);
            } else {
                ChatUser newUser = new ChatUserImpl(uuid, nickName);
                chatUsers.put(uuid, newUser);
                return newUser;
            }
        }
    }
}