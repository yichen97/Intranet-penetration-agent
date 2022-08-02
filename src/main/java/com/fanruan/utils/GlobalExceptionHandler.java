package com.fanruan.utils;


import com.fanruan.AgentStarter;
import com.fanruan.handler.MyDispatcher;
import com.fanruan.pojo.message.SimpleMessage;
import com.google.gson.Gson;
import io.socket.client.Socket;

public class GlobalExceptionHandler {

    private static Gson gson = AgentStarter.gson;

    private static MyDispatcher myDispatcher = AgentStarter.myDispatcher;

    static public void sendException(Exception e, String dbName){
        Socket server = myDispatcher.getSocket(dbName);
        server.emit("ErrorMessage", e.getMessage());
    }

    static public void sendException(String errMsg, String dbName){
        Socket server = myDispatcher.getSocket(dbName);
        server.emit("ErrorMessage", errMsg);
    }
}
