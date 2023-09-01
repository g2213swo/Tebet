package me.g2213swo.tebet.chat.integration;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;
@ExtendWith(MockitoExtension.class)
class ChatApiClientImplTest {

    /**
     * Spy 表示部分模拟的可以部分模拟的测试对象
     */
    @Spy
    private ChatApiClientImpl chatApiClient = (ChatApiClientImpl) ChatApiClientImpl.getINSTANCE();

    @org.junit.jupiter.api.Test
    void createChatModel() throws IOException {
        Mockito.doReturn(List.of()).when(chatApiClient).fetchModels();
        Mockito.doReturn(List.of()).when(chatApiClient).fetchFineTuningJobs(Mockito.anyInt());
        Mockito.doReturn("file").when(chatApiClient).uploadFile(Mockito.any(File.class));
        Mockito.doReturn("modelId").when(chatApiClient).createFineTuningJob(Mockito.anyString());
        String model = chatApiClient.createChatModel(new File("training.txt"));

        // 校验测试结果相等
        Assertions.assertEquals("modelId", model);
    }

    @org.junit.jupiter.api.Test
    void uploadFile() {
    }

    @org.junit.jupiter.api.Test
    void createFineTuningJob() {
    }

    @org.junit.jupiter.api.Test
    void fetchFineTuningJobs() {
    }

    @org.junit.jupiter.api.Test
    void fetchModels() {


    }

    @org.junit.jupiter.api.Test
    void removeTrainingFile() {
    }

    @org.junit.jupiter.api.Test
    void fetchTrainingFiles() {
    }

    @org.junit.jupiter.api.Test
    void removeAllTrainingFiles() {
    }
}