package me.g2213swo.tebet.command.subcmds.training;

import me.g2213swo.tebet.chat.integration.ChatApiClientImpl;
import me.g2213swo.tebetapi.command.AbstractSubCommand;
import me.g2213swo.tebetapi.integration.TrainFiles;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;

public class TrainFilesListCmd extends AbstractSubCommand {

    public static final TrainFilesListCmd INSTANCE = new TrainFilesListCmd();

    public TrainFilesListCmd() {
        super("trainfileslist");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        Bukkit.getScheduler().runTaskAsynchronously(me.g2213swo.tebet.TebetPlugin.getInstance(), () -> {
            sender.sendMessage("正在获取训练文件列表...");
            try {
                List<TrainFiles> trainFiles = ChatApiClientImpl.getINSTANCE().fetchTrainingFiles();
                sender.sendMessage("训练文件列表:");
                trainFiles.forEach(trainFile -> sender.sendMessage(trainFile.object() + " " + trainFile.id()));
            } catch (IOException e) {
                sender.sendMessage("获取训练文件列表失败, 请检查控制台");
            }
        });
        return true;
    }
}
