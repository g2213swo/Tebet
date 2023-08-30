package me.g2213swo.tebet.chat.integration;

import com.jayway.jsonpath.JsonPath;
import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebet.chat.ChatMessageImpl;
import me.g2213swo.tebet.chat.ChatResponseImpl;
import me.g2213swo.tebet.config.ConfigManager;
import me.g2213swo.tebetapi.integration.ChatApiClient;
import me.g2213swo.tebetapi.integration.ChatResponse;
import me.g2213swo.tebetapi.model.ChatMessage;
import me.g2213swo.tebetapi.model.ChatOption;
import me.g2213swo.tebetapi.model.MessageRole;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChatApiClientImpl implements ChatApiClient {

    private final TebetPlugin plugin;

    private final ComponentLogger logger;

    private static final String API_KEY = ConfigManager.getApiKey();

    public ChatApiClientImpl(TebetPlugin plugin) {
        this.plugin = plugin;
        this.logger = ComponentLogger.logger("TebetChat");
    }

    public static class ChatRequest {
        private String model = "gpt-3.5-turbo-16k";

        private final List<ChatMessage> messages;

        /**
         * What sampling temperature to use, between 0 and 2.
         * Higher values like 0.8 will make the output more random,
         * while lower values like 0.2 will make it more focused and deterministic.
         */
        private final double temperature = 0.5;

        private String user;

        public ChatRequest(String model, List<ChatMessage> messages) {
            this.model = model;
            this.messages = messages;
        }

        public ChatRequest(List<ChatMessage> messages) {
            this.messages = messages;
        }
        public void setUser(String user) {
            this.user = user;
        }
    }

    @Override
    public @NotNull ChatResponse chat(UUID chatId, List<ChatMessage> chatContext, ChatOption options) {
        try {
            ChatRequest chatRequest = new ChatRequest(chatContext);
            chatRequest.setUser(String.valueOf(chatId));
            String jsonRequest = gson.toJson(chatRequest);

            HttpURLConnection connection = getHttpURLConnection(jsonRequest);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String responseBody = reader.lines().collect(Collectors.joining("\n"));
                    String content = JsonPath.parse(responseBody).read("$.choices[0].message.content", String.class);

                    if (StringUtils.isNotBlank(responseBody) && StringUtils.isNotBlank(content)) {
                        return new ChatResponseImpl(true, new ChatMessageImpl(MessageRole.assistant, content));
                    }
                }
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    String errorBody = reader.lines().collect(Collectors.joining("\n"));
                    return new ChatResponseImpl(true, new ChatMessageImpl(MessageRole.system, errorBody));
                }
            }
        } catch (IOException e) {
            logger.error("http error: " + e);
            return new ChatResponseImpl(false, new ChatMessageImpl(MessageRole.system, e.toString()));
        }
        return new ChatResponseImpl(false, new ChatMessageImpl(MessageRole.system, "system unknown error"));
    }

    @NotNull
    private static HttpURLConnection getHttpURLConnection(String jsonRequest) throws IOException {
        URL url = new URL("https://api.openai.com/v1/chat/completions");

        // 设置代理
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8888));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);

        connection.setConnectTimeout(30000); // 30 seconds
        connection.setReadTimeout(30000);

        // 设置请求方法和请求头
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);

        // 设置允许输出，默认为false
        connection.setDoOutput(true);

        // 写入请求体数据
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }
}