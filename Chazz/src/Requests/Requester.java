package Requests;

import Chazz.Chazz_Client;
import Controllers.Controller;


public class Requester extends Thread{
    private boolean connection;

    @Override
    public void run() {
        connection = Chazz_Client.socket.isConnected();
        while(connection){
            try{
                Requests.Request req;
                req = new Requests.ReadRequest(Controller.Channel, "true");
                Chazz_Client.out.println(req);
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Client : Requester " + Thread.currentThread().getName() + " closed");
    }

    public void close() {
        connection = false;
    }

}
