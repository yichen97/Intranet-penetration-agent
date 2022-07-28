package com.fanruan.utils;


import com.fanruan.AgentStarter;
import com.fanruan.pojo.message.SimpleMessage;
import com.google.gson.Gson;
import io.socket.client.Socket;





public class GlobalExceptionHandler {

    static private Socket server = AgentStarter.socket;

    static Gson gson = AgentStarter.gson;

    static public void sendException(Exception e){
        SimpleMessage msg = new SimpleMessage(e.getMessage());
        server.emit("ErrorMessage", gson.toJson(msg));
    }

    static public void sendException(String errMsg){
        server.emit("ErrorMessage", errMsg);
    }
}
