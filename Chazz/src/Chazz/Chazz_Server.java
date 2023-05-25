package Chazz;

import Database.DB_Functions;
import Requests.*;
import Response.ErrorResponse;
import Response.MessageListResponse;
import org.json.simple.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Chazz_Server {

    public static List<Message> Channel_CS = new ArrayList<Message>();
    public static List<Message> Channel_SE = new ArrayList<Message>();
    public static List<Message> Channel_General = new ArrayList<Message>();


    static class ClientHandler extends Thread {
        // shared logical clock

        private String Username;
        private int read_CS;
        private int read_SE;
        private int read_Gen;

        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) throws IOException, SQLException {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            Username = null; read_CS = 0; read_SE = 0; read_Gen = 0;
        }

        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    Object JsonInput = JSONValue.parse(inputLine);
                    Request req;

                    if (Username == null)
                    {
                        System.out.println("--------------------------------");
                        System.out.println("Request : "+ inputLine + " By user " + Username);
                        synchronized (ClientHandler.class) {
                        // Check for login / register request
                        if ((req = LoginRequest.fromJSON(JsonInput)) != null) {
                            if (DB_Functions.Login(((LoginRequest) req).getName(), ((LoginRequest) req).getPassword(), req)) {
                                Username = ((LoginRequest) req).getName();
                                out.println(new Response.SuccessResponse());
                                continue;
                            } else {
                                Username = null;
                            }
                        } else if ((req = RegisterRequest.fromJSON(JsonInput)) != null) {
                            if (!((RegisterRequest) req).getName().isEmpty() && !((RegisterRequest) req).getPassword().isEmpty()) {
                                try {
                                    DB_Functions.SendData("INSERT INTO Users " + "VALUES ('0','000.000.00.0', '" + ((RegisterRequest) req).getName() + "', '" + ((RegisterRequest) req).getPassword() + "', '0', '0', '0')");
                                    out.println(new Response.SuccessResponse());
                                    continue;
                                } catch (Exception ignored) {
                                    System.out.println("Server: Can't insert new user into database");
                                }
                            }
                        }
                        }
                    }
                    else{

                        // check membership
                        if ((req = MembershipRequest.fromJSON(JsonInput)) != null) {
                            System.out.println("--------------------------------");
                            System.out.println("Request : "+ inputLine + " By user " + Username);
                            String Channel = ((MembershipRequest)req).getChannel();
                            synchronized (ClientHandler.class) {
                                if (DB_Functions.AccessToChannel(Username, Channel)) {
                                    System.out.println("Server : User is subscribed");
                                    out.println(new Response.SuccessResponse());
                                    continue;
                                }
                            }
                        }

                        // subscribe / unsubscribe
                        if ((req = Subscribe.fromJSON(JsonInput)) != null) {
                            System.out.println("--------------------------------");
                            System.out.println("Request : "+ inputLine + " By user " + Username);
                            String Channel = ((Subscribe)req).getChannel();
                            String unsub = ((Subscribe)req).getUnsubStatus();
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                            LocalDateTime now = LocalDateTime.now();
                            try{
                                synchronized (ClientHandler.class) {
                                    String status;
                                    if(unsub.equals("No")){
                                        DB_Functions.SendData("INSERT INTO "+ Channel +"_Messages " + "VALUES ('User "+ Username +" has joined the channel', 'server', '"+ dtf.format(now) +"', '0')");
                                        DB_Functions.SendData("update Users set "+Channel+"_Access ='1' where Name ='"+ Username +"'");
                                        status = "joined";
                                    }else{
                                        DB_Functions.SendData("INSERT INTO "+ Channel +"_Messages " + "VALUES ('User "+ Username +" has left the channel', 'server', '"+ dtf.format(now) +"', '0')");
                                        DB_Functions.SendData("update Users set "+Channel+"_Access ='0' where Name ='"+ Username +"'");
                                        status = "left";
                                    }

                                    switch (Channel){
                                        case "CS":
                                        {
                                            Channel_CS.add(new Message("User "+ Username +" has "+ status +" the channel", "server", dtf.format(now)));
                                            break;
                                        }
                                        case "SE":
                                        {
                                            Channel_SE.add(new Message("User "+ Username +" has "+ status +" the channel", "server", dtf.format(now)));
                                            break;
                                        }
                                        case "GE":
                                        {
                                            Channel_General.add(new Message("User "+ Username +" has "+ status +" the channel", "server", dtf.format(now)));
                                            break;
                                        }
                                    }
                                }
                                continue;
                            }catch (Exception ignored){}
                        }


                        // Post
                        if ((req = PostRequest.fromJSON(JsonInput)) != null) {
                            System.out.println("--------------------------------");
                            if(!(inputLine.length() > 255)){
                                System.out.println("Request : "+ inputLine + " By user " + Username);
                            }
                            String message = ((PostRequest)req).getMessage();
                            String channel = ((PostRequest)req).getChannel();
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                            LocalDateTime now = LocalDateTime.now();
                            // synchronized access to the shared message board
                            synchronized (ClientHandler.class) {
                                switch (channel){
                                    case "CS":
                                    {
                                        Channel_CS.add(new Message(message, Username, dtf.format(now)));
                                        read_CS++;
                                        break;
                                    }
                                    case "SE":
                                    {
                                        Channel_SE.add(new Message(message, Username, dtf.format(now)));
                                        read_SE++;
                                        break;
                                    }
                                    case "GE":
                                    {
                                        Channel_General.add(new Message(message, Username, dtf.format(now)));
                                        read_Gen++;
                                        break;
                                    }
                                }
                                if(message.length() < 200){
                                    DB_Functions.SendData("INSERT INTO "+ channel +"_Messages " + "VALUES ('"+ message +"', '"+ Username +"', '"+ dtf.format(now) +"', '0')");
                                }
                                System.out.println("Server : posted \""+ message +"\" to " + channel + "Channel");
                            }
                            continue;
                        }


                        // Read
                        if ((req = ReadRequest.fromJSON(JsonInput)) != null) {
                            String channel = ((ReadRequest)req).getChannel();
                            int toread = 0;
                            List<Message> UnreadMessages = null;

                            // synchronized access to the shared message board
                            synchronized (ClientHandler.class) {
                                switch (channel){
                                    case "CS":
                                    {
                                        if(((ReadRequest)req).getRecent().equals("false")){
                                            if(Channel_CS.size() > 10){
                                                toread = Channel_CS.size() -10;
                                            }
                                            UnreadMessages = Channel_CS.subList(toread, Channel_CS.size());
                                            read_CS = Channel_CS.size();
                                        }else{
                                            UnreadMessages = Channel_CS.subList(read_CS, Channel_CS.size());
                                            read_CS = Channel_CS.size();
                                        }
                                        break;
                                    }
                                    case "SE":
                                    {
                                        if(((ReadRequest)req).getRecent().equals("false")){
                                            if(Channel_SE.size() > 10){
                                                toread = Channel_SE.size() -10;
                                            }
                                            UnreadMessages = Channel_SE.subList(toread, Channel_SE.size());
                                            read_SE = Channel_SE.size();
                                        }else{
                                            UnreadMessages = Channel_SE.subList(read_SE, Channel_SE.size());
                                            read_SE = Channel_SE.size();
                                        }
                                        break;
                                    }
                                    case "GE":
                                    {
                                        if(((ReadRequest)req).getRecent().equals("false")){
                                            if(Channel_General.size() > 10){
                                                toread = Channel_General.size() -10;
                                            }
                                            UnreadMessages = Channel_General.subList(toread, Channel_General.size());
                                            read_Gen = Channel_General.size();
                                        }else{
                                            UnreadMessages = Channel_General.subList(read_Gen, Channel_General.size());
                                            read_Gen = Channel_General.size();
                                        }
                                        break;
                                    }
                                }
                            }
                            // response: list of messages
                            out.println(new MessageListResponse(UnreadMessages));
                            continue;
                        }

                        // Quit request
                        if (QuitRequest.fromJSON(JsonInput) != null) {
                            in.close();
                            out.close();
                            System.out.println("Server : Connection lost with " + Username + " reason (user quited)");
                            return;
                        }
                    }
                    System.out.println("Server : Illegal request response sent");
                    // error response acknowledging illegal request
                    out.println(new ErrorResponse("ILLEGAL REQUEST"));
                }
            } catch (IOException | SQLException e) {
                System.out.println("Server : Connection lost with " + Username + " reason : " + e);
            }
        }
    }


    public static void main(String[] args) throws SQLException {
        DB_Functions.LoadCS();
        DB_Functions.LoadSE();
        DB_Functions.LoadGE();
        try (ServerSocket serverSocket = new ServerSocket(12345))
        {
            while (true) {new ClientHandler(serverSocket.accept()).start();}
        }
        catch (IOException | SQLException e) {
            System.out.println("Exception listening for connection on port " + 12345);
            System.out.println(e.getMessage());
        }
    }
}
