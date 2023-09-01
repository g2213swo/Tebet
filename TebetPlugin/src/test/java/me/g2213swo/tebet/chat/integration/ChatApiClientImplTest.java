package me.g2213swo.tebet.chat.integration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;
@ExtendWith(MockitoExtension.class)
class ChatApiClientImplTest {
    @Mock
    ChatApiClientImpl chatApiClient1;

    @org.junit.jupiter.api.Test
    void createChatModel() throws IOException {
//        ChatApiClientImpl chatApiClient1 = (ChatApiClientImpl) chatApiClient;
//        chatApiClient1.setApiKey("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");

        Mockito.when(chatApiClient1.createChatModel(Mockito.any(File.class))).thenCallRealMethod();

        Mockito.when(chatApiClient1.fetchModels()).thenReturn(List.of());
        Mockito.when(chatApiClient1.fetchFineTuningJobs(Mockito.anyInt())).thenReturn(List.of());
//        Mockito.when(chatApiClient1.uploadFile(Mockito.any(File.class))).thenReturn("fileId");
        Mockito.when(chatApiClient1.createFineTuningJob(Mockito.anyString())).thenReturn("jobId");
        String model = chatApiClient1.createChatModel(new File("training.txt"));
        System.out.println(model);
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