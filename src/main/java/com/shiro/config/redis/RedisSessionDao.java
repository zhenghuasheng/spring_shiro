package com.shiro.config.redis;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author zhenghuasheng
 * @date 2017/11/2.16:34
 */
public class RedisSessionDao extends AbstractSessionDAO {

    @Autowired
    private ShiroSessionRepository shiroSessionRepository;

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        shiroSessionRepository.saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return shiroSessionRepository.getSession(sessionId);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        shiroSessionRepository.saveSession(session);
    }

    @Override
    public void delete(Session session) {
        if (session != null) {
            if (session.getId()  != null) {
                shiroSessionRepository.deleteSession(session.getId());
            }
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
       return shiroSessionRepository.getAllSessions();
    }
}
