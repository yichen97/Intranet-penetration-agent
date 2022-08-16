package com.fanruan.cache;

/**
 * Define the operation of the Map where cached instances like Driver, Connection
 * @author Yichen Dai
 * @date 2022/8/16 16:41
 */
public interface BeanCache {

    /**
     * Get cached instances like Driver, Connection
     * @param ID The unique num of a cache instance, It comes from the RPC request ID,
     *           which asked to create the instance.
     * @param clazz The class of the cached instance.
     * @return cached instance of the given type.
     */
    <T> T getCachedInstances(String ID, Class<T> clazz);

    /**
     * Remove a cached instance of given ID.
     * @param ID The unique num of a cache instance, It comes from the RPC request ID,
     *           which asked to create the instance.
     */
    void removeInstances(String ID);

    /**
     * Save the given object
     * @param ID The unique num of a cache instance, It comes from the RPC request ID,
     *           which asked to create the instance.
     * @param o Instance to be cached
     */
    void cacheInstance(String ID, Object o);

}
