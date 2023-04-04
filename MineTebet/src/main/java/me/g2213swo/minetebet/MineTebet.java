package me.g2213swo.minetebet;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;

public class MineTebet extends JavaPlugin {

    public static MineTebet INSTANCE;

    private final ComponentLogger logger = ComponentLogger.logger("MineTebet");

    public static MineTebet getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger.info("MineTebet is enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("MineTebet is disabled!");
    }

}
