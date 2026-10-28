
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final Set<ClientHandler> clients;

    private PrintWriter out;
    private String username;

    public ClientHandler(
            Socket socket,
            Set<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {

        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(), true
                )
        ) {
            this.out = writer;

            username = in.readLine();

            if (username == null || username.isBlank()) {
                return;
            }

            System.out.println(
                    username + " conectou-se ao servidor."
            );

            broadcast(
                    "[SERVIDOR] " + username + " entrou no chat.",
                    this
            );

            String message;

            while ((message = in.readLine()) != null) {

                if (message.equalsIgnoreCase("/sair")) {
                    break;
                }

                if (!message.isBlank()) {
                    String formattedMessage =
                            username + ": " + message;

                    System.out.println(formattedMessage);

                    broadcast(formattedMessage, null);
                }
            }

        } catch (IOException e) {
            System.out.println(
                    "Conexao encerrada para " + username
            );

        } finally {
            clients.remove(this);

            try {
                socket.close();
            } catch (IOException ignored) {
            }

            if (username != null && !username.isBlank()) {
                broadcast(
                        "[SERVIDOR] " + username + " saiu do chat.",
                        this
                );

                System.out.println(
                        username + " desconectou-se."
                );
            }
        }
    }

    private void broadcast(
            String message,
            ClientHandler excludedClient) {

        for (ClientHandler client : clients) {
            if (client != excludedClient) {
                client.sendMessage(message);
            }
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
