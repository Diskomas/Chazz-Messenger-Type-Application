package Controllers;

import Chazz.Chazz_Client;
import Requests.*;
import Response.SuccessResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.json.simple.JSONValue;
import java.io.IOException;

public class Controller {
    public static String Channel;
    public static String Username;
    private Listener listener;
    private Requester requester;


    @FXML
    public Button UnsubBtm;
    @FXML
    public Button JoinBtn;
    @FXML
    public TextField Login_Username, Login_Password;
    @FXML
    public Label Login_Label, Register_Label, SubText, DeniedLabel;
    @FXML
    private BorderPane selec;

    @FXML
    private void CS() throws IOException{
        try{ // kill the listener and requested thread if there is one
            listener.close();
            requester.close();
        }catch (Exception ignored){}
        Channel = "CS";
        if(IsMember()){
            LoadChat("CS");
        }else{
            LoadSubscription();
        }
    }

    @FXML
    private void SE() throws IOException {
        try{ // kill the listener and requested thread if there is one
            requester.close();
            listener.close();
        }catch (Exception ignored){}
        Channel = "SE";
        if(IsMember()){
            LoadChat("SE");
        }else{
            LoadSubscription();
        }
    }

    @FXML
    private void GE() throws IOException {
        try{ // kill the listener and requested thread if there is one
            requester.close();
            listener.close();

        }catch (Exception ignored){}
        Channel = "GE";
        if(IsMember()){
            LoadChat("GE");
        }else{
            LoadSubscription();
        }
    }

    @FXML
    private void LoadChat(String chat) {
        UnsubBtm.setText("Leave " + Channel);

        // PASS LISTENER AND REQUESTER TO CHAT BOX
        listener = new Listener();
        requester = new Requester();
        ChannelController.listener = listener;
        ChannelController.requester = requester;
        // PASS LISTENER AND REQUESTER TO CHAT BOX

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/View/" + chat + ".fxml"));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        selec.setCenter(root);
    }

    private void LoadSubscription() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/View/Subscribe.fxml"));
        }catch (Exception ignored){}
        selec.setCenter(root);
    }

    private boolean IsMember() throws IOException {
        Thread.currentThread().setPriority(1);
        Request req;
        req = new MembershipRequest(Channel);

        // any current listener thread will continue running due to this class having higher priority
        // the listener will steal the request, for that reason a dummy request needs to be sent
        try{
        if(listener.isAlive()){
            Chazz_Client.out.println(req);
        }}catch (Exception ignored){}

        Chazz_Client.out.println(req);

        String response = Chazz_Client.in.readLine();
        Object json = JSONValue.parse(response);

        return (SuccessResponse.fromJSON(json) != null);
    }

    public void GotoSelector(javafx.event.ActionEvent actionEvent) throws IOException {
        String Username, Password;
        if (!(Username = Login_Username.getText()).isEmpty() && !(Password = Login_Password.getText()).isEmpty()){

            // SEND LOGIN REQUEST TO SERVER
            Request req;
            req = new LoginRequest(Username, Password);
            Chazz_Client.out.println(req);
            // SEND LOGIN REQUEST TO SERVER

            // GET LOGIN RESPONSE
            String response = Chazz_Client.in.readLine();
            Object json = JSONValue.parse(response);
            if(Response.SuccessResponse.fromJSON(json) != null){
                Parent root = FXMLLoader.load(getClass().getResource("/View/Chazz_Selector.fxml"));
                Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnHidden(e -> shutdown());
                stage.show();
                Controller.Username = Username;
            }else{
                try {
                    Login_Label.setText("Incorrect login details!");
                } catch (Exception ignored){}
            }
        }else{
            try {
                Login_Label.setText("Please don't leave empty fields!");
            } catch (Exception ignored){}
        }
    }

    @FXML
    void RegisterAccount(ActionEvent actionEvent) throws IOException {
        String Username, Password;
        if (!(Username = Login_Username.getText()).isEmpty() && !(Password = Login_Password.getText()).isEmpty()){

            // SEND REGISTER REQUEST TO SERVER
            Request req;
            req = new RegisterRequest(Username, Password);
            Chazz_Client.out.println(req);
            // SEND REGISTER REQUEST TO SERVER

            String response = Chazz_Client.in.readLine();
            Object json = JSONValue.parse(response);
            if(SuccessResponse.fromJSON(json) != null){
                GotoSelector(actionEvent);
            }else{
                Register_Label.setText("Account creation rejected!");
            }
        }else{
            Register_Label.setText("Please don't leave empty fields!");
        }
    }

    public void CreateUser(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/Register.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void BackTologin(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/Login.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void Subscriber() throws IOException {
        Request req;
        req = new Subscribe(Channel, "No");
        Chazz_Client.out.println(req);
        DeniedLabel.setText("Subscribed to " + Channel);
        SubText.setText("Please refresh the channel to access messages");
        JoinBtn.isDisable();
    }

    public void Unsubscribe(){
        Request req;
        req = new Subscribe(Channel, "Yes");
        Chazz_Client.out.println(req);
        LoadSubscription();
    }

    public void shutdown() {

        try{ // kill the listener and requested thread if there is one
            requester.close(); // requester refuses to close when quiting application
            System.out.println("requested closed");
            listener.close();
            System.out.println("listener closed");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
