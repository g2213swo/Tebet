package me.g2213swo.tebetapi.integration;

import me.g2213swo.tebetapi.model.ChatMessage;
import org.jetbrains.annotations.NotNull;

public interface ChatResponse {
    boolean success();

    @NotNull ChatMessage message();
}
