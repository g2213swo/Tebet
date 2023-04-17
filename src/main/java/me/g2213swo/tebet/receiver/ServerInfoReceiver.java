package me.g2213swo.tebet.receiver;

import java.util.concurrent.TimeUnit;

public class ServerInfoReceiver extends ReceiverImpl {
    private ServerInfo serverInfo;

    @Override
    public void receive() {
        try {
            String serverInfoJson = jedis.get("server_info");
            if (serverInfoJson != null) {
                serverInfo = gson.fromJson(serverInfoJson, ServerInfo.class);
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

    public ServerInfo getServerInfo() {
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

        public String getCpu() {
            return cpu;
        }

        public double getCpuUsage() {
            return cpuUsage;
        }

        public double getMemoryUsage() {
            return memoryUsage;
        }

        public double getTps() {
            return tps;
        }

        @Override
        public String toString() {
            return "Server CPU: " + cpu +
                    ", CPU usage: " + cpuUsage + "%" +
                    ", Memory usage: " + memoryUsage + "%" +
                    ", TPS: " + tps;
        }
    }
}