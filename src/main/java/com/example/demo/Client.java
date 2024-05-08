// Client.java
package com.example.demo;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.rmi.Naming;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;

public class Client extends Application {
    private FileService fileService;
    private static final String KEY = "squirrel123squir"; // for example

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            fileService = (FileService) Naming.lookup("rmi://localhost:1099/FileService");
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileChooser fileChooser = new FileChooser();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Button openButton = new Button("Upload a File...");
        Button downloadButton = new Button("Download Selected File");
        ListView<String> listView = new ListView<>();
        Label statusLabel = new Label();

        // Load the list of files on the server at startup
        try {
            String[] filesOnServer = fileService.listFilesOnServer();
            listView.getItems().addAll(filesOnServer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        openButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    byte[] fileData = Files.readAllBytes(file.toPath());
                    byte[] encryptedData = encrypt(fileData, KEY);
                    fileService.uploadFileToServer(encryptedData, file.getName());
                    listView.getItems().add(file.getName());
                    statusLabel.setText("File sent to server: " + file.getName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        downloadButton.setOnAction(e -> {
            String selectedFile = listView.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                try {
                    byte[] encryptedData = fileService.downloadFileFromServer(selectedFile);
                    byte[] fileData = decrypt(encryptedData, KEY);
                    File destDir = directoryChooser.showDialog(primaryStage);
                    if (destDir != null) {
                        Files.write(Paths.get(destDir.getAbsolutePath(), selectedFile), fileData);
                        statusLabel.setText("File downloaded from server: " + selectedFile);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        VBox vBox = new VBox(openButton, listView, downloadButton, statusLabel);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        Scene scene = new Scene(vBox, 300, 200);

        primaryStage.setTitle("JavaFX RMI Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private byte[] encrypt(byte[] data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    private byte[] decrypt(byte[] data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }
}
