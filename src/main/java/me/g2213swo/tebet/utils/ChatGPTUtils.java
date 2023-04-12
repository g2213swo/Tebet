package me.g2213swo.tebet.utils;

import com.jayway.jsonpath.JsonPath;
import me.g2213swo.tebet.Tebet;
import me.g2213swo.tebet.model.ChatOption;
import me.g2213swo.tebet.model.ChatUser;
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
     * @param chatUser The chat user.
     * @return A CompletableFuture that completes with the generated text.
     */
    public static CompletableFuture<String> generateGPTText(ChatUser chatUser) {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder stringBuilder = new StringBuilder();

            // Model configuration
            stringBuilder.append("{\n  \"model\": \"gpt-3.5-turbo\"," +
                            "\n  \"temperature\": 0.9," +
                            "\n  \"messages\": [\n        ")
                    .append("{\"role\": \"system\", \"content\": \"")
                    .append(ChatOption.getSystemInput(chatUser.isAngry()))
                    .append("\"},\n        ");

            for (int i = 0; i < ChatOption.getAssistantInputs(chatUser.isAngry()).size(); i++) {
                stringBuilder.append("{\"role\": \"assistant\", \"content\": \"")
                        .append(ChatOption.getAssistantInputs(chatUser.isAngry()).get(i))
                        .append("\"},\n        ");
            }

            for (String string : ChatContextHolder.getChatContext(chatUser.getQQ())) {
                stringBuilder.append("{\"role\": \"assistant\", \"content\": \"")
                        .append(string)
                        .append("\"},\n        ");
            }

            stringBuilder.append("{\"role\": \"user\", \"content\": \"")
                    .append(chatUser.getMessage())
                    .append("\"}\n")
                    .append("    \n],")
                    .append("\n  \"user\": \"")
                    .append(chatUser.getQQ())
                    .append("\"\n}");

            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 10808)))
                        .readTimeout(100, TimeUnit.SECONDS)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .connectionPool(new ConnectionPool(32, 5, TimeUnit.MINUTES))
                        .build();

                // debug
                logger.info(stringBuilder.toString());

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(stringBuilder.toString(), mediaType);
                Request request = new Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + Config.INSTANCE.api_key.get())
                        .build();
                Response response = client.newCall(request).execute();
                String json = response.body().string();

                // 将json存入redis
                // jedis.publish("openai", json);

                //debug
                logger.info(json);

                //Gson 对象
                String content = JsonPath.parse(json).read("$.choices[0].message.content", String.class);
                
                //debug
                logger.info(content);

                String contentWithoutLineBreaks = content.replaceAll("[^a-zA-Z0-9\\u4E00-\\u9FA5]+", "\\\\$0");
                String message = contentWithoutLineBreaks.replaceAll("([\\\\\"'])", "\\\\$0");

                ChatContextHolder.saveChatMessage(chatUser.getQQ(), message);

                return content;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        });
    }
}

