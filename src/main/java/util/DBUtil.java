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
            //db product server
//            String url = "jdbc:postgresql://ec2-54-146-91-153.compute-1.amazonaws.com:5432/d14bl687e2t203?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
//            Properties props = new Properties();
//            props.setProperty("user","gwhxozzgvzmqjy");
//            props.setProperty("password","c02161c4beab36f14bcf542dc6312a17e8c719d13629be101d398a7c4353524b");
//            conn = DriverManager.getConnection(url, props);
            //db local
//            String url = "jdbc:postgresql://localhost:5433/postgres?ssl=true?loggerLevel=TRACE&loggerFile=pgjdbc.log";
//            Properties props = new Properties();
//            props.setProperty("user","postgres");
//            props.setProperty("password","748159263");
//            conn = DriverManager.getConnection(url, props);
            //db dev server
            String url = "jdbc:postgresql://ec2-3-216-92-193.compute-1.amazonaws.com:5432/d8aftg79s0gs6t?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
            Properties props = new Properties();
            props.setProperty("user","cnsgttdjfqnozh");
            props.setProperty("password","8fabab17d81a60c9f05f205e877ca7dcfad4125c56a87425dfdcbedfbf254fd9");
            conn = DriverManager.getConnection(url, props);
            // dev azure
//            String url = "jdbc:postgresql://youthhosteldb-dev.postgres.database.azure.com:5432/youthhosteldb_dev?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
//            Properties props = new Properties();
//            props.setProperty("user","youthhostel@youthhosteldb-dev");
//            props.setProperty("password","KieuTrongKhanh!$&");
//            conn = DriverManager.getConnection(url, props);
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
        return conn;
    }
}
