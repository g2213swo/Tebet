package me.g2213swo.tebet.command.subcmds.training;

import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebet.chat.integration.ChatApiClientImpl;
import me.g2213swo.tebetapi.command.AbstractSubCommand;
import me.g2213swo.tebetapi.integration.Models;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;

public class TrainModelsListCmd extends AbstractSubCommand {
    public static final TrainModelsListCmd INSTANCE = new TrainModelsListCmd();

    public TrainModelsListCmd() {
        super("trainmodelslist");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        Bukkit.getScheduler().runTaskAsynchronously(TebetPlugin.getInstance(), () -> {
            sender.sendMessage("正在获取训练模型列表...");
            try {
                List<Models> models = ChatApiClientImpl.getINSTANCE().fetchModels();
                sender.sendMessage("模型列表:");
                models.forEach(model -> sender.sendMessage(model.owned_by() + " -- " + model.id()));
            } catch (IOException e) {
                sender.sendMessage("获取模型列表失败, 请检查控制台");
            }
        });
        return true;
    }
}
