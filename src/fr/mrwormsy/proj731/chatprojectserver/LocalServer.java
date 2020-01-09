package fr.mrwormsy.proj731.chatprojectserver;

import java.io.IOException;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class LocalServer implements RemoteLocalServer {

    // The users who are part of the server
    public ArrayList<RemoteClient> users;

    // The local registry
    public Registry registry;

    // The id of the server
    public String id;

    // The host
    public RemoteClient host;

    // --- Contructor ---

    // We create a local server by a new id but also the host
    public LocalServer(String id, RemoteClient host) {

        // We init the variables
        this.users = new ArrayList<RemoteClient>();
        this.id = id;
        this.host = host;

        //We create the registry for the server here
        try {
            // Here we check if the port is already taken by one of the local servers by "testing" with a socket
            int port = 50000;
            while (!available(port)) {
                port++;
            }

            // We create a registry and we export this server to it
            registry = LocateRegistry.createRegistry(port);
            registry.bind("LSERVER_" + id, UnicastRemoteObject.exportObject(this, 0));
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }

        // Check if the registry is available
        try {
            registry.list();
            System.out.println("Connection to registry server successful");
        } catch (Exception e) {
            System.out.println("Connection to registry server failed");
            return;
        }
    }

    // --- Overridden Methods ---

    // Check if the port is available
    private static boolean available(int port) {
        try (Socket temp = new Socket("localhost", port)) {
            return false;
        } catch (IOException temp) {
            return true;
        }
    }

    // Used to send a message to all the users of the conversation
    @Override
    public boolean sendMessage(String from, String message) throws RemoteException {
        for (RemoteClient friend : this.getUsers()) {

            // We prettify the message...
            if (from.equalsIgnoreCase(friend.getUsername())) {
                friend.sendMessage(id, "You", message);
            } else {
                friend.sendMessage(id, from, message);
            }
        }

        // Returns true for future implements
        return true;
    }

    // Check if a user is in the conversation
    @Override
    public boolean containsUser(String theUser) throws RemoteException {
        for (RemoteClient temp : getUsers()) {
            if (theUser.equalsIgnoreCase(temp.getUsername())) {
                return true;
            }
        }
        return false;
    }

    // Destroy the current server (/!\ This method may lead to errors /!\)
    @Override
    public void destroy() throws RemoteException {

        // We first need to kick all the RemoteClient and then destroy the server.
        for (RemoteClient client : getUsers()) {
            client.getLocalServers().remove(this.id);
        }

        // We close the registry
        UnicastRemoteObject.unexportObject(this.registry, true);

        // To destroy the link
        this.registry = null;
    }

    // --- Methods ---

    // Get the id of the server
    @Override
    public String getId() throws RemoteException {
        return this.id;
    }

    // --- Getters and Setters

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public ArrayList<RemoteClient> getUsers() {
        return users;
    }

    @Override
    public void setUsers(ArrayList<RemoteClient> users) {
        this.users = users;
    }

    @Override
    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    @Override
    public RemoteClient getHost() {
        return host;
    }

    public void setHost(RemoteClient host) {
        this.host = host;
    }
}
