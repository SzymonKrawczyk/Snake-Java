/*
    Klasa reprezentująca gracza

    Data        | Autor zmian           | Zmiany
    ------------|-----------------------|---------------------------------------------------
    24.05.2020  | Szymon Krawczyk       |   Utworzenie
                |                       |
    29.05.2020  | Szymon Krawczyk       |   Refaktoryzacja niektórych metod w celu uproszczenia zapisu
                |                       |
 */

package Game;

import java.util.Random;

public class GameRoom extends Thread {

    private Player player1;
    private Player player2;
    private Player winningPlayer;
    private Player losingPlayer;

    private boolean gameEnd;
    private int FPS = 4;                    // Ilość operacji na sekundę (kroków / ruchów graczy)
    private final int gameScreenSize = 25;  // Wielkość ekranu gry
    private final int positionOffset = 5;   // Odległość losowych pozycji startowych od krańców ekranu gry
    private final int maxScore = 5;         // Ilość punktów do zwycięstwa
    private int[][] gameScreen;

    public GameRoom(Player player1, Player player2) {

        this.gameEnd = false;
        this.gameScreen = new int[gameScreenSize][gameScreenSize];

        this.player1 = player1;
        this.player2 = player2;
        this.winningPlayer = null;
        this.losingPlayer = null;

        this.player1.setCurrentGame(this);
        this.player2.setCurrentGame(this);

        this.player1.getClientConnector().write("newGameļ" + this.player2.getUsername());
        this.player2.getClientConnector().write("newGameļ" + this.player1.getUsername());
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    @Override
    public void run () {

        try {

            resetScreen();

            while (!gameEnd) {

                movePlayers();

                handleCollisions();

                Thread.sleep(1000 / FPS);
            }

        } catch (Exception error) {

            error.printStackTrace();
        }

        handleGameEnd();
        cleanup();
    }

    private void handleGameEnd() {

        winningPlayer.getClientConnector().write("gameEndļtrue");
        losingPlayer.getClientConnector().write("gameEndļfalse");
    }

    private void movePlayers() throws Exception {

        // Pobranie początkowych pozycji i ustawienie pól na zajęte
        int p1OldX = player1.getPositionX();
        int p1OldY = player1.getPositionY();
        int p2OldX = player2.getPositionX();
        int p2OldY = player2.getPositionY();
        gameScreen[p1OldX][p1OldY] = 1;
        gameScreen[p2OldX][p2OldY] = 2;


        // Pobranie kierunku graczy, weryfikacja poprawności i poruszenie
        setDirection(player1);
        setDirection(player2);

        movePlayer(player1);
        movePlayer(player2);

        // Informacje o ostatnim ruchu dla graczy
        writeToPlayer(player1, "updateScreenļ" + p1OldX + 'ļ' + p1OldY + 'ļ' + player1.getPositionX() + 'ļ' + player1.getPositionY() + 'ļ' + p2OldX + 'ļ' + p2OldY + 'ļ' + player2.getPositionX() + 'ļ' + player2.getPositionY());
        writeToPlayer(player2, "updateScreenļ" + p2OldX + 'ļ' + p2OldY + 'ļ' + player2.getPositionX() + 'ļ' + player2.getPositionY() + 'ļ' + p1OldX + 'ļ' + p1OldY + 'ļ' + player1.getPositionX() + 'ļ' + player1.getPositionY());
    }

    private void setDirection(Player player) {

        // Blokowanie wykonania ruchu 'wstecz'
        switch (player.getNewDirection()) {

            case 'N': if (player.getLastDirection() != 'S') player.setLastDirection(player.getNewDirection()); break;
            case 'E': if (player.getLastDirection() != 'W') player.setLastDirection(player.getNewDirection()); break;
            case 'W': if (player.getLastDirection() != 'E') player.setLastDirection(player.getNewDirection()); break;
            case 'S': if (player.getLastDirection() != 'N') player.setLastDirection(player.getNewDirection()); break;
            default: player.setLastDirection('N'); break;
        }
    }

    private void movePlayer(Player player) {

        // Poruszenie graczem w zależności od kierunku
        switch (player.getLastDirection()) {

            case 'N': player.setPositionY(player.getPositionY()-1); break;
            case 'E': player.setPositionX(player.getPositionX()+1); break;
            case 'W': player.setPositionX(player.getPositionX()-1); break;
            case 'S': player.setPositionY(player.getPositionY()+1); break;
        }
    }

    private void handleCollisions() throws Exception {

        // Sprawdza kolizje (Dopuszcza sytuację, w której dwóch graczy ma kolizję w tym samym kroku (remis rundy))
        boolean player1dead = checkforCollisions(player1);
        boolean player2dead = checkforCollisions(player2);

        if (player1dead && player2dead) {
            Thread.sleep(2000);

            resetScreen();
            return;
        }

        // Jesli kolizja, sprawdz punkty (czy koniec gry)
        if (player1dead) {
            player2.setScore(player2.getScore()+1);
            writeToPlayer(player1, "updateScoreļfalseļ" + player2.getScore());
            writeToPlayer(player2, "updateScoreļtrueļ" + player2.getScore());
            Thread.sleep(2000);
        }
        if (player2dead) {
            player1.setScore(player1.getScore()+1);
            writeToPlayer(player1, "updateScoreļtrueļ" + player1.getScore());
            writeToPlayer(player2, "updateScoreļfalseļ" + player1.getScore());
            Thread.sleep(2000);
        }

        // Jeśli tak, powiadom graczy
        if (player1.getScore() >= maxScore) {

            setPlayer1Win(true);
            Thread.sleep(2000);

        } else if (player2.getScore() >= maxScore) {

            setPlayer1Win(false);
            Thread.sleep(2000);

        // Jeśli nie, nowa runda
        } else if (player1dead || player2dead) {

            resetScreen();
        }
    }

    private boolean checkforCollisions(Player player) {

        // Sprawdzenie kolizji: poza plansza gry, na zajetym polu, gracz w graczu
        if (player.getPositionX() < 0 || player.getPositionX() >= gameScreenSize || player.getPositionY() < 0 || player.getPositionY() >= gameScreenSize || (gameScreen[player.getPositionX()][player.getPositionY()] != 0)) {
            return true;
        }
        if (player1.getPositionX() == player2.getPositionX() && player1.getPositionY() == player2.getPositionY()) {
            return true;
        }
        return false;
    }

    private void resetScreen() throws Exception {

        // Reset na 0
        this.gameScreen = new int[gameScreenSize][gameScreenSize];
        player1.setLastDirection('0');
        player2.setLastDirection('0');
        player1.setNewDirection('0');
        player2.setNewDirection('0');

        // Losowanie pozycji
        Random randomBoolean = new Random();
        int value1 = randomBoolean.nextInt(4);
        int value2 = value1;
        // Gwarancja, że pozycje są różne
        while (value2 == value1) {
            value2 = randomBoolean.nextInt(4);
        }
        switch (value1) {
            case 0: setPlayerPosition(player1, gameScreenSize/2, positionOffset); break;
            case 1: setPlayerPosition(player1, gameScreenSize - (positionOffset+1), gameScreenSize/2); break;
            case 2: setPlayerPosition(player1, gameScreenSize/2, gameScreenSize - (positionOffset+1)); break;
            case 3: setPlayerPosition(player1, positionOffset, gameScreenSize/2); break;
        }
        switch (value2) {
            case 0: setPlayerPosition(player2, gameScreenSize/2, positionOffset); break;
            case 1: setPlayerPosition(player2, gameScreenSize - (positionOffset+1), gameScreenSize/2); break;
            case 2: setPlayerPosition(player2, gameScreenSize/2, gameScreenSize - (positionOffset+1)); break;
            case 3: setPlayerPosition(player2, positionOffset, gameScreenSize/2); break;
        }

        // Wyslanie informacji
        writeToPlayer(player1, "newScreenļ" + gameScreenSize + 'ļ' + player1.getPositionX()  + 'ļ' + player1.getPositionY()  + 'ļ' + player2.getPositionX()  + 'ļ' + player2.getPositionY());
        writeToPlayer(player2, "newScreenļ" + gameScreenSize + 'ļ' + player2.getPositionX()  + 'ļ' + player2.getPositionY()  + 'ļ' + player1.getPositionX()  + 'ļ' + player1.getPositionY());

        // Opóźnienie startu nowej rundy
        Thread.sleep(2000);
    }

    private void setPlayerPosition (Player player, int posX, int posY) {

        player.setPositionX(posX);
        player.setPositionY(posY);
    }


    private void cleanup() {

        player1.setCurrentGame(null);
        player2.setCurrentGame(null);
        player1 = null;
        player2 = null;
        winningPlayer = null;
        losingPlayer = null;
    }

    public void setPlayerDisconnected(Player player) {

        if (player == player1) {

            setPlayer1Win(false);
            player2.getClientConnector().write("popupļPrzeciwnik rozłączony");

        } else {

            setPlayer1Win(true);
            player1.getClientConnector().write("popupļPrzeciwnik rozłączony");
        }
    }

    public void setPlayerGiveUp(Player player) {

        if (player == player1) {

            setPlayer1Win(false);
            player2.getClientConnector().write("popupļPrzeciwnik poddał się");

        } else {

            setPlayer1Win(true);
            player1.getClientConnector().write("popupļPrzeciwnik poddał się");
        }
    }

    private void setPlayer1Win(boolean p1won) {

        gameEnd = true;

        if (p1won) {

            winningPlayer = player1;
            losingPlayer = player2;

        } else {

            winningPlayer = player2;
            losingPlayer = player1;
        }
    }

    private void writeToPlayer(Player player, String msg) throws Exception {

        try {

            player.getClientConnector().write(msg);

        } catch (Exception error) {

            setPlayerDisconnected(player);
            player = null;
            throw error;
        }
    }

}
