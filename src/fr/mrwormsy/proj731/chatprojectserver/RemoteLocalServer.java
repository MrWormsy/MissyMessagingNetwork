package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RemoteLocalServer extends Remote {

    boolean sendMessage(String from, String message) throws RemoteException;

    public ArrayList<RemoteClient> getUsers() throws RemoteException;

    public void setUsers(ArrayList<RemoteClient> users) throws RemoteException;


}
