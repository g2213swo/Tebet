package me.g2213swo.tebetapi.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSubCommand {

    private final String command;
    private Map<String, AbstractSubCommand> subCommandMap;

    public AbstractSubCommand(String command) {
        this.command = command;
    }

    public boolean onCommand(CommandSender sender, List<String> args) {
        if (subCommandMap == null || args.isEmpty()) {
            return true;
        }
        AbstractSubCommand subCommand = subCommandMap.get(args.get(0));
        if (subCommand == null) {
            sender.sendMessage("§c§lTestCommand §7- §f§lHelp");
        } else {
            subCommand.onCommand(sender, args.subList(1, args.size()));
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (subCommandMap == null)
            return Collections.singletonList("");
        if (args.size() <= 1) {
            List<String> returnList = new ArrayList<>(subCommandMap.keySet());
            returnList.removeIf(str -> !str.startsWith(args.get(0)));
            return returnList;
        }
        AbstractSubCommand subCmd = subCommandMap.get(args.get(0));
        if (subCmd != null)
            return subCommandMap.get(args.get(0)).onTabComplete(sender, args.subList(1, args.size()));
        return Collections.singletonList("");
    }

    public String getSubCommand() {
        return command;
    }

    public Map<String, AbstractSubCommand> getSubCommands() {
        return Collections.unmodifiableMap(subCommandMap);
    }

    public void regSubCommand(AbstractSubCommand command) {
        if (subCommandMap == null) {
            subCommandMap = new ConcurrentHashMap<>();
        }
        subCommandMap.put(command.getSubCommand(), command);
    }
}