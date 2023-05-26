package me.g2213swo.tebet.integration;

import com.jayway.jsonpath.JsonPath;
import me.g2213swo.tebet.model.ChatMessage;
import me.g2213swo.tebet.model.ChatOption;
import me.g2213swo.tebet.model.MessageRole;
import me.g2213swo.tebet.utils.Config;
import net.mamoe.mirai.internal.deps.okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatApiClientImpl implements ChatApiClient {

    public static class ChatRequest {
        private String model = "gpt-3.5-turbo";

        private List<ChatMessage> messages;

        /**
         * What sampling temperature to use, between 0 and 2.
         * Higher values like 0.8 will make the output more random,
         * while lower values like 0.2 will make it more focused and deterministic.
         */
        private double temperature = 1.0;

        private String user;

        public ChatRequest(String model, List<ChatMessage> messages) {
            this.model = model;
            this.messages = messages;
        }

        public ChatRequest(List<ChatMessage> messages) {
            this.messages = messages;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public List<ChatMessage> getMessages() {
            return messages;
        }

        public void setMessages(List<ChatMessage> messages) {
            this.messages = messages;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }

    public static class ChatResponse {
        private final boolean success;
        @NotNull
        private final ChatMessage message;

        public ChatResponse(boolean success, @NotNull ChatMessage message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        @NotNull
        public ChatMessage getMessage() {
            return message;
        }
    }

    @Override
    public @NotNull ChatResponse chat(long chatId, List<ChatMessage> chatContext, ChatOption options) {
        try {
            ChatRequest chatRequest = new ChatRequest(chatContext);
            chatRequest.setUser(String.valueOf(chatId));
            String jsonRequest = gson.toJson(chatRequest);

            OkHttpClient client = new OkHttpClient.Builder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 10808)))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json;charset=UTF-8");
            RequestBody requestBody = RequestBody.create(jsonRequest, JSON);

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .header("Authorization", "Bearer " + Config.INSTANCE.api_key)
                    .post(requestBody)
                    .build();
            //debug
            logger.info("openai request: " + jsonRequest);

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String body = response.body().string();
                String content = JsonPath.parse(body).read("$.choices[0].message.content", String.class);

                if (StringUtils.isNotBlank(body) && StringUtils.isNotBlank(content)) {
                    return new ChatResponse(true, new ChatMessage(MessageRole.assistant, content));
                }
            } else {
//                String error = JsonPath.parse(response.body().string()).read("$.error.message", String.class);
                String error = response.body().string();
                return new ChatResponse(true, new ChatMessage(MessageRole.system, error));
            }
        } catch (Exception e) {
            logger.error("http error: " + e);
            return new ChatResponse(false, new ChatMessage(MessageRole.system, e.toString()));
        }
        return new ChatResponse(false, new ChatMessage(MessageRole.system, "system unknown error"));
    }

    /**
     * 无聊天上下文的聊天
     * @param chatId 用户id
     * @param message 用户输入
     * @return 聊天结果
     */
    public @NotNull ChatResponse chat(long chatId, String message){
        ChatOption chatOption = new ChatOption();
        return chat(chatId, List.of(new ChatMessage(MessageRole.system, chatOption.getSystemInput()),
                new ChatMessage(MessageRole.user, message)), null);
    }
}