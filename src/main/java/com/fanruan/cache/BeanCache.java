package com.fanruan.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yichen Dai
 */
public class BeanCache {

    public String dbName;
    /**
     * cache those instances asked to be established by RPC request
     */
    final private static Map<String, Object> CACHE = new ConcurrentHashMap<>();

    public BeanCache(String dbName){
        this.dbName = dbName;
    }

    public <T> T getCachedInstances(String ID, Class<T> clazz){
        try {
            return  clazz.cast(CACHE.get(ID));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void removeInstances(String ID){
        CACHE.remove(ID);
    }

    public void cacheInstance(String ID, Object o){
        CACHE.put(ID, o);
    }
}
