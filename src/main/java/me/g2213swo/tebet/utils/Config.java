package me.g2213swo.tebet.utils;

import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JavaAutoSavePluginConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Config extends JavaAutoSavePluginConfig {

    public Config(@NotNull String saveName) {
        super(saveName);
    }

    public static final Config INSTANCE = new Config("config");

    public final Value<String> api_key = value("api-key", "YOUR_API_KEY");

    // 无默认值, 自动创建空 List
    public final Value<List<Long>> admins = typedValue("admins"
            , createKType(List.class, createKType(Long.class)));

}

