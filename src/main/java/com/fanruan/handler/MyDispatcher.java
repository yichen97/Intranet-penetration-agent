package com.fanruan.handler;

import com.fanruan.cache.BeanCache;
import com.fanruan.exception.ParamException;
import com.fanruan.pojo.message.RpcRequest;
import com.fanruan.utils.DBProperties;
import io.socket.client.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyDispatcher {
    protected static final Logger logger = LogManager.getLogger();

    // stored socket instance by dataBase name
    private static Map<String, Socket> socketCache = new ConcurrentHashMap<>();

    // stored class instance by dataBase name
    private static Map<String, BeanCache> DBCache = new ConcurrentHashMap<>();

    private static Handler handler = new Handler();

    static {
        DBCache.put(DBProperties.MYSQL, new BeanCache(DBProperties.MYSQL_DRIVER_NAME));
        DBCache.put(DBProperties.POSTGRESQL, new BeanCache(DBProperties.POSTGRESQL_DRIVER_NAME));
        DBCache.put(DBProperties.ORACLE, new BeanCache(DBProperties.ORACLE_DRIVER_NAME));
        DBCache.put(DBProperties.SQLSERVER, new BeanCache(DBProperties.SQLSERVER_DRIVER_NAME));
        DBCache.put(DBProperties.DB2, new BeanCache(DBProperties.DB2_DRIVER_NAME));
    }

    public MyDispatcher(){}

    public void doDispatch(RpcRequest rpcRequest, String dbName) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        logger.debug("do dispatcher");
        BeanCache beanCache = DBCache.get(dbName);
        if(beanCache == null){
            throw new RuntimeException("the class name invoked is wrong");
        }
        invokeAsRequest(rpcRequest, beanCache);
        handler.sendOk(socketCache.get(dbName), rpcRequest);

    }

    public void invokeAsRequest(RpcRequest rpcRequest, BeanCache beanCache) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz = rpcRequest.getServiceClass();
        String methodName = rpcRequest.getMethodName();
        Object[] args = rpcRequest.getArgs();
        Class[] argTypes = rpcRequest.getArgTypes();
        Object calledClassInstance = null;

        // 缓存中取类实例，若没有则创建
        String fullName = clazz.getName();
        String className = getClassName(fullName);

        // Result 实例以 SQL 语句作为键缓存
        if("MyResult".equals(className)){
            logger.info("invoke method in Result");
        }else{
            calledClassInstance = beanCache.getSingleton(fullName, clazz);
            if(calledClassInstance == null){
                try{
                    // 创建被调用类实例
                    calledClassInstance = Class.forName(fullName).newInstance();
                }catch (Exception e){
                    e.printStackTrace();
                }
                beanCache.saveSingleton(fullName, calledClassInstance);
            }
        }

        Method method = clazz.getDeclaredMethod(methodName, argTypes);
        Object res = method.invoke(calledClassInstance, args);
        beanCache.saveSingleton(res.getClass().getName(), res);
        logger.info("调用" + className + "的" + methodName + " 方法生成 "+ res.getClass());
    }



    public String getClassName(String fullyQualifiedClassName){
        String[] arr = fullyQualifiedClassName.split("\\.");
        int n = arr.length;
        if(n == 0) throw new RuntimeException("the class name invoked is wrong");
        return arr[n-1];
    }

    public void registerSocket(String dbName, Socket socket){
        socketCache.put(dbName, socket);
    }

    public Socket getSocket(String dbName){
        Socket socket = socketCache.get(dbName);
        if (socket == null){
            throw new RuntimeException("no such DataBase Name");
        }
        return socket;
    }
}
