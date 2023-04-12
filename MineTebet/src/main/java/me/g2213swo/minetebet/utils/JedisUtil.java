package me.g2213swo.minetebet.utils;
/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class JedisUtil {

    private static JedisPool jedisPool = new JedisPool();

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    public static void initializeRedis() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(30000));
        jedisPoolConfig.setNumTestsPerEvictionRun(-1);
        jedisPoolConfig.setMinEvictableIdleTime(Duration.ofMillis(1800000));
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMinIdle(1);
        jedisPoolConfig.setMaxWait(Duration.ofMillis(30000));
        jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6379);


        List<Jedis> minIdleJedisList = new ArrayList<>(jedisPoolConfig.getMinIdle());
        for (int i = 0; i < jedisPoolConfig.getMinIdle(); i++) {
            Jedis jedis;
            try {
                jedis = jedisPool.getResource();
                minIdleJedisList.add(jedis);
                jedis.ping();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < jedisPoolConfig.getMinIdle(); i++) {
            Jedis jedis;
            try {
                jedis = minIdleJedisList.get(i);
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void closePool() {
        if (jedisPool != null) {
            jedisPool.close();
            jedisPool = null;
        }
    }

    public static boolean isPoolEnabled() {
        return jedisPool != null && !jedisPool.isClosed();
    }
}