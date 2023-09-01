package me.g2213swo.tebet.command.subcmds;

import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebet.listeners.ChatListener;
import me.g2213swo.tebet.listeners.ListenerManager;
import me.g2213swo.tebetapi.command.AbstractSubCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatCmd extends AbstractSubCommand {
    public static final ChatCmd INSTANCE = new ChatCmd();

    private final ChatListener chatListener;

    public ChatCmd() {
        super("chat");
        this.chatListener = ((ListenerManager) TebetPlugin.getInstance().getManager(ListenerManager.class)).getChatListener();
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (sender instanceof Player player){
            if (chatListener.isListenedPlayer(player)) {
                chatListener.removeListenedPlayer(player);
                player.sendMessage(Component.text("已退出聊天模式"));
            } else if (!chatListener.isListenedPlayer(player)) {
                chatListener.addListenedPlayer(player);
                player.sendMessage(Component.text("已进入聊天模式"));
            }
        }
        return true;
    }
}
