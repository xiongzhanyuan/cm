package com.xzy.cm.nosql.redis.impl;

import com.xzy.cm.nosql.LockService;
import com.xzy.cm.nosql.redis.config.RedisConfig;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by xiongzhanyuan on 2017/2/13.
 */
@Service(value = "service/lock/redis")
public class LockServiceImpl extends RedisConfig implements LockService{
    @Override
    public boolean lock(String key, long timeout, TimeUnit unit) throws Exception {

        try {
            RLock rLock = redisson.getLock(key);
            boolean flag = rLock.tryLock(timeout, unit);
            return flag;
        } catch (Exception e) {
            //e
        }
        return false;
    }

    @Override
    public boolean lock(String key) throws Exception {
        return lock(key, 30, TimeUnit.MINUTES);
    }

    @Override
    public void unLock(String key) {
        RLock lock = redisson.getLock(key);
        lock.unlock();
    }

    @Override
    public Boolean isLocked(String key) {
        RLock lock = redisson.getLock(key);
        return lock.isLocked();
    }

    @Override
    public void forceUnlock(String key) {
        RLock lock = redisson.getLock(key);
        lock.forceUnlock();
    }
}
