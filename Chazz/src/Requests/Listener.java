package Requests;

import Chazz.Chazz_Client;
import Chazz.Message;
import Controllers.ChannelController;
import Response.MessageListResponse;
import javafx.scene.layout.VBox;
import org.json.simple.JSONValue;

import java.io.IOException;

public class Listener extends Thread{

    public static VBox vBox;
    private boolean connection;

    @Override
    public void run() {
        connection = Chazz_Client.socket.isConnected();
        while(connection){
            try{
                String serverResponse = Chazz_Client.in.readLine();
                Object json = null;
                try {
                    json = JSONValue.parse(serverResponse);
                }catch (Exception ignored){break;} // on quit request listener will try to listen for response
                MessageListResponse resp;
                if ((resp = MessageListResponse.fromJSON(json)) != null) {
                    for (Message m : resp.getMessages()) {
                        ChannelController.addLabel(m.getBody(), vBox, m.getAuthor(), m.getTimestamp(), true, true); // build a new message on my window
                    }
                }else{
                    System.out.println("Client : Listener stolen request (" + serverResponse + ")");
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Client : Listener " + Thread.currentThread().getName() + " closed");
    }

    public void close() {
        connection = false;
    }
}
