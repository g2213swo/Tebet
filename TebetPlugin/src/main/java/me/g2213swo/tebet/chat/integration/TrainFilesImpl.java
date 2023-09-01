package me.g2213swo.tebet.chat.integration;

import me.g2213swo.tebetapi.integration.TrainFiles;

public record TrainFilesImpl(String id, String object, int bytes, int created_at, String filename, String purpose) implements TrainFiles {
    /*
            "id": "file-abc123",
            "object": "file",
            "bytes": 175,
            "created_at": 1613677385,
            "filename": "train.jsonl",
            "purpose": "search"
     */
}
