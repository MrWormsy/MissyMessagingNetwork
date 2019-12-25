package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RemoteServer extends Remote {

    void log(String message) throws RemoteException;

    boolean userExists(String username) throws RemoteException;

    boolean isUserAlreadyLoggedIn(String username) throws RemoteException;

    void logInUser(String username) throws RemoteException;

    boolean logOut(String username) throws RemoteException;

    boolean sendMessage(String from, String to, String message) throws RemoteException;

    ArrayList<String> getAllOnlinePlayers() throws RemoteException;

    RemoteClient getUserFromUsername(String username) throws RemoteException;
}