package me.g2213swo.minetebet.commands;

import me.g2213swo.minetebet.MineTebet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MineTebetCommand implements TabExecutor {

    private final MineTebet instance = MineTebet.getInstance();

    private final ComponentLogger logger = instance.logger;

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private final Component prefix = instance.prefix;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //处理指令
        if (args.length == 0) {
            sender.sendMessage(prefix.append(miniMessage.deserialize("<red>参数异常")));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                sender.sendMessage(prefix.append(miniMessage.deserialize("<gray>插件重载成功")));
                break;
            case "help":
                sender.sendMessage(prefix.append(miniMessage.deserialize("<gray>指令帮助")));
                break;
            case "send":
                if (args.length == 1) {
                    sender.sendMessage(prefix.append(miniMessage.deserialize("<red>参数异常")));
                    return true;
                }
                //获取消息
                String message = String.join(" ", args).substring(5);
                message = sender.getName() + "->" + message; //g2213swo->awa
                logger.info(message);
                //发送消息
                instance.jedis.publish("gpt", message);
                break;

            default:
                sender.sendMessage(prefix.append(miniMessage.deserialize("<gray>未知指令")));
                break;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("reload", "help", "send");
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("send")) {
                    if (args[1].isEmpty()) {
                        return List.of("发送给GPT处理的消息");
                    }
                }
            }
        }
        return null;
    }
}
