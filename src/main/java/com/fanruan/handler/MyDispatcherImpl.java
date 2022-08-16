package com.fanruan.handler;


import com.fanruan.cache.BeanCacheImpl;
import com.fanruan.pojo.message.RpcRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;



/**
 * @author Yichen Dai
 */
public class MyDispatcherImpl implements Dispatcher{
    protected static final Logger logger = LogManager.getLogger();

    public final static String CLOSE_NAME = "close";

    public MyDispatcherImpl(){}

    @Override
    public void doDispatch(RpcRequest rpcRequest, String dbName) {
        logger.debug("do dispatcher");
        BeanCacheImpl beanCache = CACHE.getBeanCache(dbName);

        Object res = null;
        try {
            res = invokeAsRequest(rpcRequest, beanCache);
        }catch (Exception e){
            RESPONSE_EMITTER_IMPL.sendError(CACHE.getSocket(dbName), rpcRequest, e);
        }

        if(rpcRequest.isReply()){
            RESPONSE_EMITTER_IMPL.replyWithData(CACHE.getSocket(dbName), rpcRequest, res);
        }else {
            RESPONSE_EMITTER_IMPL.sendOk(CACHE.getSocket(dbName), rpcRequest);
        }
    }

    @Override
    public Object invokeAsRequest(RpcRequest rpcRequest, BeanCacheImpl beanCache) throws Exception{
        Class<?> clazz = rpcRequest.getServiceClass();
        String methodName = rpcRequest.getMethodName();
        Object[] args = rpcRequest.getArgs();
        Class<?>[] argTypes = rpcRequest.getArgTypes();
        Object calledClassInstance = null;
        // The ID of the rpcRequest could be save as the ID of an instance
        // Because one instance can only been create just once for an unique rpcRequest
        String IDToCache = rpcRequest.getID();
        String IDToInvoke = rpcRequest.getIDToInvoke();

        String fullName = clazz.getName();
        String className = DispatcherHelper.getClassName(fullName);

        // If BeanCache contains instance, get it; if not, create it.
        if(IDToInvoke == null){
            try{
                // create
                calledClassInstance = Class.forName(fullName).newInstance();
            }catch (Exception e) {
                e.printStackTrace();
            }
                beanCache.cacheInstance(IDToCache, calledClassInstance);
        }else{
            calledClassInstance = beanCache.getCachedInstances(IDToInvoke, clazz);
        }

        Method method;

        try {
            // The primitive variable's type will be automatically packaged when passed as a class object,
            // And an error will be reported when the method with the primitive variable as the parameter is called
            method = clazz.getDeclaredMethod(methodName, argTypes);
        }catch (Exception e){
            for(int i=0; i<argTypes.length; i++){
                Class<?> clz = argTypes[i];
                if(DispatcherHelper.isWraps(clz)){
                    argTypes[i] = DispatcherHelper.castToPrimitiveClass(clz);
                }
            }
            method = clazz.getDeclaredMethod(methodName, argTypes);


        }


        Object res = method.invoke(calledClassInstance, args);

        if(CLOSE_NAME.equals(methodName)){
            beanCache.removeInstances(IDToInvoke);
        }

        // Cached some instances need to be invoke later.
        // Some method return null, so determine the value of `res` before referencing it.
        if(res != null){
            String resClassName = res.getClass().getName();
            if(DispatcherHelper.isInCacheList(resClassName)) {
                beanCache.cacheInstance(rpcRequest.getID(), res);
            }
            logger.info("invoke" + className + "-" + methodName + " and return a instance of" + res.getClass().getName());
        }else{
            logger.info("invoke" + className + "-" + methodName + " and no return value");
        }
        return res;
    }


}
