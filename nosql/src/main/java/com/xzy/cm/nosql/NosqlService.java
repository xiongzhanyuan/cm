package com.xzy.cm.nosql;

import java.util.concurrent.TimeUnit;

/**
 * Created by xiongzhanyuan on 2017/2/7.
 */
public interface NosqlService {

    /**
     * @param name
     * @param key
     * @param data
     * @param ttl
     */
    <T> void save(String name, String key, T data, int ttl);

    /**
     * @param name
     * @param key
     */
    void remove(String name, String key);

    /**
     * @param name
     * @param key
     * @param clazz
     * @return
     */
    <T> T find(String name, String key, Class<T> clazz);

    /**
     * 移除单一的数据
     *
     * @param key
     * @return
     */
    boolean remove(String key);

    /**
     * 获取单一的数据
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> T get(String key);

    /**
     * 保存单一的数据
     *
     * @param key
     * @param data
     * @param ttl
     * @param unit
     * @param <T>
     */
    <T> void save(String key, T data, long ttl, TimeUnit unit);

    /**
     * 永久保存数据
     *
     * @param key
     * @param data
     * @param <T>
     */
    <T> void save(String key, T data);
}
