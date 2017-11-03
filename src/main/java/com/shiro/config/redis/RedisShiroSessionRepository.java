package com.shiro.config.redis;

import com.shiro.common.SerializeUtil;
import com.shiro.config.redis.Jedis.JedisManager;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author zhenghuasheng
 * @date 2017/11/2.16:40
 */
@Component
public class RedisShiroSessionRepository implements ShiroSessionRepository {

//    @Autowired
//    private LocalRedisCache localRedisCache;
    @Autowired
    private JedisManager jedisManager;

    public static final String REDIS_SHIRO_SESSION = "shiro-session:";
    private static final int DB_INDEX = 1;


    @Override
    public void saveSession(Session session) {
        if (session == null || session.getId() == null) {
            throw new NullPointerException("session is empty");
        }

        byte[] key = SerializeUtil.serialize(REDIS_SHIRO_SESSION + session.getId());
        long expireTime = session.getTimeout()/1000 + 300;
        byte[] value = SerializeUtil.serialize(session);

        jedisManager.saveValueByKey(DB_INDEX, key, value, (int) expireTime);
    }

    @Override
    public void deleteSession(Serializable sessionId) {
        if (sessionId != null) {
            byte[] key = SerializeUtil.serialize(sessionId);
            jedisManager.deleteByKey(DB_INDEX, key);
        }
    }

    @Override
    public Session getSession(Serializable sessionId) {
//        Cache.ValueWrapper valueWrapper = localRedisCache.get(sessionId);
//        if (valueWrapper == null) {
//            return null;
//        }
//       Object o = valueWrapper.get();
//        if (o == null) {
//            return null;
//        }
//        return (SimpleSession)o;

        if (sessionId == null) {
            return null;
        }
        byte[] key = SerializeUtil.serialize(sessionId);
        return SerializeUtil.deserialize(jedisManager.getValueByKey(DB_INDEX, key), Session.class);
    }

    @Override
    public Collection<Session> getAllSessions() {
        return  jedisManager.allSession(DB_INDEX,REDIS_SHIRO_SESSION);
    }
}
