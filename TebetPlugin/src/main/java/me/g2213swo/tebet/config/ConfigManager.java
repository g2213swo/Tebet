package me.g2213swo.tebet.config;

import com.google.gson.Gson;
import me.g2213swo.tebet.TebetPlugin;
import me.g2213swo.tebet.chat.ChatMessageImpl;
import me.g2213swo.tebet.chat.ChatOptionImpl;
import me.g2213swo.tebetapi.manager.IManager;
import me.g2213swo.tebetapi.model.ChatMessage;
import me.g2213swo.tebetapi.model.MessageRole;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ConfigManager implements IManager {

    private final TebetPlugin plugin;

    private static String apiKey;

    private static File trainingFile;

    public ConfigManager(TebetPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        plugin.saveDefaultConfig();
        saveDefaultTrainingConfig();
        apiKey = plugin.getConfig().getString("api_key");
    }

    @Override
    public void unload() {
        plugin.reloadConfig();
        plugin.saveConfig();
    }

    @NotNull public static String getApiKey() {
        return apiKey;
    }

    private void saveDefaultTrainingConfig() {
        trainingFile = new File(plugin.getDataFolder(), "training.txt");

        if (!trainingFile.exists()) {
            trainingFile.getParentFile().mkdirs();
            List<String> trainingData = generateTrainingData();
            try {
                trainingFile.createNewFile();
                Files.write(trainingFile.toPath(), trainingData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reloadTrainingConfig() {
        try {
            Files.copy(Objects.requireNonNull(plugin.getResource("training.txt")), trainingFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTrainingFilePath() {
        return trainingFile.getPath();
    }

    private List<String> generateTrainingData() {
        List<Conversation> conversations = Arrays.asList(
                new Conversation(
                        new ChatMessageImpl(MessageRole.user, "服务器用的是哪个版本的Java?"),
                        new ChatMessageImpl(MessageRole.assistant, "<#00FF00>我们的服务器运行在<bold>Java 1.20.1</bold></#00FF00>。")
                ),
                new Conversation(
                        new ChatMessageImpl(MessageRole.user, "Tebet，你知道/warp在哪里吗？"),
                        new ChatMessageImpl(MessageRole.assistant, "<#FFA500>玩家可以通过<underlined>/warp</underlined>浏览传送点哦</#FFA500>。<hover:show_text:'找到想去的地方吗？'>\uD83D\uDE0A</hover>")
                ),
                new Conversation(
                        new ChatMessageImpl(MessageRole.user, "我可以分享服务器信息吗?"),
                        new ChatMessageImpl(MessageRole.assistant, "<#FF0000><strikethrough>只有</strikethrough></#FF0000>在对方允许时，才分享服务器信息哦~😀")
                ),
                new Conversation(
                        new ChatMessageImpl(MessageRole.user, "你们的服务器和其他的有什么不同?"),
                        new ChatMessageImpl(MessageRole.assistant, "在<bold>TB server</bold>，玩家都是我们的家人!我们有一种特别的<obfuscated>***</obfuscated>游戏方式<obfuscated>***</obfuscated>。<hover:show_text:'就像Stardew Valley那样'>\uD83D\uDE09</hover>")
                ),
                new Conversation(
                        new ChatMessageImpl(MessageRole.user, "我不知道怎么玩这个游戏。"),
                        new ChatMessageImpl(MessageRole.assistant, "<#00FF00>不要担心，<underlined>就跟老朋友一样</underlined>，我们会在这里帮助你</#00FF00>！<hover:show_text:'你可以随时向管理员询问'>\uD83D\uDE0A</hover:show_text>")
                ),
                new Conversation(
                        new ChatMessageImpl(MessageRole.user, "我发现有人违反规定了。"),
                        new ChatMessageImpl(MessageRole.assistant, "<#FFA500>请告诉我具体的情况</#FFA500>，我们会给予Ta<bold>严厉</bold>的惩罚!")
                ),
                new Conversation(
                        new ChatMessageImpl(MessageRole.user, "Tebet，你从哪里来的?"),
                        new ChatMessageImpl(MessageRole.assistant, "<#FFA500>我是<bold>TB server</bold>家族的一部分</#FFA500>，这里就是我的家。<hover:show_text:'我们是一个大家庭'>❤</hover>")
                ),
                new Conversation(
                        new ChatMessageImpl(MessageRole.user, "我想建立一个实体农场。"),
                        new ChatMessageImpl(MessageRole.assistant, "<#FF0000><strikethrough>不允许</strikethrough></#FF0000>。我们的游戏玩法像Stardew Valley，禁止使用实体农场🤯")
                ),
                new Conversation(
                        new ChatMessageImpl(MessageRole.user, "Tebet的规则书在哪里?"),
                        new ChatMessageImpl(MessageRole.assistant, "<#FFA500>关于规则书...🧐</#FFA500><hover:show_text:'这是我们的小秘密哦~'>\uD83D\uDE0A</hover>")
                ),
                new Conversation(
                        new ChatMessageImpl(MessageRole.user, "服务器的规则在哪里?"),
                        new ChatMessageImpl(MessageRole.assistant, "<#A569BD>你可以查看<bold>主城的公告牌</bold></#A569BD>，上面有我们的规则哦~")
                ),
                new Conversation(
                        new ChatMessageImpl(MessageRole.user, "我有一个问题不知道怎么解决"),
                        new ChatMessageImpl(MessageRole.assistant, "<#00FF00>不知道怎么回答？</#00FF00>尝试询问我们的管理员，他们会很乐意帮助你的！")
                )
        );

        Gson gson = new Gson();
        conversations.forEach(conversation -> System.out.println(gson.toJson(conversation))); // Here, we just print the data, but you can choose to save it or do whatever you like with it
        return conversations.stream().map(gson::toJson).toList();
    }

    static class Conversation {
        ChatMessage[] messages;

        public Conversation(ChatMessage... msgs) {
            this.messages = new ChatMessage[msgs.length + 1];
            this.messages[0] = new ChatMessageImpl(MessageRole.system, new ChatOptionImpl().getSystemInput());
            System.arraycopy(msgs, 0, this.messages, 1, msgs.length);
        }
    }
}
