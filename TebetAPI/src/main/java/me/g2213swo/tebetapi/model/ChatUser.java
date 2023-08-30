package me.g2213swo.tebetapi.model;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface ChatUser {
    UUID getUUID();

    void clearChatContext();

    void setMessage(String message);

    String getName();

    String getMessage();

    ChatOption getChatOption();

    Player getPlayer();
}
