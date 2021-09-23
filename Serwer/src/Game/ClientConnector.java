/*
    Klasa reprezentująca klienta w serwerze

    Data        | Autor zmian           | Zmiany
    ------------|-----------------------|---------------------------------------------------
    19.05.2020  | Szymon Krawczyk       |   Utworzenie
                |                       |
    24.05.2020  | Szymon Krawczyk       |   System do powiadamiani przeciwnika o rozłączeniu/poddaniu się
                |                       |
 */

package Game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnector extends Thread {

    private BufferedReader Reader;
    private PrintWriter Writer;
    private Socket socket;

    private Player player;

    ClientConnector (Socket sk) {
        socket = sk;
        System.out.println("Nowy klient!");

        player = new Player(this);
    }


    public Player getPlayer() {
        return player;
    }

    private void reset() {

        player = null;
    }

    @Override
    public void run () {

        try {

            Reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Writer = new PrintWriter(socket.getOutputStream(), true);
            ++MainSerwer.onlinePlayers;
            for (ClientConnector temp : MainSerwer.ClientConnectorList) {
                temp.write("onlinePlayersļ" + MainSerwer.onlinePlayers);
            }

            String message = "";
            //noinspection InfiniteLoopStatement
            while (true) {

                message = Reader.readLine();
                System.out.println(player.getUsername() + ": " + message);

                String[] tokens = message.split("ļ");

                switch (tokens[0]) {

                    case "setUsername": {

                        boolean error = false;
                        for (ClientConnector tempConnector : MainSerwer.ClientConnectorList) {
                            if (tempConnector.getPlayer().getUsername().toLowerCase().equals(tokens[1].toLowerCase())) {
                                error = true;
                                break;
                            }
                        }
                        if (!error) {
                            getPlayer().setUsername(tokens[1]);
                            write("setUsernameļtrue");
                        } else {
                            write("setUsernameļfalse");
                        }

                    }
                    break;

                    case "enterMatchmaking": {

                        if (MainSerwer.MatchmakingQueue.size() <= 0) {

                            MainSerwer.MatchmakingQueue.add(this);

                        } else {

                            ClientConnector temp = MainSerwer.MatchmakingQueue.get(0);
                            MainSerwer.MatchmakingQueue.remove(temp);

                            GameRoom tempGameRoom = new GameRoom(getPlayer(), temp.getPlayer());
                            tempGameRoom.start();

                        }

                    }
                    break;

                    case "leaveMatchmaking": {

                        MainSerwer.MatchmakingQueue.remove(this);
                    }
                    break;

                    case "setDirection": {

                        getPlayer().setNewDirection(tokens[1].charAt(0));
                    }
                    break;

                    case "giveUp": {

                        if (player.getCurrentGame() != null) {
                            player.getCurrentGame().setPlayerGiveUp(player);
                        }
                    }
                    break;

                }

            }
        } catch (Exception err) {
            err.printStackTrace();
            if (player.getCurrentGame() != null) {
                player.getCurrentGame().setPlayerDisconnected(player);
            }
        }
        finally {

            try {
                --MainSerwer.onlinePlayers;
                System.out.println(player.getUsername() + " rozlaczony!");
                MainSerwer.ClientConnectorList.remove(this);
                MainSerwer.MatchmakingQueue.remove(this);
                socket.close();

                for (ClientConnector temp : MainSerwer.ClientConnectorList) {
                    temp.write("onlinePlayersļ" + MainSerwer.onlinePlayers);
                }

            } catch (Exception err) {

                err.printStackTrace();
            }
        }
    }

    public void write(String msg) {
        Writer.println(msg);
    }

}
