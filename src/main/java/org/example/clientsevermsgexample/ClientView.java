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
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientView implements Initializable {
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
        connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 3000);
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                addMessage("Connected to localhost:3000");

                while (true) {
                    String incoming = dis.readUTF();
                    addMessage("User 2: " + incoming);
                }
            } catch (IOException e) {
                addMessage("Disconnected: " + e.getMessage());
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
            addMessage("User 1: " + text);
            tf_message.clear();
        } catch (IOException e) {
            addMessage("Send failed: " + e.getMessage());
        }
    }

    private void addMessage(String text) {
        Platform.runLater(() -> vbox_messages.getChildren().add(new Label(text)));
    }
}
