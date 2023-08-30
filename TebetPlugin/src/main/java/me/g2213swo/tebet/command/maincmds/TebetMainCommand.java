package me.g2213swo.tebet.command.maincmds;

import me.g2213swo.tebet.command.subcmds.ChatCommands;
import me.g2213swo.tebet.command.subcmds.ReloadCommands;
import me.g2213swo.tebetapi.command.AbstractMainCommand;

public class TebetMainCommand extends AbstractMainCommand {
    public TebetMainCommand() {
        regSubCommand(ReloadCommands.INSTANCE);
        regSubCommand(ChatCommands.INSTANCE);
    }
}
