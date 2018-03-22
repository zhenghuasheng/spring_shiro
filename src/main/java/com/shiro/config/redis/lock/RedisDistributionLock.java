package com.shiro.config.redis.lock;

/**
 * @author zhenghuasheng
 * @date 2018/3/22.11:06
 */
public interface RedisDistributionLock {

    /**
     * 加锁成功，返回加锁时间
     *
     * @param lockKey
     * @param waitMaxTime
     * @return
     */
    Long lock(String lockKey, long waitMaxTime);

    /**
     * 解锁， 需要更新加锁时间，判断是否有权限
     *
     * @param lockKey
     */
    void unlock(String lockKey);

    /**
     * 多服务器集群，使用下面的方法，代替System.currentTimeMillis()，获取redis时间，避免多服务的时间不一致问题！！！
     *
     * @return
     */
    long currtTimeForRedis();
}
