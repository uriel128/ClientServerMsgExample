package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerView implements Initializable {
    @FXML
    private Button button_send;

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        button_send.setOnAction(event -> sendMessage());
        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(6666, 1, InetAddress.getByName("localhost"))) {
                addMessage("Waiting on localhost:6666...");
                socket = serverSocket.accept();
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                addMessage("User 1 connected");

                while (true) {
                    String incoming = dis.readUTF();
                    addMessage("User 1: " + incoming);
                }
            } catch (IOException e) {
                addMessage("Server stopped: " + e.getMessage());
            }
        }).start();
    }

    private void sendMessage() {
        String text = tf_message.getText();
        if (text == null || text.isBlank() || dos == null) {
            return;
        }
        try {
            dos.writeUTF(text);
            dos.flush();
            addMessage("User 2: " + text);
            tf_message.clear();
        } catch (IOException e) {
            addMessage("Send failed: " + e.getMessage());
        }
    }

    private void addMessage(String text) {
        Platform.runLater(() -> vbox_messages.getChildren().add(new Label(text)));
    }
}
