package com.fanruan.cache;

import io.socket.client.Socket;

/**
 * @author Yichen Dai
 * @date 2022/8/16 16:13
 */
public class CacheImpl implements Cache{

    @Override
    public void registerSocket(String dbName, Socket socket){
        SOCKET_MAP.put(dbName, socket);
    }

    @Override
    public Socket getSocket(String dbName){
        Socket socket = SOCKET_MAP.get(dbName);
        if (socket == null){
            throw new RuntimeException("no such DataBase Name");
        }
        return socket;
    }

    @Override
    public void registerBeanCache(String dbName, BeanCacheImpl beanCache) {
        BEAN_CACHE.put(dbName, beanCache);
    }

    @Override
    public BeanCacheImpl getBeanCache(String dbName) {
        BeanCacheImpl beanCache = BEAN_CACHE.get(dbName);
        if(beanCache == null){
            beanCache = new BeanCacheImpl(dbName);
            registerBeanCache(dbName, beanCache);
        }
        return beanCache;
    }
}
