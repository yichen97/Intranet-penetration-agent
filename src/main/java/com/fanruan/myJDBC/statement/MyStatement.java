package com.fanruan.myJDBC.statement;

import com.fanruan.myJDBC.resultSet.MyResultSet;

import java.sql.*;

public class MyStatement implements Statement {

    final private Statement st;


    public MyStatement(Statement statement) {
        this.st = statement;
    }


    //使用与 Service 同名的类保证数据库对应的 JDBC
    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return new MyResultSet(st.executeQuery(sql));
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return st.executeUpdate(sql);
    }

    @Override
    public void close() throws SQLException {
        st.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return st.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        st.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return st.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        st.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        st.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return st.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        st.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        st.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return st.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        st.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        st.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return st.execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return st.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return st.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return st.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        st.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return st.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        st.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return st.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return st.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return st.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        st.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        st.clearBatch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return st.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return st.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return st.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return st.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return st.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return st.executeUpdate(sql, columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return st.executeUpdate(sql, columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return st.execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return st.execute(sql, columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return st.execute(sql, columnNames);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return st.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return st.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        st.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return st.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        st.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return st.isCloseOnCompletion();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return st.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return st.isWrapperFor(iface);
    }
}
