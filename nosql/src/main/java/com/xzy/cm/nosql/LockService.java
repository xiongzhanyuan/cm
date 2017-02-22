package com.xzy.cm.nosql;

import java.util.concurrent.TimeUnit;

/**
 * Created by xiongzhanyuan on 2017/2/7.
 */
public interface LockService {

    /**
     * 如果锁空闲立即返回 获取失败 一直等待
     *
     * @param key
     */
    boolean lock(String key, long timeout, TimeUnit unit) throws Exception;

    /**
     * @param key
     */
    boolean lock(String key) throws Exception;


    /**
     * 释放锁
     *
     * @param key
     */
    void unLock(String key);


    /**
     * 校验当前有没有锁
     *
     * @param key
     * @return
     */
    Boolean isLocked(String key);

    /**
     * 强制释放锁
     *
     * @param key
     */
    void forceUnlock(String key);
}
