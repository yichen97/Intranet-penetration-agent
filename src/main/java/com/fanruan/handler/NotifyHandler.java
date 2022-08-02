package com.fanruan.handler;

import com.fanruan.AgentStarter;
import com.fanruan.pojo.message.RpcResponse;
import io.socket.client.Socket;

public class NotifyHandler {


    public static void sendOk(Socket socket){
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResult(null)
                .setStatus(true);
        byte[] bytes = AgentStarter.serializer.serialize(rpcResponse);
        socket.emit("RPCResponse", bytes);
    }
}
