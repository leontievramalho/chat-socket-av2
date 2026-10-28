
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private static final int PORT = 12345;

    private static final Set<ClientHandler> clients =
            ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        System.out.println("Iniciando servidor de chat...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println(
                    "Servidor aguardando conexoes na porta " + PORT
            );

            while (true) {
                Socket socket = serverSocket.accept();

                ClientHandler client =
                        new ClientHandler(socket, clients);

                clients.add(client);

                Thread thread = new Thread(client);
                thread.start();
            }

        } catch (IOException e) {
            System.err.println(
                    "Erro no servidor: " + e.getMessage()
            );
        }
    }
}
