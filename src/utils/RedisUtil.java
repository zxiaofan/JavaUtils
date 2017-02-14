/*
 * 文件名：RedisUtil.java
 * 版权：Copyright 2007-2017 zxiaofan.com. Co. Ltd. All Rights Reserved. 
 * 描述： RedisUtil.java
 * 修改人：zxiaofan
 * 修改时间：2017年2月8日
 * 修改内容：新增
 */
package utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis工具类，基于redis2.9.
 * 
 * @author zxiaofan
 */
public class RedisUtil {

    /**
     * JedisPool.
     */
    private static JedisPool jedisPool = null;

    /**
     * 初始化JedisPool.
     * 
     */
    private static void initJedisPool() {
        // import commons-pool2-2.4.2.jar，否则The type org.apache.commons.pool2.impl.GenericObjectPoolConfig cannot be resolved.
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大连接数, 默认8个,通过pool.getResource()来获取；
        // -1表示不限制；如果pool已经分配了MaxTotal个jedis实例，则此时pool的状态为exhausted(耗尽)。
        config.setMaxTotal(8);
        // 最大空闲(idle)连接数, 默认8个
        config.setMaxIdle(8);
        // borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException，默认-1；
        config.setMaxWaitMillis(1000 * 100);
        // 获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(true);

        // redis有密码
        // jedisPool = new JedisPool(config, JRedisPoolConfig.REDIS_IP, JRedisPoolConfig.REDIS_PORT, 10000, JRedisPoolConfig.REDIS_PASSWORD);

        // redis无密码
        jedisPool = new JedisPool(config, "localhost", 6379);
    };

    /**
     * 获取JedisPool实例.
     * 
     * @return
     */
    public synchronized static JedisPool getPool() {
        if (null == jedisPool) {
            initJedisPool();
        }
        return jedisPool;
    }

    /**
     * 从连接池获取Jedis实例.
     * 
     * @return Jedis Jedis
     * @throws Exception
     *             异常信息
     */
    public synchronized static Jedis getJedis() throws Exception {
        Jedis jedis = null;
        try {
            jedis = getPool().getResource();
        } catch (Exception e) {
            throw new Exception("获取redis连接失败:" + e.getMessage());
        }
        return jedis;
    }

    /**
     * 向redis存入key-value,key已经存在则覆盖
     * 
     * @param key
     *            key
     * @param value
     *            value
     * @return 成功返回OK，失败返回 0
     * @throws Exception
     *             Exception
     */
    public static String set(String key, String value) throws Exception {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.set(key, value);
        } finally {
            close(jedis);
        }
    }

    /**
     * 取值.
     * 
     * @param key
     *            业务key
     * @return String 值
     * @throws Exception
     *             Exception
     */
    public static String get(String key) throws Exception {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.get(key);
        } finally {
            close(jedis);
        }
    }

    /**
     * 关闭Jedis.
     * 
     * @param jedis
     *            jedis
     */
    private static void close(Jedis jedis) {
        if (null != jedis) {
            jedis.close();
        }
    }

    /**
     * 设置过期时间（过期之前可用PERSIST key删除过期时间使其一直可用；ttl key查看过期时间）
     * 
     * @param key
     *            key
     * @param seconds
     *            seconds
     * @throws Exception
     *             Exception
     */
    public void expire(String key, int seconds) throws Exception {
        if (seconds <= 0) {
            return;
        }
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.expire(key, seconds);
        } finally {
            close(jedis);
        }

    }
}
