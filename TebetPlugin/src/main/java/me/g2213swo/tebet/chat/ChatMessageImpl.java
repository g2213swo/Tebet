package me.g2213swo.tebet.chat;

import me.g2213swo.tebetapi.model.ChatMessage;
import me.g2213swo.tebetapi.model.MessageRole;

public class ChatMessageImpl implements ChatMessage {
    private MessageRole role;
    private String content;

    public ChatMessageImpl(MessageRole role, String content) {
        this.role = role;
        this.content = content;
    }

    @Override
    public MessageRole getRole() {
        return role;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void setRole(MessageRole role) {
        this.role = role;
    }
}