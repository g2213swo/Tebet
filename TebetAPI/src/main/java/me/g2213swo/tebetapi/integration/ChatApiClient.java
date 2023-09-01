package me.g2213swo.tebetapi.integration;

import com.google.gson.Gson;
import me.g2213swo.tebetapi.model.ChatMessage;
import me.g2213swo.tebetapi.model.ChatOption;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ChatApiClient {
    Gson gson = new Gson();
    @NotNull
    ChatResponse chat(UUID chatId, List<ChatMessage> chatContext, ChatOption options);

    String createChatModel(File trainingData);

    String createFineTuningJob(String trainingFileId) throws IOException;

    List<TrainModels> fetchFineTuningJobs(int limit) throws IOException;

    List<Models> fetchModels() throws IOException;

    boolean removeTrainingFile(String fileId);

    List<TrainFiles> fetchTrainingFiles() throws IOException;

    void removeAllTrainingFiles() throws IOException;
}
