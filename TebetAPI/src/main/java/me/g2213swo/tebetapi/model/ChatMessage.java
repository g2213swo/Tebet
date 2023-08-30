package me.g2213swo.tebetapi.model;

public interface ChatMessage {
    MessageRole getRole();

    String getContent();

    void setContent(String content);

    void setRole(MessageRole role);
}
