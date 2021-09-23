/*
    Klasa reprezentująca gracza

    Data        | Autor zmian           | Zmiany
    ------------|-----------------------|---------------------------------------------------
    19.05.2020  | Szymon Krawczyk       |   Utworzenie
                |                       |
 */

package Game;

public class Player {

    private String username;

    private char lastDirection;
    private char newDirection;
    private int score;
    private int positionX;
    private int positionY;

    private GameRoom currentGame;
    private ClientConnector clientConnector;

    public Player(ClientConnector clientConnector) {

        this.username = "<username>";
        this.lastDirection = '0';
        this.newDirection = '0';
        this.score = 0;
        this.positionX = 0;
        this.positionY = 0;

        this.currentGame = null;
        this.clientConnector = clientConnector;
    }

    public ClientConnector getClientConnector() {
        return clientConnector;
    }

    public void setClientConnector(ClientConnector clientConnector) {
        this.clientConnector = clientConnector;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public GameRoom getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(GameRoom currentGame) {

        this.currentGame = currentGame;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(char lastDirection) {
        this.lastDirection = lastDirection;
    }

    public char getNewDirection() {
        return newDirection;
    }

    public void setNewDirection(char currentDirection) {
        this.newDirection = currentDirection;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
