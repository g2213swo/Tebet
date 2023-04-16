package me.g2213swo.tebet.receiver;

import me.g2213swo.tebet.Tebet;
import me.g2213swo.tebet.receiver.info.ServerInfo;

import java.util.concurrent.TimeUnit;

public class ServerInfoReceiver extends ReceiverImpl{
    private final Tebet instance = Tebet.instance;

    private static ServerInfo serverInfo;

    @Override
    public void receive() {
        String serverInfoJson = jedis.get("server_info");
        if (serverInfoJson != null) {
            serverInfo = gson.fromJson(serverInfoJson, ServerInfo.class);
        }
    }

    @Override
    public TimeUnit getUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    public long getDelay() {
        return 10;
    }

    public static ServerInfo getServerInfo() {
        if (serverInfo == null) {
            return null;
        }
        return serverInfo;
    }
}