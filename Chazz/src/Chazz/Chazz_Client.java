package Chazz;

import Controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Chazz_Client extends Application {

    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
    public static LocalDateTime now = LocalDateTime.now();
    public static PrintWriter out;
    public static BufferedReader in;
    public static Socket socket;

    @Override
    public void start(Stage Login) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../View/Login.fxml"));
        Login.setTitle("Chazz"); // application name
        Login.setScene(new Scene(root));
        Login.show();

        String hostName = "localhost";
        int portNumber = 12345;

        // establish a connection
        socket = new Socket(hostName, portNumber);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void stop() throws IOException {
        Requests.Request req;
        req = new Requests.QuitRequest();
        out.println(req);

        in.close();
        out.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
