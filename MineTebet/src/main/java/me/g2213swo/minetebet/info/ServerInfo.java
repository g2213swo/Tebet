package me.g2213swo.minetebet.info;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.util.Util;

import java.text.DecimalFormat;

public class ServerInfo {

    private final String cpu;
    private final double cpuUsage;

    private final double memoryUsage;

    private final double tps;

    private static final ComponentLogger LOGGER = ComponentLogger.logger("MineTebet");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public ServerInfo() {
        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        this.cpu = processor.getProcessorIdentifier().getName();
        this.cpuUsage = getCpuUsage(processor);
        this.memoryUsage = getMemoryUsage(memory);
        this.tps = getServerTps();
    }

    private static double getCpuUsage(CentralProcessor processor) {
        double systemCpuLoad = processor.getSystemCpuLoad(1000);
        double cpuUsage = systemCpuLoad * 100;
        Util.sleep(1000); // Wait for 1 second
        if (cpuUsage < 0) {
            LOGGER.error("获取 CPU 使用率失败");
            return 0;
        }
        String formatCpuUsage = DECIMAL_FORMAT.format(cpuUsage);
        LOGGER.info(Component.text("CPU 使用率: " + formatCpuUsage).color(TextColor.color(0xe0e1)));
        return Double.parseDouble(formatCpuUsage);
    }

    private double getMemoryUsage(GlobalMemory memory) {
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        double memoryUsage = (double) usedMemory / totalMemory * 100;
        String formatMemoryUsage = DECIMAL_FORMAT.format(memoryUsage);
        LOGGER.info(Component.text("内存使用率: " + formatMemoryUsage).color(TextColor.color(0xe0e1)));
        return Double.parseDouble(formatMemoryUsage);
    }

    private double getServerTps() {
        double[] tps = Bukkit.getTPS();
        String formatTPS = DECIMAL_FORMAT.format(tps[0]);
        LOGGER.info(Component.text("Server TPS: " + formatTPS).color(TextColor.color(0xe0e1)));
        return Double.parseDouble(formatTPS);
    }
}
