package com.fanruan.handler;


import com.fanruan.cache.BeanCache;
import com.fanruan.pojo.message.RpcRequest;
import com.fanruan.utils.DBProperties;
import io.socket.client.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyDispatcher {
    protected static final Logger logger = LogManager.getLogger();

    // stored socket instance by dataBase name
    private static Map<String, Socket> socketCache = new ConcurrentHashMap<>();

    // stored class instance by dataBase name
    private static Map<String, BeanCache> DBCache = new ConcurrentHashMap<>();

    private static Handler handler = new Handler();

    private final static String[] cacheList = new String[]{
            "com.fanruan.myJDBC.driver.MyDriver",
            "com.fanruan.myJDBC.connection.MyConnection",
            "com.fanruan.myJDBC.statement.MyStatement",
            "com.fanruan.myJDBC.statement.MyPreparedStatement",
            "com.fanruan.myJDBC.resultSet.MyResultSet",

    };

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

        Object res = invokeAsRequest(rpcRequest, beanCache);

        if(rpcRequest.isReply()){
            handler.replyWithData(socketCache.get(dbName), rpcRequest, res);
        }else {
            handler.sendOk(socketCache.get(dbName), rpcRequest);
        }
    }

    public Object invokeAsRequest(RpcRequest rpcRequest, BeanCache beanCache) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz = rpcRequest.getServiceClass();
        String methodName = rpcRequest.getMethodName();
        Object[] args = rpcRequest.getArgs();
        Class[] argTypes = rpcRequest.getArgTypes();
        Object calledClassInstance = null;
        // The ID of the rpcRequest could be save as the ID of an instance
        // Because one instance can only been create just once for an unique rpcRequest
        String IDtoCache = rpcRequest.getID();
        String IDtoInvoke = rpcRequest.getIDtoInvoke();

        // 缓存中取类实例，若没有则创建
        String fullName = clazz.getName();
        String className = getClassName(fullName);

        // If BeanCache contains instance, get it; if not, create it.
        if(IDtoInvoke == null){
            try{
                // create
                calledClassInstance = Class.forName(fullName).newInstance();
            }catch (Exception e) {
                e.printStackTrace();
            }
                beanCache.cacheInstance(IDtoCache, calledClassInstance);
        }else{
            calledClassInstance = beanCache.getCachedInstances(IDtoInvoke, clazz);
        }

        Method method;

        try {
            // The primitive variable's type will be automatically packaged when passed as a class object,
            // And an error will be reported when the method with the primitive variable as the parameter is called
            method = clazz.getDeclaredMethod(methodName, argTypes);
        }catch (Exception e){
            for(int i=0; i<argTypes.length; i++){
                Class clz = argTypes[i];
                if(DispatcherHelper.isWraps(clz)){
                    argTypes[i] = DispatcherHelper.castToPrimitiveClass(clz);
                }
            }
            method = clazz.getDeclaredMethod(methodName, argTypes);
        }

        Object res = method.invoke(calledClassInstance, args);

        // Cached some instances need to be invoke later.
        // Some method return null, so determine the value of `res` before referencing it.
        if(res != null){
            String resClassName = res.getClass().getName();
            if(isInCacheList(resClassName)) {
                beanCache.cacheInstance(rpcRequest.getID(), res);
            }
            logger.info("调用" + className + "的" + methodName + " 方法,返回" + res.getClass().getName());
        }else{
            logger.info("调用" + className + "的" + methodName + " 方法,无返回值");
        }
        return res;
    }


    public boolean isInCacheList(String className){
        for(String s : cacheList){
            if(s.equals(className)){
                return true;
            }
        }
        return false;
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
