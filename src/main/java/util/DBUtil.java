package main.java.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class DBUtil {
    public static Connection getConnectDB(){
        Connection conn = null;
        try {
            String url = "jdbc:postgresql://ec2-54-146-91-153.compute-1.amazonaws.com:5432/d14bl687e2t203?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
            Properties props = new Properties();
            props.setProperty("user","gwhxozzgvzmqjy");
            props.setProperty("password","c02161c4beab36f14bcf542dc6312a17e8c719d13629be101d398a7c4353524b");
            conn = DriverManager.getConnection(url, props);
//            System.out.println("You are now connected to the server");
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
        return conn;
    }
}
