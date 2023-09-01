package me.g2213swo.tebet.chat.integration;

import me.g2213swo.tebetapi.integration.TrainModels;

public record TrainModelsImpl(String object, String id, long created_at, String level, String message, String type) implements TrainModels {
}
