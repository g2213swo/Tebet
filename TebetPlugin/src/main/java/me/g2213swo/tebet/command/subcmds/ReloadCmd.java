package me.g2213swo.tebet.command.subcmds;

import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebetapi.command.AbstractSubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCmd extends AbstractSubCommand {

    public static final ReloadCmd INSTANCE = new ReloadCmd();

    public ReloadCmd() {
        super("reload");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        TebetPlugin.getInstance().reload();
        sender.sendMessage("Reloaded TebetPlugin");
        return true;
    }
}
