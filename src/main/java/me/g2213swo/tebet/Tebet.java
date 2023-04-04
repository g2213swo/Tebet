package me.g2213swo.tebet;

import me.g2213swo.tebet.commands.TebetCommand;
import me.g2213swo.tebet.listener.TebetMessage;
import me.g2213swo.tebet.utils.Config;
import me.g2213swo.tebet.utils.JedisSubPubUtil;
import me.g2213swo.tebet.utils.JedisUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Tebet extends JavaPlugin {

    public static Tebet instance;

    private final MiraiLogger logger = getLogger();

    public Bot getTebetBot() {
        return Bot.Companion.findInstance(1038796824);
    }

    public Tebet() {
        super(new JvmPluginDescriptionBuilder("me.g2213swo.tebet", "0.1.0")
                .name("Tebet")
                .author("g2213swo")
                .build());
    }


    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        reloadPlugin();
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!JedisUtil.isPoolEnabled()) {
            JedisUtil.initializeRedis();
            logger.info("Redis pool is enabled!");
        }

        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            try (Jedis jedis = JedisUtil.getJedis()) {
                logger.info("JedisSub is enabled!");
                // 订阅频道消息
                jedis.subscribe(new JedisSubPubUtil(), "openai");
            } catch (Exception e) {
                logger.warning("异常: " + e.getMessage());
            }
        }, 0L, 5L, TimeUnit.SECONDS);


        GlobalEventChannel.INSTANCE.registerListenerHost(new TebetMessage());

        CommandManager.INSTANCE.registerCommand(TebetCommand.INSTANCE, true);

        getLogger().warning("Tebet is enabled!");
    }

    @Override
    public void onDisable() {
        JedisUtil.closePool();
    }


    public void reloadPlugin(){
        reloadPluginData(Config.INSTANCE);
    }

}
