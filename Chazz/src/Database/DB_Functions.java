package Database;

import Chazz.Chazz_Server;
import Chazz.Message;
import Requests.LoginRequest;
import Requests.Request;

import java.sql.*;

public class DB_Functions {


    private static Connection Connection;

    static {
        try {
            // get the connection with the server database
            Connection = DriverManager.getConnection("jdbc:mysql://94.130.73.159/diskomas_JavaProject", "diskomas_Java", "JavaTest123!");
        } catch (SQLException throwable) {
            System.out.println("DB ERROR : " + throwable);
        }
    }

    // nothing else to explain everything is very simple here

    public static void SendData(String Query) throws SQLException {
        Statement stat = Connection.createStatement();
        stat.executeUpdate(Query);
        stat.close();
    }

    public static boolean Login(String username, String password, Request request) throws SQLException {
        Statement stat = Connection.createStatement();
        ResultSet Responce = stat.executeQuery("select * from Users");
        while(Responce.next()){
            if(Responce.getString("Name").equals(((LoginRequest)request).getName()) && Responce.getString("Password").equals(password)){
                System.out.println("Server : "+ username +" logged in!");
                stat.close();
                return true;
            }
        }
        stat.close();
        return false;
    }

    public static void LoadCS() throws SQLException {
        Statement stat = Connection.createStatement();
        ResultSet CS_Response = stat.executeQuery("select * from CS_Messages");
        while(CS_Response.next()){
            Chazz_Server.Channel_CS.add(new Message(CS_Response.getString("Message"), CS_Response.getString("User"), CS_Response.getString("Time")));
        }
        stat.close();
    }

    public static void LoadSE() throws SQLException {
        Statement stat = Connection.createStatement();
        ResultSet SE_Response = stat.executeQuery("select * from SE_Messages");
        while(SE_Response.next()){
            Chazz_Server.Channel_SE.add(new Message(SE_Response.getString("Message"), SE_Response.getString("Username"), SE_Response.getString("Time")));
        }
        stat.close();
    }

    public static void LoadGE() throws SQLException {
        Statement stat = Connection.createStatement();
        ResultSet GE_Response = stat.executeQuery("select * from GE_Messages");
        while(GE_Response.next()){
            Chazz_Server.Channel_General.add(new Message(GE_Response.getString("Message"), GE_Response.getString("Username"), GE_Response.getString("Time")));
        }
        stat.close();
    }

    public static boolean AccessToChannel(String username, String channel) throws SQLException {
        Statement stat = Connection.createStatement();
        ResultSet Response = stat.executeQuery("select * from Users");
        while(Response.next()){
            if(Response.getString("Name").equals(username)){
                boolean response = Response.getString(channel + "_Access").equals("1");
                stat.close();
                return response;
            }
        }
        stat.close();
        return false;
    }
}
