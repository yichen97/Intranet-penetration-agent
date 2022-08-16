package com.fanruan.handler;

import com.fanruan.pojo.message.RpcRequest;
import io.socket.client.Socket;

/**
 * @author Yichen Dai
 * @date 2022/8/16 16:57
 */
public interface ResponseEmitter {

    /**
     * Send success response for the request which not require reply.
     * @param socket socket to send event
     * @param rpcRequest corresponding request
     */
    void sendOk(Socket socket, RpcRequest rpcRequest);

    /**
     * Send failure response when error occur while handle request.
     * @param socket
     * @param rpcRequest
     * @param e Exception happened while handle request.
     */
    void sendError(Socket socket, RpcRequest rpcRequest, Exception e);

    /**
     * Send success response with data asked by request.
     * @param socket
     * @param rpcRequest
     * @param res data required.
     */
    void replyWithData(Socket socket, RpcRequest rpcRequest, Object res);
}
