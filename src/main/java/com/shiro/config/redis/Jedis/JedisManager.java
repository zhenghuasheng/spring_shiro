package com.shiro.config.redis.Jedis;

import com.shiro.common.SerializeUtil;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class JedisManager {
    @Autowired
    private JedisPool jedisPool;

    private Jedis jedis;

    private static final Logger logger = LoggerFactory.getLogger(JedisManager.class);

    public static final String REDIS_SHIRO_ALL = "*shiro-session:*";
    @PostConstruct
    private void initJedis() {
        jedis = jedisPool.getResource();
    }

    public byte[] getValueByKey(int dbIndex, byte[] key)  {
        byte[] result = null;
        try {
            jedis.select(dbIndex);
            result = jedis.get(key);
        } catch (Exception e) {
            logger.info("jedis get value key error:{}",e.getMessage());
        }
        return result;
    }

    public void deleteByKey(int dbIndex, byte[] key) {
        try {
            jedis.select(dbIndex);
            jedis.del(key);
        } catch (Exception e) {
            logger.info("jedis delete value error:{}", e.getMessage());
        }
    }

    public void saveValueByKey(int dbIndex, byte[] key, byte[] value, int expireTime) {
        try {
            jedis.select(dbIndex);
            jedis.set(key, value);
            if (expireTime > 0) {
                jedis.expire(key, expireTime);
            }
        } catch (Exception e) {
            logger.info("jedis sava value error:{}", e.getMessage());
        }
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

	/**
	 * 获取所有Session
	 * @param dbIndex
	 * @param redisShiroSession
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Collection<Session> allSession(int dbIndex, String redisShiroSession) {
        Set<Session> sessions = new HashSet<>();
		try {
            jedis.select(dbIndex);
            Set<byte[]> byteKeys = jedis.keys((REDIS_SHIRO_ALL).getBytes());
            if (byteKeys != null && byteKeys.size() > 0) {  
                for (byte[] bs : byteKeys) {  
                	Session obj = SerializeUtil.deserialize(jedis.get(bs), Session.class);
                     if(obj != null){
                    	 sessions.add(obj);  
                     }
                }  
            }  
        } catch (Exception e) {
            logger.info("jedis get all session error:{}", e.getMessage());
        }
        return sessions;
	}
}
