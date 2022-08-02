import com.fanruan.AgentStarter;
import com.fanruan.utils.DBProperties;
import io.socket.client.Socket;


import java.io.IOException;


public class Test {

    public static void main(String[] args){
        try{
            testStart();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    static void test(){
    }

    static void testStart() throws IOException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String[] DBs = new String[]{
                DBProperties.MYSQL,
        };
        new AgentStarter(DBs);
        Socket mainSocket = AgentStarter.myDispatcher.getSocket("/");
        mainSocket.connect();
        Socket socket = AgentStarter.myDispatcher.getSocket(DBProperties.MYSQL);
        socket.connect();
        System.in.read();
    }

//    static void testMysql() throws ClassNotFoundException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        try{
//            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "850656");
//            Statement st = conn.createStatement();
//            st.executeQuery("select * from `student`");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}
