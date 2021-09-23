/*
    Główna klasa aplikacji kleinta

    Data        | Autor zmian           | Zmiany
    ------------|-----------------------|---------------------------------------------------
    19.05.2020  | Szymon Krawczyk       |   Stworzenie
                |                       |
 */

package Game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main extends Application {

    public static Paint ErrorFillColor = Color.valueOf("#ff782b");
    public static Paint PassFillColor = Color.valueOf("#006D14");

    public static Stage window;

    public static BufferedReader SerwerReader;
    public static PrintWriter SerwerWriter;
    public static Socket SerwerSocket;
    public static SerwerConnector serwerConnector;

    public static String Username = "";
    public static int wins = 0;
    public static int loses = 0;
    public static int onlinePlayers = 0;



    @Override
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        window.setMinWidth(600);
        window.setMinHeight(400);
        Parent root = FXMLLoader.load(getClass().getResource("ConnectView.fxml"));
        window.setTitle("Game");

        window.setScene(new Scene (root));
        window.show();
    }


    public static void main(String[] args) {
        serwerConnector = new SerwerConnector();

        launch(args);
    }

    // Zapobiega wysyłaniu błędnych poleceń do serwera
    public static String normalizeString (String toProcess) {

        char[] tempTab = toProcess.toCharArray();

        for (int i = 0; i < tempTab.length; i++) {
            switch (tempTab[i]) {
                case 'ļ': tempTab[i] = '_';
                    break;
            }
        }
        return new String(tempTab);
    }
}