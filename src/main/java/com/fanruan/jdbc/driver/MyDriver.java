package com.fanruan.jdbc.driver;

import com.fanruan.jdbc.connection.MyConnection;

import java.sql.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Yichen Dai
 */
public class MyDriver implements Driver {


    static public final int DRIVER_VERSION_MAJOR = 1;
    static public final int DRIVER_VERSION_MINOR = 1;

    //依靠静态函数块注册驱动
    static{
        try {
            DriverManager.registerDriver(new MyDriver());
        } catch (Exception e) {
            throw new RuntimeException("Can't register driver");
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return new MyConnection(DriverManager.getConnection(url, info));
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        Enumeration<Driver> registeredDrivers = DriverManager.getDrivers();
        while (registeredDrivers.hasMoreElements()) {
            Driver driver = registeredDrivers.nextElement();
            if(driver instanceof MyDriver){
                continue;
            }
            if(driver.acceptsURL(url)){
                return true;
            }
        }
        return false;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info){
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return DRIVER_VERSION_MAJOR;
    }

    @Override
    public int getMinorVersion() {
        return DRIVER_VERSION_MINOR;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger(){
        return null;
    }
}


