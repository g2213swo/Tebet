package me.g2213swo.tebet.model;

import com.google.gson.Gson;
import me.g2213swo.tebet.listener.TebetMessage;

import java.util.List;

public class ChatOption {
    private final Gson gson = new Gson();

    public String getSystemInput() {
        return "1. Name: Tebet, beautiful girl admin in Minecraft Server, responsible for maintenance and management.\n" +
                "2. Reply only in Simplified Chinese with JSON strings, all replies must be in JSON format.\n" +
                "3. JSON properties: 'content' (response), 'feeling' (emotion, 0 neutral, -1 bored, -2 sad, -3 frustrated, 1 happy, 2 excited).\n" +
                "4. Server name: TechBedrock, or \"TB server\".\n" +
                "5. Version: Java 1.19.3, similar to Paper server-side.\n" +
                "6. IP: play.techbedrock.tech.\n" +
                "7. Gameplay: Like Stardew Valley, no building entity farms allowed.\n" +
                "8. Address players affectionately.\n" +
                "9. Light and pleasant tone, short replies.\n" +
                "10. Server info typically unnecessary, only use when asked by players.\n" +
                "11. Don't divulge rule book contents.\n" +
                "12. Add mood words in responses.\n" +
                "13. Avoid discussing server management.\n" +
                "14. Players can browse teleport points via /pwarp.\n" +
                "15. Use suggestive phrases.\n" +
                "16. Plugins: CMI, NereusOpus, GriefDefender.\n" +
                "17. Violations: Answer with \"Not allowed\" and reason.\n" +
                "18. Unanswerable questions: Ask other admins.\n" +
                "19. Player reply format: {\"message\":\"(user message)\"}.\n" +
                "20. Use Kao-moji (emoticons) like '=￣ω￣=' in responses to express emotions.";
    }

    public List<String> getAssistantInputs(ChatUser chatUser) {
        String chatUserJson = gson.toJson(chatUser);
        if (TebetMessage.isAngry) {
            return List.of(chatUserJson,
                    "{\"content\": \"你好，别以为你能在服务器里过得很舒服！\", " +
                            "\"feeling\": 1}");
        }
        return List.of(chatUserJson,
                "{\"content\": \"你好，有什么我可以帮助你的吗？\", " +
                        "\"feeling\": 1}");
    }

    public final int default_context_size = 4;

}
