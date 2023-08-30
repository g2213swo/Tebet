package me.g2213swo.tebet.command.subcmds;

import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebet.command.CommandManager;
import me.g2213swo.tebetapi.TebetAPIProvider;
import me.g2213swo.tebetapi.command.AbstractSubCommand;
import me.g2213swo.tebetapi.manager.IManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommands extends AbstractSubCommand {

    public static final ReloadCommands INSTANCE = new ReloadCommands();

    public ReloadCommands() {
        super("reload");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        TebetPlugin.getInstance().reload();
        sender.sendMessage("Reloaded TebetPlugin");
        return true;
    }
}
