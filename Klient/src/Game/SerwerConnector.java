/*
    Klasa odpowiadajaca za połączenie i komunikację z serwerem

    Data        | Autor zmian           | Zmiany
    ------------|-----------------------|---------------------------------------------------
    19.05.2020  | Szymon Krawczyk       |   Stworzenie
                |                       |
    24.05.2020  | Szymon Krawczyk       |   Komenda popup
                |                       |
 */

package Game;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SerwerConnector extends Thread {

    private boolean isConnected;

    public SerwerConnector() {
        isConnected = false;
    }

    public void connect (String address) throws Exception {
        if (!isConnected) {
            try {

                Main.SerwerSocket = new Socket(address, 29090);
                Main.SerwerReader = new BufferedReader(new InputStreamReader(Main.SerwerSocket.getInputStream()));
                Main.SerwerWriter = new PrintWriter(Main.SerwerSocket.getOutputStream(), true);

                start();

                isConnected = true;

            }
            catch (Exception error) {
                reset();
                throw error;
            }
        }
    }

    private void reset(){

        Main.SerwerSocket = null;
        Main.SerwerReader = null;
        Main.SerwerWriter = null;
        isConnected = false;
        Main.Username = "";
    }

    public void run() {
        try {
            String msg = "";

            while (true) {
                msg = Main.SerwerReader.readLine();
                System.out.println("Serwer: " + msg);

                // Dzielenie linii na polecenie + informacje

                String[] tokens = msg.split("ļ");

                switch (tokens[0].trim()) {

                    case "setUsername": {

                        if (Boolean.parseBoolean(tokens[1])) {
                            try {
                                Parent MainWindowView = FXMLLoader.load(getClass().getResource("MainWindowView.fxml"));
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {

                                        Main.window.setScene(new Scene(MainWindowView));
                                        Main.window.show();
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                                System.exit(0);
                            }
                        } else {
                            Main.Username = "";
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    ConnectViewController.ErrorLabelLoginStatic.setText("Nazwa zajęta");
                                    ConnectViewController.ErrorLabelLoginStatic.setTextFill(Main.ErrorFillColor);
                                    ConnectViewController.LoginButtonStatic.setDisable(false);
                                }
                            });
                        }
                    }
                    break;

                    case "newGame": {

                        try {
                            Parent GameWindowView = FXMLLoader.load(getClass().getResource("GameWindowView.fxml"));
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    Main.window.setScene(new Scene(GameWindowView));
                                    Main.window.show();
                                    GameWindowViewController.opponentNameStatic.setText(tokens[1]);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                            System.exit(0);
                        }
                    }
                    break;

                    case "newScreen": {
                        int screenSize = Integer.parseInt(tokens[1]);

                        GameWindowViewController.gameDataStatic = new int[screenSize+2][screenSize+2];
                        GameWindowViewController.gameScreenSizeStatic = screenSize;

                        int playerPosX = Integer.parseInt(tokens[2]);
                        int playerPosY = Integer.parseInt(tokens[3]);

                        GameWindowViewController.gameDataStatic[playerPosX+1][playerPosY+1] = -1;

                        int enemyPosX = Integer.parseInt(tokens[4]);
                        int enemyPosY = Integer.parseInt(tokens[5]);

                        GameWindowViewController.gameDataStatic[enemyPosX+1][enemyPosY+1] = -2;

                        for (int i = 0; i < screenSize+2; i++) {
                            GameWindowViewController.gameDataStatic[0][i] = 7;
                            GameWindowViewController.gameDataStatic[i][0] = 7;
                            GameWindowViewController.gameDataStatic[screenSize+1][i] = 7;
                            GameWindowViewController.gameDataStatic[i][screenSize+1] = 7;
                        }

                        GameWindowViewController.paintCanvas();

                    }
                    break;

                    case "updateScreen": {

                        int playerPosXH = Integer.parseInt(tokens[1]);
                        int playerPosYH = Integer.parseInt(tokens[2]);
                        GameWindowViewController.gameDataStatic[playerPosXH+1][playerPosYH+1] = -1;

                        int playerPosXB = Integer.parseInt(tokens[3]);
                        int playerPosYB = Integer.parseInt(tokens[4]);

                        GameWindowViewController.gameDataStatic[playerPosXB+1][playerPosYB+1] = 1;



                        int enemyPosXH = Integer.parseInt(tokens[5]);
                        int enemyPosYH = Integer.parseInt(tokens[6]);

                        GameWindowViewController.gameDataStatic[enemyPosXH+1][enemyPosYH+1] = -2;

                        int enemyPosXB = Integer.parseInt(tokens[7]);
                        int enemyPosYB = Integer.parseInt(tokens[8]);

                        GameWindowViewController.gameDataStatic[enemyPosXB+1][enemyPosYB+1] = 2;

                        GameWindowViewController.paintCanvas();

                    }
                    break;

                    case "updateScore": {

                        if (Boolean.parseBoolean(tokens[1])) {

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    GameWindowViewController.userScoreStatic.setText(tokens[2]);
                                }
                            });

                        } else {

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    GameWindowViewController.opponentScoreStatic.setText(tokens[2]);
                                }
                            });
                        }
                    }
                    break;

                    case "gameEnd": {

                        if (Boolean.parseBoolean(tokens[1])) {

                            ++Main.wins;

                        } else {

                            ++Main.loses;
                        }

                        Parent MainWindowView = FXMLLoader.load(getClass().getResource("MainWindowView.fxml"));
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                Main.window.setScene(new Scene(MainWindowView));
                                Main.window.show();
                            }
                        });
                    }
                    break;

                    case "popup": {

                        String popupMsg = tokens[1];

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                Alert a = new Alert(Alert.AlertType.NONE,
                                        "Gra zakończona: " + popupMsg, ButtonType.OK);

                                a.show();
                            }
                        });
                    }
                    break;

                    case "onlinePlayers": {

                        Main.onlinePlayers = Integer.parseInt(tokens[1]);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                               if (MainWindowViewController.playersOnlineStatic != null){

                                   MainWindowViewController.playersOnlineStatic.setText("Graczy online: " + Main.onlinePlayers);
                               }
                            }
                        });
                    }
                    break;
                }

            }

        } catch (Exception error) {

            error.printStackTrace();
            reset();
        }
    }

    public void write(String message) {
        try {

            Main.SerwerWriter.println(message);

        } catch (Exception err) {
            reset();
        }
    }
}

/*
do modyfikacji kodu fxml z innych miejsc aplikacji
Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        //update application thread
                                    }
                                });
 */