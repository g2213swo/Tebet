package me.g2213swo.tebet.commands;

import me.g2213swo.tebet.Tebet;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;

public class TebetCommand extends JRawCommand {
    public static final TebetCommand INSTANCE = new TebetCommand();

    private TebetCommand() {
        super(Tebet.instance, "tebet"); // 使用插件主类对象作为指令拥有者；设置主指令名为 "tebet"
        setUsage("/tebet"); // 设置用法，这将会在 /help 中展示
        setDescription("Tebet机器酱系列指令"); // 设置描述，也会在 /help 中展示
        setPrefixOptional(false); // 设置指令前缀是可选的，即使用 `tebet` 也能执行指令而不需要 `tebet`
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain args) {
        //处理指令
        switch (args.contentToString().toLowerCase()){
            case "reload":
                Tebet.instance.reloadPlugin();
                sender.sendMessage("插件重载成功");
                break;
            default:
                sender.sendMessage("未知指令");
                break;
        }
    }
}
