package me.g2213swo.tebet.receiver;

import me.g2213swo.tebet.utils.JedisUtil;
import redis.clients.jedis.Jedis;

public abstract class ReceiverImpl implements Receiver{

    protected final Jedis jedis = JedisUtil.getJedis();

    @Override
    public void receive() {
        // TODO Auto-generated method stub
    }
}
