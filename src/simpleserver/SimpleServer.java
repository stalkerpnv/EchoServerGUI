package simpleserver;

import java.io.*;
import java.net.*;

class SimpleServer {
    final int SERVER_PORT = 2048;
    final String SERVER_START = "Сервер запущен...";
    final String SERVER_STOP = "Сервер остановлен.";
    final String CLIENT = "Клиент";
    final String JOINED = " присоединился";
    final String CLIENT_DISCONNECTED = "Клиент отключился";
    final String EXIT_COMMAND = "exit"; // команда для выхода

    public static void main(String[] args) {
        new SimpleServer();
    }

    SimpleServer() {
        int clientCount = 0;
        System.out.println(SERVER_START);
        try (ServerSocket server = new ServerSocket(SERVER_PORT)) {
            while (true) {
                Socket socket = server.accept();
                System.out.println(CLIENT + " #" + (++clientCount) + JOINED);
                new Thread(new ClientHandler(socket, clientCount)).start();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(SERVER_STOP);
    }

    /**
     * ClientHandler: Обработчик для клиента
     */
    class ClientHandler implements Runnable {
        DataInputStream in;
        DataOutputStream out;
        Socket socket;
        String name;

        ClientHandler(Socket clientSocket, int clientCount) {
            try {
                socket = clientSocket;
                //Поток ввода, предназначен для чтения сообщений от пользователя
                in = new DataInputStream(socket.getInputStream());
                //Поток вывода, предназначен для отправки сообщений пользователю
                out = new DataOutputStream(socket.getOutputStream());
                name = "Клиент #" + clientCount;

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        @Override
        public void run() {
            String message;
            try {
                do {
                    message = in.readUTF();
                    System.out.println(name + ": " + message);
                    out.writeUTF("Echo: " + message);
                } while (!message.equalsIgnoreCase(EXIT_COMMAND));
                socket.close();
                System.out.println(name + CLIENT_DISCONNECTED);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}