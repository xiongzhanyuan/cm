package com.xzy.cm.nosql.redis.impl;

import com.alibaba.fastjson.JSON;
import com.xzy.cm.nosql.NosqlService;
import com.xzy.cm.nosql.redis.config.RedisConfig;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RMapCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by xiongzhanyuan on 2017/2/7.
 */
@Service(value = "service/nosql/redis")
public class NosqlServiceImpl extends RedisConfig implements NosqlService {

    @Override
    public <T> void save(String name, String key, T data, int ttl) {
        RMapCache<String, String> map = redisson.getMapCache(name);
        if (ttl < 0) {
            map.put(key, JSON.toJSONString(data));//存活30分钟
        } else {
            map.put(key, JSON.toJSONString(data), ttl, TimeUnit.MINUTES);//存活30分钟
        }

    }


    @Override
    public <T> void save(String key, T data, long ttl, TimeUnit unit) {
        RBucket<T> ro = redisson.getBucket(key);
        ro.set(data, ttl, unit);
    }

    @Override
    public <T> void save(String key, T data) {
        RBucket<T> ro = redisson.getBucket(key);
        ro.set(data);
    }


    @Override
    public <T> T get(String key) {
        RBucket<T> ro = redisson.getBucket(key);
        return ro.get();
    }


    @Override
    public boolean remove(String key) {
        RBucket ro = redisson.getBucket(key);
        return ro.delete();
    }


    @Override
    public void remove(String name, String key) {
        RMapCache<String, String> map = redisson.getMapCache(name);
        map.remove(key);
    }


    @Override
    public <T> T find(String name, String key, Class<T> clazz) {
        RMapCache<String, String> map = redisson.getMapCache(name);
        String content = map.get(key);
        if (!StringUtils.isBlank(content)) {
            T ret = JSON.parseObject(content, clazz);
            return ret;
        }
        return null;
    }

}
