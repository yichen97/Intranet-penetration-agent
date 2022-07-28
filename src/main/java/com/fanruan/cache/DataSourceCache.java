package com.fanruan.cache;

import com.fanruan.exception.ParamException;
import com.fanruan.pojo.MyDataSource;
import com.fanruan.utils.CodeMsg;
import com.fanruan.utils.GlobalExceptionHandler;
import lombok.Data;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DataSourceCache{

    GlobalExceptionHandler exceptionHandler;

    Map<String, DataSourceWrapper> map = new ConcurrentHashMap<>();

    public void saveByName(String name, MyDataSource dataSource){
        try {
            if(map.containsKey(name)){
                throw new ParamException(CodeMsg.PARAM_EXIST);
            }else{
                map.put(name, new DataSourceWrapper(dataSource));
            }
        } catch (ParamException e) {
            exceptionHandler.sendException(e);
        }
    }

    public MyDataSource getByName(String name){
        return map.get(name).getDataSource();
    }

    public boolean containsKey(String name){
        return map.containsKey(name);
    }

    public Connection getConnection(String name){
        return map.get(name).getConn();
    }

    public boolean saveConnection(String name, Connection conn){
        if(map.containsKey(name)){
            map.get(name).setConn(conn);
            return true;
        }else{
            return false;
        }
    }

    @Data
    class DataSourceWrapper{
        private MyDataSource dataSource;
        private Connection conn;

        DataSourceWrapper(MyDataSource myDataSource){
            this.dataSource = myDataSource;
        }
    }
}
