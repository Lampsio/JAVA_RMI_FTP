package com.example.demo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.File;

public interface FileService extends Remote {
    void uploadFileToServer(byte[] fileData, String fileName) throws RemoteException;
    byte[] downloadFileFromServer(String fileName) throws RemoteException;
    String[] listFilesOnServer() throws RemoteException;
}

