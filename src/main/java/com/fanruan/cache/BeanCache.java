package com.fanruan.cache;

import com.fanruan.myJDBC.resultSet.MyResultSet;

import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanCache {

    public String dbName;
    /**
     * cache those instances asked to be established by RPCrequest
     */
    private static Map<String, Object> cache = new ConcurrentHashMap<>();

    public BeanCache(String dbName){
        this.dbName = dbName;
    }

    public <T> T getCachedInstances(String ID, Class<T> clazz){
        try {
            return  clazz.cast(cache.get(ID));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void removeInstances(String ID){
       cache.remove(ID);
    }

    public void cacheInstance(String ID, Object o){
        cache.put(ID, o);
    }

    public boolean containsInstance(String name){
        return cache.containsKey(name);
    }
}
