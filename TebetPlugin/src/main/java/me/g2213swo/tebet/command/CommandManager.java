package me.g2213swo.tebet.command;

import me.g2213swo.tebet.command.maincmds.TebetMainCommand;
import me.g2213swo.tebetapi.manager.IManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

public class CommandManager implements IManager {

    @Override
    public void load() {
        registerCommands();
    }

    @Override
    public void unload() {
    }

    private void registerCommands() {
        TebetMainCommand mainCommand = new TebetMainCommand();
        PluginCommand tebetCommand = Bukkit.getPluginCommand("tebet");
        if (tebetCommand != null) {
            tebetCommand.setExecutor(mainCommand);
            tebetCommand.setTabCompleter(mainCommand);
        }
    }
}
