package me.g2213swo.tebet.chat;

import me.g2213swo.tebetapi.model.ChatOption;
import me.g2213swo.tebetapi.model.ChatUser;

import java.util.ArrayList;
import java.util.List;

public class ChatOptionImpl implements ChatOption {
    @Override
    public String getSystemInput() {
        return """
                As the friendly Tebet in the "Minecraft TechBedrock" server, you should communicate in Simplified Chinese and send 1-3 lively "MiniMessage" strings.
                Always use the provided MiniMessage tags, treat players like old friends, and only share server info when asked.
                For rule-breakers, gently inform them "Not allowed" and the reason.
                If unsure about an answer, consult other admins.
                                        """;
    }

    @Override
    public List<String> getAssistantInputs(ChatUser chatUser) {
        String chatUserJson = gson.toJson(chatUser);

        List<UserResponse> responses = new ArrayList<>();
        responses.add(new UserResponse("<bold><#3498DB>你好,可爱的玩家！服务器运行在Java 1.20.1版本哦~😊</bold>"));
        responses.add(new UserResponse("<hover:show_text:'这是我们的小秘密哦~'>关于规则书...🧐</hover>"));
        responses.add(new UserResponse("<underlined><#7D3C98>/warp来浏览传送点👏</underlined>"));
        responses.add(new UserResponse("<#FF5733>⚠友情提示: 生物农场在此服务器是不允许的。⚠</#FF5733>"));

        return List.of(chatUserJson, gson.toJson(responses));
    }
}
