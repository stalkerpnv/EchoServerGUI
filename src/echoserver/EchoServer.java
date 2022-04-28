package echoserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    final static int SERVER_PORT = 8189;
    final static String SERVER_START = "Сервер запущен...";
    final static String SERVER_STOP = "Сервер остановлен.";
    final static String CLIENT_JOINED = "Клиент присоединился.";
    final static String CLIENT_DISCONNECTED = "Клиент подключился";
    final static String EXIT_COMMAND = "exit"; // команда для выхода

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(SERVER_PORT);
        System.out.println(SERVER_START);
        Socket socket = server.accept();
        System.out.println(CLIENT_JOINED);
        try (//Поток ввода, предназначен для чтения сообщений от пользователя
             DataInputStream in = new DataInputStream(socket.getInputStream());
             //Поток вывода, предназначен для отправки сообщений пользователю
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());) {
            String message;
            do {
                message = in.readUTF();
                System.out.println("Клиент: " + message);
                out.writeUTF("Echo: " + message);
            } while (!message.equalsIgnoreCase(EXIT_COMMAND));
            System.out.println(CLIENT_DISCONNECTED);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(SERVER_STOP);
    }
}
