package me.g2213swo.tebet.api;

import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebet.chat.ChatUserImpl;
import me.g2213swo.tebet.command.CommandManager;
import me.g2213swo.tebet.listeners.ChatListener;
import me.g2213swo.tebet.listeners.ListenerManager;
import me.g2213swo.tebetapi.TebetAPI;
import me.g2213swo.tebetapi.manager.IManager;
import me.g2213swo.tebetapi.model.ChatUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class TebetAPIImpl implements TebetAPI {
    private final TebetPlugin plugin;

    public TebetAPIImpl(TebetPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable IManager getManager(Class<? extends IManager> clazz) {
        return plugin.getManager(clazz);
    }

    @Override
    public @NotNull ChatUser getChatUser(UUID uuid) {
        return ChatUserImpl.Factory.getInstance().getChatUserWithNick(uuid,
                Objects.requireNonNull(plugin.getServer().getPlayer(uuid)).getName());
    }

    @Override
    public @NotNull Set<UUID> getChatWaitingUsers() {
        return ((ListenerManager) plugin.getManager(ListenerManager.class)).getChatListener().getChatWaitingUsers();
    }
}
