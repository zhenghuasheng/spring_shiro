package com.shiro.controller;

import com.shiro.config.redis.lock.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhenghuasheng
 * @date 2018/3/22.14:32
 */
@Controller
public class LockController {
    private ExecutorService executor;
    private int count = 1;

    @Autowired
    private RedisLock redisLock;

    private Logger logger = LoggerFactory.getLogger(LockController.class);

    @RequestMapping("/lock")
    @ResponseBody
    public void testLock() {
        executor = Executors.newFixedThreadPool(20);

        for (int i = 0; i< 1000; i++) {
            executor.submit(this::task);
        }
    }

    private void task (){
        redisLock.lock("test_lock", 200000);
        logger.info("old count:"+ count);
        count ++;
        logger.info("new count:" + count);
        redisLock.unlock("test_lock");
    }
}
