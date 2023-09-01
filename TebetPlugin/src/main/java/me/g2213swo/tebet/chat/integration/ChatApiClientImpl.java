package me.g2213swo.tebet.chat.integration;

import com.google.common.reflect.TypeToken;
import com.jayway.jsonpath.JsonPath;
import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebet.chat.ChatMessageImpl;
import me.g2213swo.tebet.chat.ChatResponseImpl;
import me.g2213swo.tebet.config.ConfigManager;
import me.g2213swo.tebetapi.integration.*;
import me.g2213swo.tebetapi.model.ChatMessage;
import me.g2213swo.tebetapi.model.ChatOption;
import me.g2213swo.tebetapi.model.MessageRole;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatApiClientImpl implements ChatApiClient {

    private static final String OPENAI_API_URL = "https://api.openai.com";

    private static final ChatApiClient INSTANCE = new ChatApiClientImpl(TebetPlugin.getInstance());

    private final TebetPlugin plugin;

    private final ComponentLogger logger;

    private static String API_KEY = ConfigManager.getApiKey();

    private final OkHttpClient client;

    public static ChatApiClient getINSTANCE() {
        return INSTANCE;
    }

    public void setApiKey(String apiKey) {
        API_KEY = apiKey;
    }

    private ChatApiClientImpl(TebetPlugin plugin) {
        this.plugin = plugin;
        this.logger = ComponentLogger.logger("TebetChat");
        this.client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static class ChatRequestImpl implements ChatRequest {
        private String model;

        private final List<ChatMessage> messages;

        /**
         * What sampling temperature to use, between 0 and 2.
         * Higher values like 0.8 will make the output more random,
         * while lower values like 0.2 will make it more focused and deterministic.
         */
        private final double temperature = 0.5;

        private String user;

        public ChatRequestImpl(String model, List<ChatMessage> messages) {
            this.model = model;
            this.messages = messages;
        }

        public ChatRequestImpl(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @Override
        public void setUser(String user) {
            this.user = user;
        }

        @Override
        public void setModel(String model) {
            this.model = model;
        }

        @Override
        public String getModel() {
            return model;
        }
    }

    @Override
    public @NotNull ChatResponse chat(UUID chatId, List<ChatMessage> chatContext, ChatOption options) {
        try {
            ChatRequestImpl chatRequestImpl = new ChatRequestImpl(chatContext);
            chatRequestImpl.setUser(String.valueOf(chatId));

            String model = createChatModel(new File(ConfigManager.getTrainingFilePath()));
            chatRequestImpl.setModel(model);

            logger.warn("Used Model:" + chatRequestImpl.getModel());
            String jsonRequest = gson.toJson(chatRequestImpl);

            // debug
            chatContext.forEach(chatMessage -> logger.info(chatMessage.getContent()));

            RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json;charset=UTF-8"));
            Request request = new Request.Builder()
                    .url(OPENAI_API_URL + "/v1/chat/completions")
                    .post(body)
                    .addHeader("Content-Type", "application/json;charset=UTF-8")
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .build();

            try (Response response = client.newCall(request).execute();) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    String content = JsonPath.parse(responseBody).read("$.choices[0].message.content", String.class);
                    if (StringUtils.isNotBlank(content)) {
                        return new ChatResponseImpl(true, new ChatMessageImpl(MessageRole.assistant, content));
                    }
                } else {
                    String jsonString = response.body().string();
                    throw new IOException("Unexpected code " + response.code() + ", info:" + jsonString);
                }
            }
        } catch (IOException e) {
            logger.error("http error: " + e);
            return new ChatResponseImpl(false, new ChatMessageImpl(MessageRole.system, e.toString()));
        }
        return new ChatResponseImpl(false, new ChatMessageImpl(MessageRole.system, "system unknown error"));
    }

    @Override
    public String createChatModel(File trainingData) {
        try {
            // step 1: get fine-tuning jobs
            List<TrainModels> fineTuningJobs = fetchFineTuningJobs(3);
            List<Models> models = fetchModels();

            List<String> trainedModelsId = models.stream()
                    .filter(models1 -> !models1.owned_by().equals("openai"))
                    .map(Models::id)
                    .filter(id -> id.startsWith("ft:"))
                    .toList();

            fineTuningJobs.stream().filter(Objects::nonNull).map(TrainModels::id).forEach(logger::warn);
            if (trainedModelsId.isEmpty()) {
                if (fineTuningJobs.isEmpty()) {
                    logger.warn("No fine-tuning jobs found, creating one...");
                    // Step 2: Upload the training data and create a fine-tuning job
                    String fileId = uploadFile(trainingData);
                    logger.warn("File ID: " + fileId);

                    String fineTuningJob = createFineTuningJob(fileId);
                    logger.warn("Fine-tuning job: " + fineTuningJob);

                    // Optionally, you can print out or log success messages, or even return some kind of status
                    logger.warn("Chat model created successfully using the training data!");

                    return fineTuningJob;
                }
                logger.warn("No models found, using default model...");
                return "gpt-3.5-turbo";
            }
            // TODO: 优化选择模型的逻辑
            return trainedModelsId.get(0);
        } catch (IOException e) {
            return "gpt-3.5-turbo";
        }
    }

    // Step 1: Upload the file and get its ID
    public String uploadFile(File trainingData) throws IOException {
        MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("purpose", "fine-tune")
                .addFormDataPart("file", trainingData.getName(),
                        RequestBody.create(trainingData, MEDIA_TYPE_TEXT))
                .build();

        Request request = new Request.Builder()
                .url(OPENAI_API_URL + "/v1/files")
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String jsonString = response.body().string();
                throw new IOException("Unexpected code " + response.code() + ", info:" + jsonString);
            }
            String responseBody = response.body().string();
            logger.warn("File Response body: " + responseBody);
            // Extract the file ID from the response
            return JsonPath.parse(responseBody).read("$.id", String.class);
        }
    }

    private record TrainingRequest(String training_file, String model) {
    }

    @Override
    // Step 2: Create a fine-tuning job
    public String createFineTuningJob(String trainingFileId) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        TrainingRequest trainingRequest = new TrainingRequest(trainingFileId, "gpt-3.5-turbo");
        String json = gson.toJson(trainingRequest);

        logger.warn("Training request: " + json);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(OPENAI_API_URL + "/v1/fine_tuning/jobs")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String jsonString = response.body().string();
                throw new IOException("Unexpected code " + response.code() + ", info:" + jsonString);
            }
            String responseBody = response.body().string();
            logger.warn("Fine-tuning job response body: " + responseBody);
            // Extract the fine-tuning job ID from the response
            return JsonPath.parse(responseBody).read("$.id", String.class);
        }
    }

    @Override
    public List<TrainModels> fetchFineTuningJobs(int limit) throws IOException {
        Request request = new Request.Builder()
                .url(OPENAI_API_URL + "/v1/fine_tuning/jobs?limit=" + limit)
                .get()
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String jsonString = response.body().string();
                throw new IOException("Unexpected code " + response.code() + ", info:" + jsonString);
            }
            String jsonString = response.body().string();

            // 使用JsonPath提取"data"部分的JSON字符串
            String dataJson = JsonPath.parse(jsonString).read("$.data").toString();

            Type type = new TypeToken<List<TrainModelsImpl>>() {
            }.getType();
            return gson.fromJson(dataJson, type);
        }
    }

    @Override
    public List<Models> fetchModels() throws IOException {
        Request request = new Request.Builder()
                .url(OPENAI_API_URL + "/v1/models")
                .get()
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String jsonString = response.body().string();
                throw new IOException("Unexpected code " + response.code() + ", info:" + jsonString);
            }
            String jsonString = response.body().string();

            // 使用JsonPath提取"data"部分的JSON字符串
            String dataJson = JsonPath.parse(jsonString).read("$.data").toString();

            Type type = new TypeToken<List<ModelsImpl>>() {
            }.getType();
            return gson.fromJson(dataJson, type);
        }
    }

    @Override
    public boolean removeTrainingFile(String fileId) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create("", mediaType);
        Request request = new Request.Builder()
                .url(OPENAI_API_URL + "/v1/files/" + fileId)
                .delete(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String jsonString = response.body().string();
                throw new IOException("Unexpected code " + response.code() + ", info:" + jsonString);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public List<TrainFiles> fetchTrainingFiles() throws IOException {
        Request request = new Request.Builder()
                .url(OPENAI_API_URL + "/v1/files")
                .get()
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String jsonString = response.body().string();
                throw new IOException("Unexpected code " + response.code() + ", info:" + jsonString);
            }

            String jsonString = response.body().string();

            // 使用JsonPath提取"data"部分的JSON字符串
            String dataJson = JsonPath.parse(jsonString).read("$.data").toString();

            Type type = new TypeToken<List<TrainFilesImpl>>() {
            }.getType();
            return gson.fromJson(dataJson, type);
        }
    }

    @Override
    public void removeAllTrainingFiles() throws IOException {
        List<TrainFiles> trainFiles = fetchTrainingFiles();
        trainFiles.forEach(trainFile -> {
            if (removeTrainingFile(trainFile.id())) {
                logger.info("File " + trainFile.id() + " deleted successfully!");
            } else {
                logger.error("File " + trainFile.id() + " deleted failed!");
            }
        });
    }
}