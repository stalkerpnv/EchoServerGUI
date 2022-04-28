package multiplechatdb.serverside;

import org.sqlite.SQLiteException;

import java.sql.*;

public class DBConnect {
    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\sqlite\\users.db");
            if(connection!=null){
                System.out.println("Connected");
            }
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM users";
            ResultSet rs = statement.executeQuery(query);



            while (rs.next()) {
                System.out.println(rs.getString("id") + " " +
                                   rs.getString("login") + " " +
                                   rs.getString("password")+ " " +
                                   rs.getString("nick"));
            }
        }
        catch (SQLiteException|ClassNotFoundException e){
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
