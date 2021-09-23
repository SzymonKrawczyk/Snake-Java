/*
    Kontroler dla ekranu laczenia

    Data        | Autor zmian           | Zmiany
    ------------|-----------------------|---------------------------------------------------
    19.05.2020  | Szymon Krawczyk       |   Stworzenie
                |                       |

 */

package Game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.*;


public class ConnectViewController {

    @FXML
    public TextField LoginField;

    @FXML
    public TextField SerwerAddressField;

    @FXML
    public Label myErrorLabel;

    @FXML
    public Label ErrorLoginLabel;
    public static Label ErrorLabelLoginStatic;

    @FXML
    public Button ConnectToSerwerButton;

    @FXML
    public Button LoginButton;
    public static Button LoginButtonStatic;

    public void initialize () {

        myErrorLabel.setText("");
        ErrorLoginLabel.setText("");
        ErrorLabelLoginStatic = ErrorLoginLabel;
        LoginButtonStatic = LoginButton;

        // Automatyczne ładowanie adresu serwera z pliku
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("serwerAddress.txt")))) {
            String adres_serwera;
            if ((adres_serwera = reader.readLine()) != null) {
                SerwerAddressField.setText(adres_serwera);
                ConnectToSerwerButton.fire();
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            System.err.println("Nie ma pliku inicjalizacyjnego serwerAddress.txt");;
        }

    }

    public void onConnectToSerwerButtonClick(ActionEvent event) {
        try {
            if (!ConnectToSerwerButton.isDisabled()) {
                ConnectToSerwerButton.setDisable(true);
                if (SerwerAddressField.getText().trim().equals("")) {
                    throw new Exception();
                }

                try (FileWriter writer = new FileWriter("serwerAddress.txt");
                     BufferedWriter bw = new BufferedWriter(writer)) {

                    bw.write(SerwerAddressField.getText());
                } catch (IOException e1) { }

                //Próba łączenia
                Main.serwerConnector.connect(SerwerAddressField.getText().trim());

                myErrorLabel.setTextFill(Main.PassFillColor);
                myErrorLabel.setText("Połączono");
                ConnectToSerwerButton.setDisable(true);
                SerwerAddressField.setEditable(false);
                LoginButton.setDisable(false);
                LoginField.setDisable(false);
                LoginField.setEditable(true);
            }
        }
        catch (Exception err) {
            myErrorLabel.setTextFill(Main.ErrorFillColor);
            myErrorLabel.setText("Błąd połączenia");
            ConnectToSerwerButton.setDisable(false);
            //err.printStackTrace();
        }
    }

    public void onLoginButtonClick(ActionEvent event) {
        if (!LoginButton.isDisabled()) {
            if (LoginField.getText().trim().equals("")) {
                ErrorLoginLabel.setText("Podaj nick!");
                ErrorLoginLabel.setTextFill(Main.ErrorFillColor);
                LoginButton.setDisable(false);
            } else {

                Main.serwerConnector.write("setUsernameļ" + Main.normalizeString(LoginField.getText().trim()));
                Main.Username = Main.normalizeString(LoginField.getText().trim());
                LoginButton.setDisable(true);
            }
        }
    }
}
