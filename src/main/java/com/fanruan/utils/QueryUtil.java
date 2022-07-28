package com.fanruan.utils;


import com.fanruan.AgentStarter;
import com.fanruan.cache.DataSourceCache;
import com.fanruan.pojo.MyDataSource;

import java.sql.*;


public class QueryUtil {
    private static final String MYSQL_DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String POSTGRESQL_DRIVER_NAME = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
    private static final String ORACLE_DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
    private static final String SQLSERVER_DRIVER_NAME = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
    private static final String DB2_DRIVER_NAME = "com.ibm.db2.jdbc.app.DB2Driver";

    private DataSourceCache cache = AgentStarter.cache;

    public void saveDataSource(MyDataSource dataSource) throws SQLException, ClassNotFoundException {
        String URL = dataSource.getURL();
        String[] strs = URL.split(":");
        String dBName = strs[1].trim();
        cache.saveByName(dBName, dataSource);
        regDataSource(dBName, dataSource);
    }

    public void regDataSource(String dBName, MyDataSource dataSource) throws SQLException, ClassNotFoundException {
        // jdk7 之后增加switch对字符串的支持
        String driverName = "";
        switch (dBName){
            case "oracle":
                driverName = ORACLE_DRIVER_NAME;
                break;
            case "sqlserver":
                driverName = SQLSERVER_DRIVER_NAME;
                break;
            case "db2":
                driverName = DB2_DRIVER_NAME;
                break;
            case  "postgresql":
                driverName = POSTGRESQL_DRIVER_NAME;
                break;
            default:
                driverName = MYSQL_DRIVER_NAME;
                break;
        }
        Class.forName(driverName);
        Connection conn = DriverManager.getConnection(
                dataSource.getURL(),
                dataSource.getUserName(),
                dataSource.getPassWord());

        cache.saveConnection(dBName, conn);
    }

    public Connection getConn(String DBName){
        try{
            if(cache.containsKey(DBName)){
                Connection conn = cache.getConnection(DBName);
                return conn;
            }else{
                throw new RuntimeException("没有注册该数据源");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public ResultSet executeSQL(String name, String sql) throws SQLException {
        Statement stmt = cache.getConnection(name).createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        return rs;
    }

    public String result2Json(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        while(rs.next()) {
            int numColumns = rsmd.getColumnCount();
            for (int i = 1; i <= numColumns; i++) {
                String column = rsmd.getColumnName(i);
                System.out.println(rs.getObject(column));
            }
        }
        return "";
    }
}
