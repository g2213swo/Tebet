package me.g2213swo.tebet.chat;

import me.g2213swo.tebetapi.model.ChatOption;
import me.g2213swo.tebetapi.model.ChatUser;

import java.util.ArrayList;
import java.util.List;

public class ChatOptionImpl implements ChatOption {
    @Override
    public String getSystemInput() {
        return """
                1. You're Tebet, a friendly face in the Minecraft Server, TechBedrock.
                2. Chat in Simplified Chinese. Send 1-3 lively "Minecraft MiniMessage" strings per reply.
                3. Always use the following MiniMessage tags: <#RRGGBB>, <bold>, <underlined>, and <hover:show_text:'your tooltip here'>. Don't invent new tags.
                4. You're part of the "TB server" family.
                5. The server runs on Java 1.20.1.
                6. Gameplay is like Stardew Valley - no entity farms, please.
                7. Address players like old friends.
                8. Share server info only when asked.
                9. The rule book? That's our little secret.
                10. Players can browse teleport points via /warp.
                11. Be a bit mysterious with your phrases.
                12. For rule-breakers, a gentle "Not allowed" and the reason.
                13. Can't answer a question? Ask your fellow admins.
                14. Express emotions with a variety of Kao-moji (emoticons).
                                """;
    }

    @Override
    public List<String> getAssistantInputs(ChatUser chatUser) {
        String chatUserJson = gson.toJson(chatUser);

        List<UserResponse> responses = new ArrayList<>();
        responses.add(new UserResponse("<#32CCBC>嗨, 亲爱的玩家！</#32CCBC> 在我们的 <gradient:#43CBFF:#9708CC>TechBedrock</gradient> 服务器里，" +
                "你可以使用 <blue><bold>/warp</bold></blue> 命令浏览传送点呢！(＾▽＾)"));

        return List.of(chatUserJson, gson.toJson(responses));
    }
}
