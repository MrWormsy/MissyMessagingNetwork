package fr.mrwormsy.proj731.chatprojectserver;

import java.io.IOException;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class LocalServer implements RemoteLocalServer {

    public ArrayList<RemoteClient> users;

    // Has every entities it is connected to
    public Registry registry;

    public String id;

    public RemoteClient host;

    public HashMap<Long, String> messagesData;

    // We create a local server by a new id but also the host
    public LocalServer(String id, RemoteClient host) {
        this.users = new ArrayList<RemoteClient>();

        this.id = id;
        this.host = host;

        this.messagesData = new HashMap<Long, String>();

        //We create the registry for the server here
        try {
            // Here we check if the port is already taken by one of the local servers by "testing" with a socket
            int port = 50000;
            while (!available(port)) {
                port++;
            }

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

    private static boolean available(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }


    @Override
    public boolean sendMessage(String from, String message) throws RemoteException {
        for (RemoteClient friend : this.getUsers()) {

            System.out.println(friend.getUsername());

            if (from.equalsIgnoreCase(friend.getUsername())) {
                friend.sendMessage(id, "You", message);
            } else {
                friend.sendMessage(id, from, message);
            }
        }
        return true;
    }

    // Check if the user is in the conversation
    @Override
    public boolean containsUser(String theUser) throws RemoteException {

        for (RemoteClient temp : getUsers()) {
            if (theUser.equalsIgnoreCase(temp.getUsername())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void destroy() throws RemoteException {

        // We first need to kick all the RemoteClient and then destroy the server.
        for (RemoteClient client : getUsers()) {
            client.getLocalServers().remove(this.id);
        }

        // We close the registry
        UnicastRemoteObject.unexportObject(this.registry,true);

        this.registry = null;
    }

    @Override
    public String getId() throws RemoteException {
        return this.id;
    }

    public ArrayList<RemoteClient> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<RemoteClient> users) {
        this.users = users;
    }

    public RemoteClient getHost() throws RemoteException {
        return host;
    }

    @Override
    public Registry getRegistry() throws RemoteException {
        return this.registry;
    }


    public void setHost(RemoteClient host) {
        this.host = host;
    }
}
