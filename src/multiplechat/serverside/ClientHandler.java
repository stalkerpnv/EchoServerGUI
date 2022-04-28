package multiplechat.serverside;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final String WELCOME = "Вы вошли под именем: ";
    private final String JOINED = " зашел в чат";
    private final String IS_BUSY = "Учетная запись уже используется";
    private final String INVALID_LOGIN = "Неверные логин/пароль";
    private final String ERROR = "Проблемы при создании обработчика клиента";
    private final String UNSUBCRIBE = " вышел из чата";
    private final String EXIT_COMMAND = "/end"; // команда для выхода
    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(ERROR);
        }
    }

    public void authentication() throws IOException {
        try {
            while (true) {
                String str = in.readUTF();
                if (str.startsWith("/auth")) {
                    String[] parts = str.split("\\s");

                    String nick = myServer.authService().getNickByLoginPass(parts[1], parts[2]);
                    if (nick != null) {
                        if (!myServer.isNickBusy(nick)) {
                            sendMsg(WELCOME + nick);
                            name = nick;
                            myServer.broadcastMsg(name + JOINED);
                            myServer.subscribe(this);
                            return;
                        } else sendMsg(IS_BUSY);
                    } else sendMsg(INVALID_LOGIN);
                }
            }
        } catch (EOFException ignored) {

        }
    }

    public void readMessages() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();
            if (strFromClient.startsWith("/w")) {
                String[] parts = strFromClient.split(" ");
                String nickTo = parts[1];
                String message = parts[2];

                if (myServer.isNickBusy(nickTo)) {
                    myServer.broadcastMsgToNick(name, parts[1], parts[2]);

                } else {
                    myServer.broadcastMsg(name + ": " + strFromClient);
                }
            } else {
                myServer.broadcastMsg(name + ": " + strFromClient);
            }
            System.out.println("от " + name + ": " + strFromClient);
            if (strFromClient.equals(EXIT_COMMAND)) {
                return;
            }
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        myServer.unsubscribe(this);
        myServer.broadcastMsg(name + UNSUBCRIBE);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}