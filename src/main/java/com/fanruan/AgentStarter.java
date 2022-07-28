package com.fanruan;

import com.fanruan.cache.DataSourceCache;
import com.fanruan.exception.ParamException;
import com.fanruan.pojo.MyDataSource;
import com.fanruan.pojo.message.MessageSQL;
import com.fanruan.pojo.message.SimpleMessage;
import com.fanruan.utils.CodeMsg;
import com.fanruan.utils.GlobalExceptionHandler;
import com.fanruan.utils.QueryUtil;
import com.fanruan.utils.ResultSetAdapter;
import com.google.gson.Gson;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


public class AgentStarter {
    protected static final Logger logger = LogManager.getLogger();

    public static final Gson gson = new Gson();

    public static DataSourceCache cache;

    public static Socket socket;

    public static QueryUtil queryUtil;

    public AgentStarter() {
        try {
            loadConfig();
            bootStrap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() throws IOException {
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
            options.reconnectionDelay = Integer.parseInt(props.getProperty("reconnectionDelay"));
            options.timeout = Integer.parseInt(props.getProperty("timeout"));
            String uri = props.getProperty("uri");

            in.close();

            this.socket = IO.socket(URI.create(uri), options);
            this.cache = new DataSourceCache();
            this.queryUtil = new QueryUtil();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void bootStrap(){
        socket.on(Socket.EVENT_CONNECT, objects -> {
            logger.info("client connected!");
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, objects -> {
            logger.info("connect error: " +  objects[0].toString());
        });

        socket.on(Socket.EVENT_DISCONNECT, objects -> {
            for(Object obj : objects){
                logger.info("connection closed: " + obj.toString());
            }
        });

        socket.on("ClientReceive", objects -> {
            logger.info("ClientReceive: " + objects[0].toString());
            SimpleMessage msg = new SimpleMessage("Ready to Register");
            socket.emit("DataSource", gson.toJson(msg));
        });

        // 数据源注册事件
        socket.on("DataSource", objects -> {
            logger.info("DataSource : " + objects[0]);
            MyDataSource dataSource = gson.fromJson(objects[0].toString(), MyDataSource.class);
            try {
                queryUtil.saveDataSource(dataSource);
            }catch (Exception e){
                if(e instanceof ClassNotFoundException){
                    GlobalExceptionHandler.sendException(new ParamException(CodeMsg.DRIVER_NOTFOUND));
                }

            }
            SimpleMessage msg = new SimpleMessage("Ready to Query");
            socket.emit("DataSourceReady", msg);
        });

        // 查询事件
        socket.on("QueryEvent", objects -> {
            MessageSQL messageSQL = gson.fromJson(objects[0].toString(), MessageSQL.class);
            try {
                ResultSet rs = queryUtil.executeSQL(messageSQL.getDBName(), messageSQL.getSQL());
                ResultSetAdapter rsa = new ResultSetAdapter();
                String result = rsa.toJson(rs);
                logger.info("ResultSet: " + result);
                socket.emit("ReturnData",  result);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        socket.connect();
    }

}
