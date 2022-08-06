package com.fanruan.handler;

import com.fanruan.AgentStarter;
import com.fanruan.cache.BeanCache;
import com.fanruan.exception.ParamException;
import com.fanruan.myJDBC.resultSet.MyResultSet;
import com.fanruan.pojo.message.RpcRequest;
import com.fanruan.pojo.message.RpcResponse;
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

        Object res = invokeAsRequest(rpcRequest, beanCache);

        if(rpcRequest.isReply()){
            handler.replyWithData(socketCache.get(dbName), rpcRequest, res);
        }else{
            handler.sendOk(socketCache.get(dbName), rpcRequest);
        }
    }

    public Object invokeAsRequest(RpcRequest rpcRequest, BeanCache beanCache) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz = rpcRequest.getServiceClass();
        String methodName = rpcRequest.getMethodName();
        Object[] args = rpcRequest.getArgs();
        Class[] argTypes = rpcRequest.getArgTypes();
        Object calledClassInstance = null;

        // 缓存中取类实例，若没有则创建
        String fullName = clazz.getName();
        String className = getClassName(fullName);

        // Result 实例以 SQL 语句作为键缓存
        if("MyResultSet".equals(className)){
            //将附在参数末尾的sql取下来, 将参数恢复
            String sql = (String) args[args.length-1];
            Object[] tmp = new Object[args.length-1];
            for(int i=0; i<tmp.length; i++){
                tmp[i] = args[i];
            }
            args = tmp;
            calledClassInstance = beanCache.getResult(sql);
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
        Method method;
        try {
            // 类型在传输时被自动装包为包装类型，在反射调用以原始变量为参数的方法时会报错
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

        String resClassName = res.getClass().getName();

        // 每个连接可能都有多个ResultSet类， 需要根据sql进行缓存。
        if(isInCacheList(resClassName)) {
            if("com.fanruan.myJDBC.resultSet.MyResultSet".equals(resClassName)){
                beanCache.saveResult((String) args[0], res);
            } else {
                beanCache.saveSingleton(resClassName, res);
            }
        }

        logger.info("调用" + className + "的" + methodName + " 方法生成 "+ res.getClass());

        return res;
    }


    public boolean isInCacheList(String className){
        String[] cacheList = new String[]{
                "com.fanruan.myJDBC.connection.MyConnection",
                "com.fanruan.myJDBC.statement.MyStatement",
                "com.fanruan.myJDBC.resultSet.MyResultSet",
        };

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
