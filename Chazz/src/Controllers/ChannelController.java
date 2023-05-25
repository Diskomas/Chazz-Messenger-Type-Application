package Controllers;

import Chazz.Chazz_Client;
import Requests.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

public class ChannelController implements Initializable {
    @FXML
    private Button button_send, Image_send;
    @FXML
    private TextField tf_message;
    @FXML
    private ScrollPane sp_main;
    @FXML
    private VBox vbox_messages;

    @FXML
    public AnchorPane anchorPane;
    public static Listener listener;
    public static Requester requester;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Response to listener
        vbox_messages.heightProperty().addListener((observableValue, number, t1) -> sp_main.setVvalue((Double) t1));

        // read request to fill the chat when user first opens chat window
        Request req;
        req = new ReadRequest(Controller.Channel, "false");
        Chazz_Client.out.println(req);

        // enable listener
        Listener.vBox = vbox_messages;
        listener.start();

        // send messages
        button_send.setOnAction(actionEvent -> {
            addLabel(tf_message.getText(), vbox_messages, Controller.Username, Chazz_Client.dtf.format(Chazz_Client.now) , false, false);
            tf_message.clear(); // clear input window
        });


        Image_send.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("select image");
            Stage stage = (Stage)anchorPane.getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);

            String imageSting = null;
            if(file != null){
                try {
                    FileInputStream input = new FileInputStream(file);
                    byte[] ByteArray = input.readAllBytes();
                    imageSting = Base64.getEncoder().encodeToString(ByteArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            addLabel(imageSting, vbox_messages, Controller.Username, Chazz_Client.dtf.format(Chazz_Client.now) , false, false);
        });

        // start sending read requests
        requester.start();
    }

    // add received or written messages to chat window (for current client)
    public static void addLabel(String message, VBox vBox, String UserName, String Time, boolean leftside, boolean read){
        HBox UsHBox = new HBox();
        Text USText = new Text(UserName + " ("+ Time +")");
        TextFlow USFlow = new TextFlow(USText);
        USText.setFill(Color.color(0.934,0.945,0.996));

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);



        HBox pictureBox = new HBox();



        if(message.length() > 200){  // image is not saved onto the database
            byte[] data = Base64.getDecoder().decode(message);

            ImageView picture = new ImageView();

            InputStream is = new ByteArrayInputStream(data);
            picture.setImage(new Image(is));
            picture.setFitWidth(300);
            picture.setFitHeight(200);
            pictureBox.getChildren().add(picture);
        }
        if(leftside && !UserName.equals(Controller.Username)){ // message is from server
            hBox.setAlignment(Pos.CENTER_LEFT);
            UsHBox.setAlignment(Pos.CENTER_LEFT);
            textFlow.setStyle("-fx-background-color: rgb(233,233,235);" + " -fx-background-radius: 20px;");

        }else{ // message is from client
            hBox.setAlignment(Pos.CENTER_RIGHT);
            UsHBox.setAlignment(Pos.CENTER_RIGHT);
            textFlow.setStyle("-fx-text-fill: white; " + "-fx-background-color: rgb(15,125,242);" + " -fx-background-radius: 20px;");
            text.setFill(Color.color(0.934,0.945,0.996));
            if(!(Controller.Channel).isEmpty() && !read){   // validate if we are sending this message to server
                Request req;
                req = new PostRequest(message, Controller.Channel);
                Chazz_Client.out.println(req);
            }
        }

        textFlow.setPadding(new Insets(5,10,5,10));
        hBox.getChildren().add(textFlow);
        UsHBox.getChildren().add(USFlow);

        if(UserName.equals("server")){ // for "user left / joined the channel"
            USText.setText(message);
            UsHBox.setAlignment(Pos.CENTER);
            UsHBox.setPadding(new Insets(5,5,5,10));
        }

        // add message for display
        Platform.runLater(() -> {
            if(message.length() > 200){vBox.getChildren().add(UsHBox);vBox.getChildren().add(pictureBox);}else{
            vBox.getChildren().add(UsHBox);
            if(!UserName.equals("server")){
                vBox.getChildren().add(hBox);
            }}
        });

    }

}


