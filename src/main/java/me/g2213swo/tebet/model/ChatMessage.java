package me.g2213swo.tebet.model;

public class ChatMessage {
    private MessageRole role;
    private String content;

    public ChatMessage(MessageRole role, String content) {
        this.role = role;
        this.content = content;
    }

    public MessageRole getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRole(MessageRole role) {
        this.role = role;
    }
}
