package me.g2213swo.tebet.listener;

public enum UserCommand {
    ANGRY_START("暴躁模式启动"),
    ANGRY_STOP("暴躁模式关闭"),
    ANGRY_STATUS("暴躁模式状态"),
    ANGRY_HELP("暴躁模式帮助"),

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
