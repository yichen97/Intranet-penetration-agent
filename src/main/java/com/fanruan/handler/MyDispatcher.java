package com.fanruan.handler;


import com.fanruan.cache.BeanCache;
import com.fanruan.pojo.message.RpcRequest;
import com.fanruan.utils.DBProperties;
import io.socket.client.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Yichen Dai
 */
public class MyDispatcher {
    protected static final Logger logger = LogManager.getLogger();

    /**
     * stored socket instance by dataBase name
     */
    final private static Map<String, Socket> SOCKET_MAP = new ConcurrentHashMap<>();

    /**
     * stored class instance by dataBase name
     */
    final private static Map<String, BeanCache> DB_CACHE = new ConcurrentHashMap<>();

    final private static String CLOSE_NAME = "close";

    final private static Handler HANDLER = new Handler();

    private final static String[] CACHE_LIST = new String[]{
            "com.fanruan.jdbc.driver.MyDriver",
            "com.fanruan.jdbc.connection.MyConnection",
            "com.fanruan.jdbc.statement.MyStatement",
            "com.fanruan.jdbc.statement.MyPreparedStatement",
            "com.fanruan.jdbc.resultset.MyResultSet",

    };

    static {
        DB_CACHE.put(DBProperties.MYSQL, new BeanCache(DBProperties.MYSQL_DRIVER_NAME));
        DB_CACHE.put(DBProperties.POSTGRESQL, new BeanCache(DBProperties.POSTGRESQL_DRIVER_NAME));
        DB_CACHE.put(DBProperties.ORACLE, new BeanCache(DBProperties.ORACLE_DRIVER_NAME));
        DB_CACHE.put(DBProperties.SQLSERVER, new BeanCache(DBProperties.SQLSERVER_DRIVER_NAME));
        DB_CACHE.put(DBProperties.DB2, new BeanCache(DBProperties.DB2_DRIVER_NAME));
    }

    public MyDispatcher(){}

    public void doDispatch(RpcRequest rpcRequest, String dbName) {
        logger.debug("do dispatcher");
        BeanCache beanCache = DB_CACHE.get(dbName);
        if(beanCache == null){
            throw new RuntimeException("the class name invoked is wrong");
        }

        Object res = null;
        try {
            res = invokeAsRequest(rpcRequest, beanCache);
        }catch (Exception e){
            HANDLER.sendError(SOCKET_MAP.get(dbName), rpcRequest, e);
        }

        if(rpcRequest.isReply()){
            HANDLER.replyWithData(SOCKET_MAP.get(dbName), rpcRequest, res);
        }else {
            HANDLER.sendOk(SOCKET_MAP.get(dbName), rpcRequest);
        }
    }

    public Object invokeAsRequest(RpcRequest rpcRequest, BeanCache beanCache) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = rpcRequest.getServiceClass();
        String methodName = rpcRequest.getMethodName();
        Object[] args = rpcRequest.getArgs();
        Class<?>[] argTypes = rpcRequest.getArgTypes();
        Object calledClassInstance = null;
        // The ID of the rpcRequest could be save as the ID of an instance
        // Because one instance can only been create just once for an unique rpcRequest
        String IDToCache = rpcRequest.getID();
        String IDToInvoke = rpcRequest.getIDToInvoke();

        // 缓存中取类实例，若没有则创建
        String fullName = clazz.getName();
        String className = getClassName(fullName);

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
            if(isInCacheList(resClassName)) {
                beanCache.cacheInstance(rpcRequest.getID(), res);
            }
            logger.info("invoke" + className + "-" + methodName + " and return a instance of" + res.getClass().getName());
        }else{
            logger.info("invoke" + className + "-" + methodName + " and no return value");
        }
        return res;
    }


    public boolean isInCacheList(String className){
        for(String s : CACHE_LIST){
            if(s.equals(className)){
                return true;
            }
        }
        return false;
    }

    public String getClassName(String fullyQualifiedClassName){
        String[] arr = fullyQualifiedClassName.split("\\.");
        int n = arr.length;
        if(n == 0) {
            throw new RuntimeException("the class name invoked is wrong");
        }
        return arr[n-1];
    }

    public void registerSocket(String dbName, Socket socket){
        SOCKET_MAP.put(dbName, socket);
    }

    public Socket getSocket(String dbName){
        Socket socket = SOCKET_MAP.get(dbName);
        if (socket == null){
            throw new RuntimeException("no such DataBase Name");
        }
        return socket;
    }
}
