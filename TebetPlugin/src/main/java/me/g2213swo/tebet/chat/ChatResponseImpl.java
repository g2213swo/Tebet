package me.g2213swo.tebet.chat;

import me.g2213swo.tebetapi.integration.ChatResponse;
import me.g2213swo.tebetapi.model.ChatMessage;
import org.jetbrains.annotations.NotNull;

public record ChatResponseImpl(boolean success, @NotNull ChatMessage message) implements ChatResponse {
}
