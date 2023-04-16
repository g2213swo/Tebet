package me.g2213swo.tebet.model;

public class ChatUser {
    private final long qq;

    private final ChatMode chatMode;

    private final String message;


    private final boolean isCodeExecute;

    private ChatUser(long qq, ChatMode chatMode, String message, boolean isCodeExecute){
        this.qq = qq;
        this.chatMode = chatMode;
        this.message = message;
        this.isCodeExecute = isCodeExecute;
    }

    public long getQQ() {
        return qq;
    }

    public ChatMode getChatMode() {
        return chatMode;
    }


    public String getMessage() {
        return message;
    }


    public boolean isCodeExecute() {
        return isCodeExecute;
    }


    @Override
    public String toString() {
        return "ChatUser{" +
                "qq=" + qq +
                ", chatMode=" + chatMode +
                ", message='" + message + '\'' +
                '}';
    }

    public static class ChatUserBuilder{
        private long qq;
        private ChatMode chatMode = ChatMode.PRIVATE_ONLY;
        private String message = "";


        private boolean isCodeExecute = false;
        public ChatUserBuilder setQQ(long qq){
            this.qq = qq;
            return this;
        }

        public ChatUserBuilder setChatMode(ChatMode chatMode){
            this.chatMode = chatMode;
            return this;
        }

        public ChatUserBuilder setMessage(String message){
            this.message = message;
            return this;
        }

        public ChatUserBuilder setCodeExecute(boolean codeExecute) {
            isCodeExecute = codeExecute;
            return this;
        }


        public ChatUser build(){
            return new ChatUser(qq, chatMode, message, isCodeExecute);
        }
    }
}
