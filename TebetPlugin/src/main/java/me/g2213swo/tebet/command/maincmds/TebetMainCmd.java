package me.g2213swo.tebet.command.maincmds;

import me.g2213swo.tebet.command.subcmds.ChatCmd;
import me.g2213swo.tebet.command.subcmds.training.DelTrainingFilesCmd;
import me.g2213swo.tebet.command.subcmds.ReloadCmd;
import me.g2213swo.tebet.command.subcmds.training.TrainFilesListCmd;
import me.g2213swo.tebet.command.subcmds.training.TrainJobsListCmd;
import me.g2213swo.tebet.command.subcmds.training.TrainModelsListCmd;
import me.g2213swo.tebetapi.command.AbstractMainCommand;

public class TebetMainCmd extends AbstractMainCommand {
    public TebetMainCmd() {
        regSubCommand(ReloadCmd.INSTANCE);
        regSubCommand(ChatCmd.INSTANCE);
        regSubCommand(DelTrainingFilesCmd.INSTANCE);
        regSubCommand(TrainFilesListCmd.INSTANCE);
        regSubCommand(TrainJobsListCmd.INSTANCE);
        regSubCommand(TrainModelsListCmd.INSTANCE);
    }
}
