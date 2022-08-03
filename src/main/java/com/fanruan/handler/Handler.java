package com.fanruan.handler;

import com.fanruan.AgentStarter;
import com.fanruan.pojo.message.RpcRequest;
import com.fanruan.pojo.message.RpcResponse;
import io.socket.client.Socket;

public class Handler {


    public static void sendOk(Socket socket, RpcRequest rpcRequest){
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResult(null)
                .setID(rpcRequest.getID())
                .setStatus(true);
        byte[] bytes = AgentStarter.serializer.serialize(rpcResponse);
        socket.emit("RPCResponse", bytes);
    }
}
