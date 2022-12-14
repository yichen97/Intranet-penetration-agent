package com.fanruan;

import com.fanruan.cache.CacheImpl;
import com.fanruan.handler.MyDispatcherImpl;
import com.fanruan.pojo.message.RpcRequest;
import com.fanruan.serializer.KryoSerializer;
import com.fanruan.serializer.Serializer;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * @author YiChen Dai
 */
public class AgentStarter {

    protected static final Logger logger = LogManager.getLogger();

    public final static Serializer SERIALIZER = new KryoSerializer();

    public static MyDispatcherImpl myDispatcherImpl;

    public static String AgentID;

    public AgentStarter(String[] DBs) {
        myDispatcherImpl = new MyDispatcherImpl();
        try {
            createSocket(DBs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSocket(String[] DBs) throws IOException {
        logger.debug("加载配置");
        IO.Options options = new IO.Options();
        try{
            InputStream in = this.getClass().getResourceAsStream("/socket.properties");
            Properties props = new Properties();

            InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
            props.load(inputStreamReader);

            options.transports = new String[]{WebSocket.NAME};
            options.reconnectionAttempts = Integer.parseInt(props.getProperty("reconnectionAttempts"));
            options.query = "agentID=" + props.getProperty("agentID");
            AgentID = props.getProperty("agentID");
            options.reconnectionDelay = Integer.parseInt(props.getProperty("reconnectionDelay"));
            options.timeout = Integer.parseInt(props.getProperty("timeout"));
            String uri = props.getProperty("uri");
            in.close();

            // config the max number of socket
            int MAX_CLIENTS = 10;
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(MAX_CLIENTS * 2);
            dispatcher.setMaxRequestsPerHost(MAX_CLIENTS * 2);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .dispatcher(dispatcher)
                    // important for HTTP long-polling
                    .readTimeout(1, TimeUnit.MINUTES)
                    .build();

            options.callFactory = okHttpClient;
            options.webSocketFactory = okHttpClient;

            Socket defaultSocket = IO.socket(URI.create(uri), options);
            MyDispatcherImpl.CACHE.registerSocket("/", defaultSocket);
            configDefaultSocket(defaultSocket);

            for(String dbName : DBs){
                Socket socket = IO.socket(URI.create(uri + "/" + dbName), options);
                MyDispatcherImpl.CACHE.registerSocket(dbName, socket);
                configSocket(socket, dbName);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void configDefaultSocket(Socket socket) throws IOException {
        socket.on(Socket.EVENT_CONNECT, objects -> {
            logger.info("default-socket connected!");
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, objects -> {
            logger.info("default-socket error: " +  objects[0].toString());
        });

        socket.on(Socket.EVENT_DISCONNECT, objects -> {
            for(Object obj : objects){
                logger.info("default-socket closed: " + obj.toString());
            }
        });
    }

    private void configSocket(Socket socket, String dbName) throws IOException {
        socket.on(Socket.EVENT_CONNECT, objects -> {
            logger.info(dbName + "-socket connected!");
        });

        socket.on(Socket.EVENT_DISCONNECT, objects -> {
            for(Object obj : objects){
                logger.info(dbName + "-socket closed: " + obj.toString());
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, objects -> {
            logger.info(dbName + "-socket error: " +  objects[0].toString());
        });

        socket.on("RPCRequest", objects -> {
            RpcRequest rpcRequest = SERIALIZER.deserialize((byte[]) objects[0], RpcRequest.class);
            logger.debug(dbName + "-RPCRequest: " + rpcRequest.toString());
            try {
                myDispatcherImpl.doDispatch(rpcRequest, dbName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
