package sample;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button enterButton;

    @FXML
    private Button regButton;

    @FXML
    void initialize() throws Exception {
        assert enterButton != null : "fx:id=\"enterButton\" was not injected: check your FXML file 'sample.fxml'.";
        assert regButton != null : "fx:id=\"regButton\" was not injected: check your FXML file 'sample.fxml'.";

        enterButton.setOnAction(event -> {
            Client.flag = 1;
            enterButton.getScene().getWindow().hide();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../sample/enter.fxml"));
            Parent root1 = null;
            try {
                root1 = (Parent) fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Messenger");
            stage.setScene(new Scene(root1));
            stage.show();

        });

        regButton.setOnAction(event -> {
            Client.flag = 2;
            regButton.getScene().getWindow().hide();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../sample/reg.fxml"));
            Parent root1 = null;
            try {
                root1 = (Parent) fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Messenger");
            stage.setScene(new Scene(root1));
            stage.show();
        });
    }
}