package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RemoteRegistryServer extends Remote {

    void addClient(String clientName, RemoteClient client) throws RemoteException;

    void removeClient(String clientName) throws RemoteException;

    boolean clientExists(String clientName) throws RemoteException;

    ArrayList<RemoteClient> getClients() throws RemoteException;

    RemoteClient getRemoteClient(String clientName) throws RemoteException;
}
