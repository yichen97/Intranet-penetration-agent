import com.fanruan.AgentStarter;
import com.fanruan.jdbc.driver.MyDriver;
import com.fanruan.utils.DBProperties;
import io.socket.client.Socket;


import java.io.IOException;
import java.sql.*;


public class Test {

    public static void main(String[] args){
        try{
            testStart();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    static void test() throws IOException, ClassNotFoundException, SQLException {
        Class.forName(DBProperties.MYSQL_DRIVER_NAME);
        Class.forName(DBProperties.POSTGRESQL_DRIVER_NAME);
        MyDriver myDriver = new MyDriver();
        System.out.println(myDriver.acceptsURL("jdbc:mysql://127.0.0.1:.3306/test"));
    }


    static void testStart() throws IOException, ClassNotFoundException {
        Class.forName(DBProperties.MYSQL_DRIVER_NAME);
        Class.forName(DBProperties.POSTGRESQL_DRIVER_NAME);
        String[] DBs = new String[]{
                DBProperties.MYSQL,
                DBProperties.POSTGRESQL
        };

        new AgentStarter(DBs);

        Socket mainSocket = AgentStarter.myDispatcherImpl.CACHE.getSocket("/");
        mainSocket.connect();

        Socket socket = AgentStarter.myDispatcherImpl.CACHE.getSocket(DBProperties.MYSQL);
        socket.connect();

        socket = AgentStarter.myDispatcherImpl.CACHE.getSocket(DBProperties.POSTGRESQL);
        socket.connect();

        System.in.read();
    }

    static void testMysql() throws ClassNotFoundException {
        Connection conn = null;
        Statement st = null;
        PreparedStatement pst = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        try {
            Class.forName("com.fanruan.jdbc.driver.MyDriver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "850656");
            st = conn.createStatement();
            rs1 = st.executeQuery("select * from `student`");

            System.out.println("-----------");
            System.out.println("执行查询语句");
            while(rs1.next()) {
                System.out.print(rs1.getInt("student_id") + "  ");
                System.out.print(rs1.getString("student_name")+ "  ");
                System.out.println(rs1.getString("student_address")+ "  ");
            }

            String sql = "select * from `student` where `student_name`= ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, "张三");
            rs2 = pst.executeQuery();

            System.out.println("-----------");
            System.out.println("执行预查询语句");
            while(rs2.next()) {
                System.out.print(rs2.getInt("student_id") + "  ");
                System.out.print(rs2.getString("student_name")+ "  ");
                System.out.println(rs2.getString("student_address")+ "  ");
            }
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
            // 7、关闭对象，回收数据库资源
            if (rs1 != null) { //关闭结果集对象
                try {
                    rs1.close();
            } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs2 != null) { //关闭结果集对象
                try {
                    rs2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (st != null) { // 关闭数据库操作对象
                try {
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) { // 关闭数据库操作对象
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) { // 关闭数据库连接对象
                try {
                    if (!conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
