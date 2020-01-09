package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegistryServer implements RemoteRegistryServer {

    // The database of clients
    private HashMap<String, RemoteClient> clients;

    // --- Constructor ---

    public RegistryServer() {
        try {
            // We first create the registry at the port 22222
            Registry registry = LocateRegistry.createRegistry(22222);

            // We create an empty hashmap
            this.clients = new HashMap<>();

            // Here we need to create this to store the clients
            registry.bind("clients", UnicastRemoteObject.exportObject(this, 0));

            // We notify that the server is up
            System.out.println("Registry server is ready at port 22222");
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

    // --- Overridden methods ---

    // Add client to list
    @Override
    public void addClient(String clientName, RemoteClient client) throws RemoteException {
        this.clients.put(clientName, client);
    }

    // Remove client from list
    @Override
    public void removeClient(String clientName) throws RemoteException {
        this.clients.remove(clientName);
    }

    // Check if client exists
    @Override
    public boolean clientExists(String clientName) throws RemoteException {
        Iterator it = this.clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            if (((String) pair.getKey()).equalsIgnoreCase(clientName)) {
                return true;
            }
        }
        return false;
    }

    // Gather all the clients
    @Override
    public ArrayList<RemoteClient> getClients() throws RemoteException {

        ArrayList<RemoteClient> clients = new ArrayList<>();

        Iterator it = this.clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            clients.add((RemoteClient) pair.getValue());
        }
        return clients;
    }

    // Get a client from his name
    @Override
    public RemoteClient getRemoteClient(String clientName) throws RemoteException {
        Iterator it = this.clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            if (((String) pair.getKey()).equalsIgnoreCase(clientName)) {
                return (RemoteClient) pair.getValue();
            }
        }
        return null;
    }
}
