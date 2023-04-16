package me.g2213swo.tebet.integration;

import com.google.gson.Gson;
import me.g2213swo.tebet.Tebet;
import me.g2213swo.tebet.model.ChatMessage;
import me.g2213swo.tebet.model.ChatOption;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ChatApiClient {
    Gson gson = new Gson();
    MiraiLogger logger = Tebet.instance.getLogger();
    @NotNull
    ChatApiClientImpl.ChatResponse chat(long chatId, List<ChatMessage> chatContext, ChatOption options);
}
