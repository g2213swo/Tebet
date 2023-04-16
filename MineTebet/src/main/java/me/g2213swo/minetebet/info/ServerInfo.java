package me.g2213swo.minetebet.info;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerInfo {

    private final String cpu;

    private final double cpuUsage;
    private final ComponentLogger logger = ComponentLogger.logger("MineTebet");

    public ServerInfo() {
        this.cpu = getCpu();
        this.cpuUsage = getCpuUsage();
    }

    private double getCpuUsage() {
        try {
            // 执行命令获取 CPU 信息
            Process process = Runtime.getRuntime().exec("wmic cpu get loadpercentage");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    if (line.contains("LoadPercentage")) {
                        continue;
                    }
                    return Double.parseDouble(line.trim());
                }
            }
        } catch (IOException e) {
            logger.error("获取 CPU 使用率失败");
            e.printStackTrace();
        }
        return 0;
    }


    //获取CPU
    public String getCpu() {
        try {
            // 执行命令获取 CPU 信息
            Process process = Runtime.getRuntime().exec("wmic cpu get name");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    if (line.contains("Name")) {
                        continue;
                    }
                    return line.trim();
                }
            }
        } catch (IOException e) {
            logger.error("获取 CPU 型号失败");
            e.printStackTrace();
        }
        return null;
    }
}
