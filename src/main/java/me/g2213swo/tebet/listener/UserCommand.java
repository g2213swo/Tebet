package me.g2213swo.tebet.listener;

public enum UserCommand {
    CLEAR("清空"),
    SERVER_INFO("服务器信息");

    private final String command;

    UserCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static UserCommand fromString(String text) {
        for (UserCommand command : UserCommand.values()) {
            if (command.getCommand().equalsIgnoreCase(text)) {
                return command;
            }
        }
        return null;
    }
}
