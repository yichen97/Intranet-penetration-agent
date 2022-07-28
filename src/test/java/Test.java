import com.fanruan.AgentStarter;
import io.socket.client.Socket;

import java.io.IOException;


public class Test {

    public static void main(String[] args) throws IOException {
        new AgentStarter();
        System.in.read();
        AgentStarter.socket.close();
    }
}
