package com.fanruan.cache;

import io.socket.client.Socket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global cache which has the same life cycle with Dispatcher
 * @author Yichen Dai
 * @date 2022/8/16 16:13
 */
public interface Cache {
    /**
     * stored socket instance by dataBase name
     */
    Map<String, Socket> SOCKET_MAP = new ConcurrentHashMap<>();


    /**
     * Bunch of Map where cached instances like Driver, Connection
     */
    Map<String, BeanCacheImpl> BEAN_CACHE = new ConcurrentHashMap<>();

    /**
     * register the socket of specific nameSpace,
     * the nameSpace is named as "/" + DB name.
     * @param dbName the key of cache entry
     * @param socket the value of cache entry
     */
    void registerSocket(String dbName, Socket socket);

    /**
     * get socket by DB name
     * @param dbName the key of cache entry
     * @return socket of the nameSpace
     */
    Socket getSocket(String dbName);

    /**
     * register the socket of specific nameSpace/db
     * @param dbName the key of cache
     * @param beanCache Map where cached instances like Driver, Connection
     */
    void registerBeanCache(String dbName, BeanCacheImpl beanCache);

    /**
     * get the beanCache of specific nameSpace/db
     * @param dbName the key of cache
     * @return beanCache: Map where cached instances like Driver, Connection
     */
    BeanCacheImpl getBeanCache(String dbName);

}
