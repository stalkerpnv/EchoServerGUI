package simpleserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.io.IOException;
import java.util.Scanner;

class SimpleClient {
    final String SERVER_ADDR = "127.0.0.1"; // "localhost"
    final int SERVER_PORT = 2048;
    final String CLIENT_PROMPT = "$ ";
    final String CONNECT_TO_SERVER = "Соединение с сервером установлено.";
    final String CONNECT_CLOSED = "Сервер завершил работу.";
    final String EXIT_COMMAND = "exit"; // команда для выхода

    public static void main(String[] args) {
        new SimpleClient();
    }

    SimpleClient() {
        String message;
        try (Socket socket = new Socket(SERVER_ADDR, SERVER_PORT);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {
            System.out.println(CONNECT_TO_SERVER);
            do {
                System.out.print(CLIENT_PROMPT);
                message = scanner.nextLine();
                out.writeUTF(message);
                String strFromServer = in.readUTF();
                System.out.println(strFromServer);
            } while (!message.equals(EXIT_COMMAND));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(CONNECT_CLOSED);
    }
}