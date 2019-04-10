package com.fadp.wx.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * <p>jedisPool连接池</p>
 * */
public class JedisPoolConnection {

    private static JedisPool jedisPool=null;

    static {
        JedisPoolConfig config=new JedisPoolConfig();
        config.setMaxIdle(3);
        config.setMaxTotal(3);
        config.setMaxWaitMillis(10000);
        config.setTestOnBorrow(true);
        jedisPool=new JedisPool(config,"127.0.0.1",6379);
    }

    /**
     * <p>获取jedis</p>
     * */
    public static Jedis getJedis(){

        return jedisPool.getResource();
    }

}
