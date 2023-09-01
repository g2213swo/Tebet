package me.g2213swo.tebet.chat.integration;

import me.g2213swo.tebetapi.integration.Models;

public record ModelsImpl(String id, String object, long created, String owned_by) implements Models {
    /*
    {
        "id": "model-id-0",
            "object": "model",
            "created": 1686935002,
            "owned_by": "organization-owner"
    }
     */
}
