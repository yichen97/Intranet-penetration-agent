package com.fanruan.utils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetAdapter extends TypeAdapter<ResultSet> {

    public static class NotImplemented extends RuntimeException {}
    private static final Gson gson = new Gson();

    @Override
    public ResultSet read(JsonReader reader)
            throws IOException {
        throw new NotImplemented();
    }

    @Override
    public void write(JsonWriter writer, ResultSet rs)
            throws IOException {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int cc = meta.getColumnCount();

            writer.beginArray();
            while (rs.next()) {
                writer.beginObject();
                for (int i = 1; i <= cc; ++i) {
                    writer.name(meta.getColumnName(i));
                    Class<?> type = Class.forName(meta.getColumnClassName(i));
                    gson.toJson(rs.getObject(i), type, writer);
                    //writer.value(rs.getString(i));
                }
                writer.endObject();
            }
            writer.endArray();
        } catch (SQLException e) {
            throw new RuntimeException(e.getClass().getName(), e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getClass().getName(), e);
        }
    }
}
