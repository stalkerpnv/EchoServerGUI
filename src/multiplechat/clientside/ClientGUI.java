package multiplechat.clientside;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

public class ClientGUI extends JFrame {
    final String SERVER_ADDR = "127.0.0.1"; // or "localhost"
    final int SERVER_PORT = 8189;
    final String CONNECT_TO_SERVER = "Соединение с сервером установлено.";
    final String CONNECT_CLOSED = "Сервер завершил работу.";
    final String EXIT_COMMAND = "exit"; // command for exit
    private JTextField msgInputField;
    private JTextArea chatArea;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    public ClientGUI() {
        try {
            openConnection();
            prepareGUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() throws IOException {
        socket = new Socket(SERVER_ADDR, SERVER_PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String strFromServer = in.readUTF();
                        chatArea.append(strFromServer);
                        chatArea.append("\n");
                        if (strFromServer.equalsIgnoreCase("/end")) {
                            break;
                        }
                    }
                } catch (EOFException eof) {
                    JOptionPane.showMessageDialog(null, "Нет соединения с сервером");
                } catch (ConnectException connectException) {
                    System.out.println("Нет подключения к серверу");
                } catch (SocketException connectException) {
                    System.out.println("Нет соединения с сервером");
                } catch (Exception e) {
                    System.out.println("Нет подключения к серверу");
                }

            }
        }).start();
    }

    public void closeConnection() {
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

    public void sendMessage() {
        if (!msgInputField.getText().trim().isEmpty()) {
            try {
                out.writeUTF(msgInputField.getText());
                msgInputField.setText("");
                msgInputField.grabFocus();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка отправки сообщения");
            }

        }
    }

    public void prepareGUI() {
        // Параметры окна
        setBounds(600, 300, 500, 500);
        setTitle("Клиент");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Текстовое поле для вывода сообщений
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        // Нижняя панель с полем для ввода сообщений и кнопкой отправки сообщений
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton btnSendMsg = new JButton("Отправить");
        bottomPanel.add(btnSendMsg, BorderLayout.EAST);
        msgInputField = new JTextField();
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(msgInputField, BorderLayout.CENTER);
        btnSendMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        msgInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        // Настраиваем действие на закрытие окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    out.writeUTF(EXIT_COMMAND);
                    closeConnection();
                    chatArea.append(CONNECT_CLOSED);
                    chatArea.append("\n");
                }
                catch (IOException exc) {
                    exc.printStackTrace();
                }

            }
        });
        setVisible(true);
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}