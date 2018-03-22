package com.shiro.config.redis.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author zhenghuasheng
 * @date 2018/3/22.11:09
 */
@Component
public class RedisLock implements RedisDistributionLock {
    private static final Logger logger = LoggerFactory.getLogger(RedisLock.class);
    /**
     * 加锁超时时间，单位毫秒， 即：加锁时间内执行完操作，如果未完成会有并发现象
     */
    private static final long LOCK_TIMEOUT = 5 * 1000;
    @Autowired
    private RedisTemplate  redisTemplate;

    @Override
    public synchronized Long lock(String lockKey, long waitMaxTime) {
        logger.info(lockKey + "开始执行加锁");
        /**循环获取锁**/
        while (waitMaxTime > 0) {
            //锁时间
            Long lockTimeout = currtTimeForRedis() + LOCK_TIMEOUT + 1;
            if (this.setNX(lockKey, lockTimeout.toString())) {
                logger.info(lockKey + "加锁成功");
                //设置超时时间，释放内存
                redisTemplate.expire(lockKey, LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
                return lockTimeout;
            } else {
                //获取redis里面的时间
                String result = (String) redisTemplate.opsForValue().get(lockKey);

                Long cachedValue = result == null ? null : Long.parseLong(result);
                //锁已经失效
                if (cachedValue != null && cachedValue < System.currentTimeMillis()) {
                    //判断是否为空，不为空时，说明已经失效，如果被其他线程设置了值，则第二个条件判断无法执行
                    //获取上一个锁到期时间，并设置现在的锁到期时间
                    Long oldLockValue = Long.valueOf((String) redisTemplate.opsForValue().getAndSet(lockKey, lockTimeout.toString()));
                    if (oldLockValue != null && oldLockValue.equals(cachedValue)) {
                        //多线程运行时，多个线程签好都到了这里，但只有一个线程的设置值和当前值相同，它才有权利获取锁
                        logger.info(lockKey + "加锁成功");
                        //设置超时间，释放内存
                        redisTemplate.expire(lockKey, LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
                        //返回加锁时间
                        return lockTimeout;
                    }
                }
            }

            try {
                logger.info(lockKey + "等待加锁， 睡眠100毫秒");
                waitMaxTime -= 200;
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public synchronized void unlock(String lockKey) {
        //获取redis中设置的时间
        Object obj =  redisTemplate.opsForValue().get(lockKey);
        if (obj != null) {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public long currtTimeForRedis() {
        return redisTemplate.getConnectionFactory().getConnection().time();
    }


    private boolean setNX(String key, String cacheValue) {

        Object obj = null;
        try {
            obj = redisTemplate.execute((RedisCallback) redisConnection -> {
                //定义序列化方式
                RedisSerializer<String> serializer = new StringRedisSerializer();
                byte[] value = serializer.serialize(cacheValue);
                boolean flag = redisConnection.setNX(key.getBytes(), value);
                redisConnection.close();
                return flag;
            });
        } catch (Exception e) {
            logger.error("setNX redis error, key : {}", key);
        }
        return obj != null;
    }
}
