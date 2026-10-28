
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {

        try (
                Socket socket = new Socket(HOST, PORT);

                BufferedReader serverInput = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter serverOutput = new PrintWriter(
                        socket.getOutputStream(), true
                );

                BufferedReader keyboard = new BufferedReader(
                        new InputStreamReader(System.in)
                )
        ) {
            System.out.print("Digite seu nome: ");
            String username = keyboard.readLine();

            if (username == null || username.isBlank()) {
                System.out.println("Nome invalido.");
                return;
            }

            serverOutput.println(username);

            Thread receiver = new Thread(() -> {
                try {
                    String message;

                    while ((message = serverInput.readLine()) != null) {
                        System.out.println(message);
                    }

                    System.out.println(
                            "Conexao com o servidor encerrada."
                    );

                } catch (IOException e) {
                    System.out.println(
                            "Erro ao receber mensagens."
                    );
                }
            });

            receiver.setDaemon(true);
            receiver.start();

            System.out.println(
                    "Conectado! Digite /sair para desconectar."
            );

            String message;

            while ((message = keyboard.readLine()) != null) {
                serverOutput.println(message);

                if (message.equalsIgnoreCase("/sair")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println(
                    "Nao foi possivel conectar ao servidor: "
                            + e.getMessage()
            );
        }
    }
}
