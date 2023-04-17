package me.g2213swo.tebet.receiver;

import com.google.gson.Gson;
import me.g2213swo.tebet.Tebet;
import me.g2213swo.tebet.utils.JedisUtil;
import net.mamoe.mirai.utils.MiraiLogger;
import redis.clients.jedis.Jedis;

public abstract class ReceiverImpl implements Receiver{
    protected final MiraiLogger logger = Tebet.INSTANCE.getLogger();
    protected final Jedis jedis = JedisUtil.getJedis();

    protected final Gson gson = new Gson();
    @Override
    public abstract void receive();

}
