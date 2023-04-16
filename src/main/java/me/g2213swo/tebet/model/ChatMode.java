package me.g2213swo.tebet.model;

public enum ChatMode {

    GROUP_ONLY("group"),
    PRIVATE_ONLY("private");

    private final String mode;

    ChatMode(String mode) {
        this.mode = mode;
    }

    public static ChatMode getChatMode(String mode) {
        for (ChatMode chatMode : ChatMode.values()) {
            if (chatMode.mode.equals(mode)) {
                return chatMode;
            }
        }
        return null;
    }
}