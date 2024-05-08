package com.example.demo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;

public class FileServiceImpl extends UnicastRemoteObject implements FileService {
    private static final String SERVER_DIR = "D:\\RMI_Serwer";

    protected FileServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void uploadFileToServer(byte[] fileData, String fileName) throws RemoteException {
        try {
            Path destinationPath = Paths.get(SERVER_DIR, fileName);
            Files.write(destinationPath, fileData);
            System.out.println("File uploaded to server: " + fileName);
        } catch (IOException e) {
            throw new RemoteException("Error uploading file", e);
        }
    }

    @Override
    public byte[] downloadFileFromServer(String fileName) throws RemoteException {
        try {
            Path filePath = Paths.get(SERVER_DIR, fileName);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RemoteException("Error downloading file", e);
        }
    }

    @Override
    public String[] listFilesOnServer() throws RemoteException {
        File directory = new File(SERVER_DIR);
        return directory.list();
    }
}
