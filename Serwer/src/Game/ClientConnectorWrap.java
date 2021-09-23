/*
    Klasa-wrapper potrzebna, by oczekiwanie na klientów było w oddzielnym wątku

    Data        | Autor zmian           | Zmiany
    ------------|-----------------------|---------------------------------------------------
    19.05.2020  | Szymon Krawczyk       |   Utworzenie
                |                       |
 */

package Game;

import java.net.ServerSocket;

public class ClientConnectorWrap extends Thread {

    public ServerSocket serverSocket;
    public int Port = 29090;

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(Port);

            System.out.println("Serwer otwarty.");

            try {
                while (true) {
                    ClientConnector temp = new ClientConnector(serverSocket.accept());
                    MainSerwer.ClientConnectorList.add(temp);
                    temp.start();
                }
            }
            finally {
                serverSocket.close();
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }

    }

}
