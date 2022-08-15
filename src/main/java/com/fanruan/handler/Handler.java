package com.fanruan.handler;

import com.fanruan.AgentStarter;
import com.fanruan.pojo.message.RpcRequest;
import com.fanruan.pojo.message.RpcResponse;
import io.socket.client.Socket;

/**
 * @author Yichen Dai
 */
public class Handler {

    public void sendOk(Socket socket, RpcRequest rpcRequest){
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResult(null)
                .setID(rpcRequest.getID())
                .setBinding(rpcRequest.isBinding())
                .setStatus(true);
        byte[] bytes = AgentStarter.SERIALIZER.serialize(rpcResponse);
        socket.emit("RPCResponse", bytes);
    }

    public void sendError(Socket socket, RpcRequest rpcRequest, Exception e){
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResult("Some errors happened when AgentID: " + AgentStarter.AgentID + " "
                + rpcRequest.getMethodName() + " is being invoked!" + "\n"
                + "Error Message: " + e.getMessage()
                + " and check your code")
                .setID(rpcRequest.getID())
                .setBinding(rpcRequest.isBinding())
                .setStatus(false);
        byte[] bytes = AgentStarter.SERIALIZER.serialize(rpcResponse);
        socket.emit("RPCResponse", bytes);
    }

    public void replyWithData(Socket socket, RpcRequest rpcRequest, Object res){
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setID(rpcRequest.getID())
                .setStatus(true)
                .setResult(res);
        byte[] bytes = AgentStarter.SERIALIZER.serialize(rpcResponse);
        socket.emit("RPCResponse", bytes);
    }
}
