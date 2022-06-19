package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Messenger");
        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.show();
        thread.start();

    }
    public static void main(String[] args) {
        launch(args);
    }

    public static myThread thread = new myThread();
    public static Client client;

    public static class myThread extends Thread
    {
        public void run()
        {
            try {
                client = new Client();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
