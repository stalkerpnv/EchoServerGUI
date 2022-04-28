package multiplechatdb.serverside;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

interface AuthService {
    void start();
    void stop();
    String getNickByLoginPassDB(String login, String pass) throws SQLException, ClassNotFoundException;
}

public class BaseAuthService implements AuthService{
    final static String connectionUrl = "jdbc:sqlite:C:\\sqlite\\users.db";
    private static Connection connection;
    private static Statement statement;

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public String  getNickByLoginPassDB(String login, String pass) throws SQLException, ClassNotFoundException{
        connect();
        String query = "SELECT * FROM users";
        String nickReturn = null;
        ResultSet rs= statement.executeQuery(query);
        while (rs.next()) {
            if(rs.getString("login").equals(login) && rs.getString("password").equals(pass)){
                nickReturn = rs.getString("nick");
                System.out.println(rs.getString("nick"));
            }
        }
        closeConnection();
        return nickReturn;
    }
    public void connect() throws SQLException, ClassNotFoundException{
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(connectionUrl);
        statement = connection.createStatement();
    }
    public void closeConnection(){
        try{
            if (statement !=null){
                statement.close();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        try{
            if(connection !=null){
                connection.close();
            }
        }
        catch (SQLException esql){
            esql.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
    }
}
