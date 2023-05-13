package me.g2213swo.tebet.listener;

import me.g2213swo.tebet.model.ChatUser;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class MCServerMessage extends TebetMessageHandler {
    public List<MessageChain> handleGPTMessage(String message) {
        return super.handleOutputs(message);
    }
}
