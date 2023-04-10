package me.g2213swo.tebet.utils;

import com.jayway.jsonpath.JsonPath;
import me.g2213swo.tebet.Tebet;
import me.g2213swo.tebet.model.ChatMode;
import me.g2213swo.tebet.model.ChatOption;
import net.mamoe.mirai.internal.deps.okhttp3.*;
import net.mamoe.mirai.utils.MiraiLogger;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ChatGPTUtils {

    private static final Jedis jedis = JedisUtil.getJedis();
    private static final MiraiLogger logger = Tebet.instance.getLogger();

    /**
     * This method generates GPT text.
     *
     * @param input    The input string.
     * @param chatMode 聊天模式
     * @param qq       qq号
     * @return A CompletableFuture that completes with the generated text.
     */
    public static CompletableFuture<String> generateGPTText(String input, ChatMode chatMode, long qq) {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder stringBuilder = new StringBuilder();

            // Model configuration
            stringBuilder.append("{\n  \"model\": \"gpt-3.5-turbo\"," +
                            "\n  \"temperature\": 0.9," +
                            "\n  \"messages\": [\n        ")
                    .append("{\"role\": \"system\", \"content\": \"")
                    .append(ChatOption.getSystemInput(chatMode))
                    .append("\"},\n        ");

            for (int i = 0; i < ChatOption.getAssistantInputs().size(); i++) {
                stringBuilder.append("{\"role\": \"assistant\", \"content\": \"")
                        .append(ChatOption.getAssistantInputs().get(i))
                        .append("\"},\n        ");
            }

            for (String string : ChatContextHolder.getChatContext(qq)) {
                stringBuilder.append("{\"role\": \"assistant\", \"content\": \"")
                        .append(string)
                        .append("\"},\n        ");
            }

            stringBuilder.append("{\"role\": \"user\", \"content\": \"")
                    .append(input)
                    .append("\"}\n")
                    .append("    \n],")
                    .append("\n  \"user\": \"")
                    .append(qq)
                    .append("\"\n}");

            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 10808)))
                        .readTimeout(100, TimeUnit.SECONDS)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .connectionPool(new ConnectionPool(32, 5, TimeUnit.MINUTES))
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, stringBuilder.toString());
                Request request = new Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + Config.INSTANCE.api_key.get())
                        .build();
                Response response = client.newCall(request).execute();
                String json = response.body().string();

                // debug
                logger.info(stringBuilder.toString());

                // 将json存入redis
                jedis.publish("openai", json);

                //debug
                logger.info(json);
                //Gson 对象
                String content = JsonPath.parse(json).read("$.choices[0].message.content", String.class);
                //debug
                logger.info(content);

                String contentWithoutLineBreaks = content.replaceAll("[^a-zA-Z0-9\\u4E00-\\u9FA5]+", "\\\\$0");
                String message = contentWithoutLineBreaks.replaceAll("([\\\\\"'])", "\\\\$0");

                ChatContextHolder.saveChatMessage(qq, message);

                return content;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        });
    }
}

