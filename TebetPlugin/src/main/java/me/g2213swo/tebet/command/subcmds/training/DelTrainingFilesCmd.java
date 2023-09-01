package me.g2213swo.tebet.command.subcmds.training;

import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebet.chat.integration.ChatApiClientImpl;
import me.g2213swo.tebetapi.command.AbstractSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;

public class DelTrainingFilesCmd extends AbstractSubCommand {

    public static final DelTrainingFilesCmd INSTANCE = new DelTrainingFilesCmd();

    public DelTrainingFilesCmd() {
        super("deltrainingfiles");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        Bukkit.getScheduler().runTaskAsynchronously(TebetPlugin.getInstance(), () -> {
            sender.sendMessage("正在删除训练文件...");
            try {
                ChatApiClientImpl.getINSTANCE().removeAllTrainingFiles();
            } catch (IOException e) {
                sender.sendMessage("删除训练文件失败, 请检查控制台");
            }
        });
        return true;
    }
}
