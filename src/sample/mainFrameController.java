package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class mainFrameController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<String> chatList;

    @FXML
    private TextField enterField;

    @FXML
    private Button exitID;

    @FXML
    private Button closeDialog;

    @FXML
    private ListView<String> userList;

    @FXML
    private Text nameText;

    @FXML
    private Button sendButton;

    @FXML
    private ListView<String> friendList;

    @FXML
    void initialize() {
        assert friendList != null : "fx:id=\"friendList\" was not injected: check your FXML file 'mainFrame.fxml'.";
        assert userList != null : "fx:id=\"userList\" was not injected: check your FXML file 'mainFrame.fxml'.";
        assert nameText != null : "fx:id=\"nameText\" was not injected: check your FXML file 'mainFrame.fxml'.";
        assert sendButton != null : "fx:id=\"sendButton\" was not injected: check your FXML file 'mainFrame.fxml'.";
        assert chatList != null : "fx:id=\"chatList\" was not injected: check your FXML file 'mainFrame.fxml'.";
        assert enterField != null : "fx:id=\"enterField\" was not injected: check your FXML file 'mainFrame.fxml'.";
        assert exitID != null : "fx:id=\"exitID\" was not injected: check your FXML file 'mainFrame.fxml'.";
        assert closeDialog != null : "fx:id=\"closeDialog\" was not injected: check your FXML file 'mainFrame.fxml'.";


        exitID.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });

        sendButton.setOnAction(event -> {
            Sender.fromUser = enterField.getText();
            try {
                Sender.Sender1();
                temp.add(Client.myLogin + ":\t" + enterField.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        closeDialog.setOnAction(event ->{
            chatList.getItems().clear();
            sendButton.setVisible(false);
            enterField.setVisible(false);
            closeDialog.setVisible(false);
            chatList.setVisible(false);
        });


        userList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                try {
                    for(int i = 0;i < Client.availableUsers.size();i++)
                    {
                        if(userList.getSelectionModel().getSelectedIndex() == i) {
                            Main.client.addFriend(i);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        friendList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sendButton.setVisible(true);
                enterField.setVisible(true);
                closeDialog.setVisible(true);
                chatList.getItems().clear();
                chatList.setVisible(true);
                try {
                    for (int i = 0;i < Client.listOfFriends.size();i++) {
                        if(friendList.getSelectionModel().getSelectedIndex() == i)
                        {
                            i++;
                            BufferedReader br = new BufferedReader(new FileReader
                                    (Client.listOfUserInformation.get(i - 1).IDFriend + ".txt"));
                            String line;

                            while ((line = br.readLine()) != null)
                            {
                                temp.add(Client.decryptorHistory(line));
                            }
                            chatList.getItems().clear();
                            chatList.getItems().addAll(temp);
                            Main.client.writeMessage(Integer.toString(i));
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        nameText.setText(Client.myLogin);
        userList.getItems().clear();
        friendList.getItems().clear();
        userList.getItems().addAll(Client.availableUsers);
        friendList.getItems().addAll(Client.listOfFriends);
        t.start();
    }
    public ArrayList<String> temp = new ArrayList<String>();
    public tick t = new tick();
    public tick2 t2 = new tick2();
    ArrayList<String> tempUser = new ArrayList<String>();
    ArrayList<String> tempFriend = new ArrayList<String>();

    public class tick extends Thread
    {
        @FXML
        public void run()
        {
            while (true) {
                Platform.runLater(()->userList.getItems().clear());
                Platform.runLater(()->friendList.getItems().clear());
                Platform.runLater(()->chatList.getItems().clear());
                tempUser.clear();
                tempFriend.clear();

                int start = 0;
                int end = 0;
                int temp2 = 0;
                for(int i = 0;i < Client.availableUsers.size();i++)
                {
                    for(int j = 0;j < 3;j++)
                    {
                        start = Client.availableUsers.get(i).indexOf("/", temp2);
                        temp2 = start + 1;
                    }
                    end = Client.availableUsers.get(i).indexOf("/", temp2);
                    tempUser.add(Client.availableUsers.get(i).substring(start + 1, end));
                    start = 0;
                    end = 0;
                    temp2 = 0;
                }
                for(int i = 0;i < Client.listOfFriends.size();i++)
                {
                    for(int j = 0;j < 3;j++)
                    {
                        start = Client.listOfFriends.get(i).indexOf("/", temp2);
                        temp2 = start + 1;
                    }
                    end = Client.listOfFriends.get(i).indexOf("/", temp2);
                    tempFriend.add(Client.listOfFriends.get(i).substring(start + 1, end));
                    start = 0;
                    end = 0;
                    temp2 = 0;
                }
                Platform.runLater(()->userList.getItems().addAll(tempUser));
                Platform.runLater(()->chatList.getItems().addAll(temp));
                Platform.runLater(()->friendList.getItems().addAll(tempFriend));
                if (Client.message != null) {
                    temp.add(Client.message);
                    Client.message = null;
                }
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class tick2 extends Thread
    {
        @FXML
        public void run()
        {
            while (true) {
                Sender.fromUser = enterField.getText();
            }
        }
    }
}