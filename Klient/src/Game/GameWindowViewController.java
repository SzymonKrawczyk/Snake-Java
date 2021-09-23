/*
    Kontroler dla ekranu głównego gry

    Data        | Autor zmian           | Zmiany
    ------------|-----------------------|---------------------------------------------------
    19.05.2020  | Szymon Krawczyk       |   Stworzenie
                |                       |
    24.05.2020  | Szymon Krawczyk       |   Dodanie funkcjonalności canvas
                |                       |

 */

package Game;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;


public class GameWindowViewController {

    private static final Color userHead       = new Color(0x1B/255.0, 0xB1/255.0, 0x1B/255.0, 1);
    private static final Color userBody       = new Color(0x90/255.0, 0xEE/255.0, 0x90/255.0, 1);

    private static final Color opponentHead   = new Color(0xFF/255.0, 0x8C/255.0, 0x00/255.0, 1);
    private static final Color opponentBody   = new Color(0xFF/255.0, 0xA5/255.0, 0x00/255.0, 1);

    private static final Color background     = new Color(224/255.0, 224/255.0, 224/255.0, 1);
    private static final Color backgroundBack = new Color(10/255.0, 10/255.0, 10/255.0, 1);

    private static double cellWidth = 20.0;

    public static Integer gameScreenSizeStatic;

    @FXML
    private Canvas gameCanvas;
    private static Canvas gameCanvasStatic;


    public static int[][] gameDataStatic;

    @FXML
    private Label opponentScore;
    public static Label opponentScoreStatic;

    @FXML
    private Label userScore;
    public static Label userScoreStatic;

    @FXML
    private Label opponentName;
    public static Label opponentNameStatic;

    @FXML
    private Label userName;

    @FXML
    private Button giveUpButton;


    public void initialize() {

        userName.setText(Main.Username);

        gameCanvasStatic = gameCanvas;

        opponentScoreStatic = opponentScore;
        userScoreStatic = userScore;
        opponentNameStatic = opponentName;

    }

    public static void paintCanvas() {

        if (gameCanvasStatic != null) {

            cellWidth = (gameCanvasStatic.getWidth() / (gameScreenSizeStatic+2));


            GraphicsContext gc = gameCanvasStatic.getGraphicsContext2D();

            gc.setFill(backgroundBack);
            gc.fillRect(0, 0, gameCanvasStatic.getWidth(), gameCanvasStatic.getWidth());
            gc.setFill(background);
            gc.fillRect(cellWidth, cellWidth, gameCanvasStatic.getWidth()-2*cellWidth, gameCanvasStatic.getWidth()-2*cellWidth);

            Color currentColor = null;

            for (int width = 0; width < gameScreenSizeStatic+2; ++width) {
                for (int height = 0; height < gameScreenSizeStatic+2; ++height) {



                    switch (gameDataStatic[width][height]) {

                        case -1: currentColor = userHead;     break;
                        case  1: currentColor = userBody;     break;
                        case -2: currentColor = opponentHead; break;
                        case  2: currentColor = opponentBody; break;
                        case  7: currentColor = backgroundBack; break;
                        default: currentColor = background;   break;
                    }
                    gc.setFill(currentColor);
                    gc.fillRect((width*cellWidth)+1, (height*cellWidth)+1, cellWidth-2, cellWidth-2);
                }
            }
        }
    }

    public void giveUp (ActionEvent event) {

        Main.serwerConnector.write("giveUp");
        giveUpButton.setDisable(true);
    }

    public void buttonUpAction (ActionEvent event) {

        Main.serwerConnector.write("setDirectionļN");
    }

    public void buttonRightAction (ActionEvent event) {

        Main.serwerConnector.write("setDirectionļE");
    }

    public void buttonLeftAction (ActionEvent event) {

        Main.serwerConnector.write("setDirectionļW");
    }

    public void buttonDownAction (ActionEvent event) {

        Main.serwerConnector.write("setDirectionļS");
    }

}
