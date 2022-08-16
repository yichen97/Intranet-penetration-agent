package com.fanruan.handler;

import com.fanruan.cache.BeanCacheImpl;
import com.fanruan.cache.CacheImpl;
import com.fanruan.pojo.message.RpcRequest;

/**
 * Dispatch and process received requests
 * @author Yichen Dai
 * @date 2022/8/16 14:56
 */
public interface Dispatcher {
    CacheImpl CACHE = new CacheImpl();

    ResponseEmitterImpl RESPONSE_EMITTER_IMPL = new ResponseEmitterImpl();
    /**
     *
     * @param rpcRequest
     * @param dbName
     */
    void doDispatch(RpcRequest rpcRequest, String dbName);

    /**
     *
     * @param rpcRequest
     * @param beanCache
     * @return
     * @throws Throwable
     */
    Object invokeAsRequest(RpcRequest rpcRequest, BeanCacheImpl beanCache) throws Throwable;



}
