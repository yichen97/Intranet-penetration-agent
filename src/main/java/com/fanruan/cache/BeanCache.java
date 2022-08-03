package com.fanruan.cache;

import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanCache {

    public String dbName;
    /**
     * cache those instances asked to be established by RPCrequest
     */
    private static Map<String, Object> singletonCache = new ConcurrentHashMap<>();
    private static Map<String, Object> resultSetCache = new ConcurrentHashMap<>();

    public BeanCache(String dbName){
        this.dbName = dbName;
    }

    public <T> T getSingleton(String name, Class<T> clazz){
        try {
            return  clazz.cast(singletonCache.get(name));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void saveSingleton(String name, Object o){
        singletonCache.put(name, o);
    }

    public boolean containsSingleton(String name){
        return singletonCache.containsKey(name);
    }

    public ResultSet getResult(String sql){
        return (ResultSet) resultSetCache.get(sql);
    }

    public void saveResult(String sql, ResultSet rs){
        resultSetCache.put(sql, rs);
    }

    public boolean containsResultSet(String sql){
        return resultSetCache.containsKey(sql);
    }
}
