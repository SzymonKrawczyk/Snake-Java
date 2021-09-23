/*
    Kontroler dla ekranu głównego

    Data        | Autor zmian           | Zmiany
    ------------|-----------------------|---------------------------------------------------
    19.05.2020  | Szymon Krawczyk       |   Stworzenie
                |                       |

 */

package Game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class MainWindowViewController {


    @FXML
    public TextArea gameRulesField;

    @FXML
    private Label sessionWins;

    @FXML
    private Label sessionLoses;

    @FXML
    private Label playersOnline;
    public static Label playersOnlineStatic;


    @FXML
    private Button searchGameButton;

    @FXML
    private Button leaveQueueButton;


    public void initialize() {

        playersOnlineStatic = playersOnline;
        playersOnline.setText("Graczy online: " + Main.onlinePlayers);

        sessionWins.setText("Wygrane: " + Main.wins);
        sessionWins.setTextFill(Main.PassFillColor);
        sessionLoses.setText("Przegrane: " + Main.loses);
        sessionLoses.setTextFill(Main.ErrorFillColor);

        searchGameButton.setDisable(false);
        leaveQueueButton.setDisable(true);
    }

    public void searchGame(ActionEvent event) {

        Main.serwerConnector.write("enterMatchmaking");
        searchGameButton.setDisable(true);
        leaveQueueButton.setDisable(false);
    }

    public void leaveQueue(ActionEvent event) {

        Main.serwerConnector.write("leaveMatchmaking");
        searchGameButton.setDisable(false);
        leaveQueueButton.setDisable(true);
    }
}
