package me.g2213swo.tebet.receiver;

import com.google.gson.Gson;
import me.g2213swo.tebet.Tebet;
import me.g2213swo.tebet.utils.JedisUtil;
import net.mamoe.mirai.utils.MiraiLogger;
import redis.clients.jedis.Jedis;

public abstract class ReceiverImpl implements Receiver{
    protected final MiraiLogger logger = Tebet.instance.getLogger();
    protected final Jedis jedis = JedisUtil.getJedis();

    protected final Gson gson = new Gson();
    @Override
    public void receive() {
        // TODO Auto-generated method stub
    }
}
