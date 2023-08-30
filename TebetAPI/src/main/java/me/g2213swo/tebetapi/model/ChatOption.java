package me.g2213swo.tebetapi.model;

import com.google.gson.Gson;

import java.util.List;

public interface ChatOption {
    Gson gson = new Gson();

    String getSystemInput();

    List<String> getAssistantInputs(ChatUser chatUser);

    int default_context_size = 8;
}