package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RemoteLocalServer extends Remote {

    boolean sendMessage(String from, String message) throws RemoteException;

    ArrayList<RemoteClient> getUsers() throws RemoteException;

    void setUsers(ArrayList<RemoteClient> users) throws RemoteException;
}
