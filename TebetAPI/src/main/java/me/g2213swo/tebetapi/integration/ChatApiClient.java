package me.g2213swo.tebetapi.integration;

import com.google.gson.Gson;
import me.g2213swo.tebetapi.model.ChatMessage;
import me.g2213swo.tebetapi.model.ChatOption;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface ChatApiClient {
    Gson gson = new Gson();
    @NotNull
    ChatResponse chat(UUID chatId, List<ChatMessage> chatContext, ChatOption options);
}
