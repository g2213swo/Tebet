package me.g2213swo.tebet.model;

import me.g2213swo.tebet.listener.TebetOnline;
import me.g2213swo.tebet.receiver.ServerInfoReceiver;

public class ChatUser {
    private final long qq;

    private final ChatMode chatMode;

    private final String message;

    private final ServerInfoReceiver serverInfoReceiver;

    private final ChatOption chatOption;

    private ChatUser(long qq, ChatMode chatMode, String message, ChatOption chatOption, ServerInfoReceiver serverInfoReceiver) {
        this.qq = qq;
        this.chatMode = chatMode;
        this.message = message;
        this.chatOption = chatOption;
        this.serverInfoReceiver = serverInfoReceiver;
    }

    public long getQQ() {
        return qq;
    }

    public ServerInfoReceiver.ServerInfo getServerInfo() {
        return serverInfoReceiver.getServerInfo();
    }

    public ChatOption getChatOption() {
        return chatOption;
    }

    public String getMessage() {
        return message;
    }

    public static class ChatUserBuilder {
        private long qq;
        private ChatMode chatMode = ChatMode.PRIVATE_ONLY;
        private String message = "";
        private ServerInfoReceiver serverInfoReceiver = TebetOnline.serverInfoReceiver;

        private ChatOption chatOption = new ChatOption();

        public ChatUserBuilder setQQ(long qq) {
            this.qq = qq;
            return this;
        }

        public ChatUserBuilder setChatMode(ChatMode chatMode) {
            this.chatMode = chatMode;
            return this;
        }

        public ChatUserBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ChatUserBuilder setChatOption(ChatOption chatOption) {
            this.chatOption = chatOption;
            return this;
        }

        public ChatUserBuilder setServerInfoReceiver(ServerInfoReceiver serverInfoReceiver) {
            this.serverInfoReceiver = serverInfoReceiver;
            return this;
        }
        public ChatUser build() {
            return new ChatUser(qq, chatMode, message, chatOption, serverInfoReceiver);
        }
    }
}
