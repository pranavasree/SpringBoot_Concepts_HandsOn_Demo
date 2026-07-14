import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {

    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/demo_db",
                    "postgres",
                    "0000");

            System.out.println("Connected Successfully!");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}