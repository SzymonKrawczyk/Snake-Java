/*
    Główna klasa serwera

    Data        | Autor zmian           | Zmiany
    ------------|-----------------------|---------------------------------------------------
    19.05.2020  | Szymon Krawczyk       |   Utworzenie
                |                       |
 */

package Game;

import java.util.ArrayList;

public class MainSerwer {

    public static ArrayList<ClientConnector> ClientConnectorList;
    public static ArrayList<ClientConnector> MatchmakingQueue;
    public static int onlinePlayers = 0;


    public static void main(String[] args) {

        try {
            ClientConnectorWrap clientConnectorWrap = new ClientConnectorWrap();
            clientConnectorWrap.start();

            ClientConnectorList = new ArrayList<ClientConnector>();
            MatchmakingQueue = new ArrayList<ClientConnector>();

        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
