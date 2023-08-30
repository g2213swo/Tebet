package me.g2213swo.tebetapi;

import me.g2213swo.tebetapi.manager.IManager;
import me.g2213swo.tebetapi.model.ChatUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface TebetAPI {
    @Nullable IManager getManager(Class<? extends IManager> managerClass);

    @NotNull ChatUser getChatUser(UUID uuid);

    @NotNull Set<UUID> getChatWaitingUsers();
}
