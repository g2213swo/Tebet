package me.g2213swo.tebet.command.subcmds.training;

import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebet.chat.integration.ChatApiClientImpl;
import me.g2213swo.tebetapi.command.AbstractSubCommand;
import me.g2213swo.tebetapi.integration.TrainModels;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;

public class TrainJobsListCmd extends AbstractSubCommand {
    public static final TrainJobsListCmd INSTANCE = new TrainJobsListCmd();

    public TrainJobsListCmd() {
        super("trainjobslist");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        Bukkit.getScheduler().runTaskAsynchronously(TebetPlugin.getInstance(), () -> {
            sender.sendMessage("正在获取训练任务列表...");
            try {
                List<TrainModels> trainJobs = ChatApiClientImpl.getINSTANCE().fetchFineTuningJobs(3);
                sender.sendMessage("训练任务列表:");
                trainJobs.forEach(trainJob -> sender.sendMessage(trainJob.object() + " " + trainJob.id()));
            } catch (IOException e) {
                sender.sendMessage("获取训练任务列表失败, 请检查控制台");
            }
        });
        return true;
    }
}
