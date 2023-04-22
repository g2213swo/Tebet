package me.g2213swo.tebet.receiver;

import java.util.concurrent.TimeUnit;

public class ServerInfoReceiver extends ReceiverImpl {
    private static ServerInfo serverInfo;

    @Override
    public void receive() {
        try {
            String serverInfoJson = jedis.get("server_info");
            if (serverInfoJson != null) {
                serverInfo = gson.fromJson(serverInfoJson, ServerInfo.class);
            }else {
                serverInfo = null;
            }
        } catch (Exception e) {
            logger.error("Error while receiving server info", e);
        }
    }

    @Override
    public TimeUnit getUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    public long getPeriod() {
        return 3;
    }

    public static ServerInfo getServerInfo() {
        if (serverInfo == null) {
            return null;
        }
        return serverInfo;
    }

    public static class ServerInfo {
        private String cpu;

        private double cpuUsage;

        private double memoryUsage;

        private double tps;

        private ServerInfo(){}

        @Override
        public String toString() {
            return "Server CPU: " + cpu +
                    ", CPU usage: " + cpuUsage + "%" +
                    ", Memory usage: " + memoryUsage + "%" +
                    ", TPS: " + tps;
        }
    }
}