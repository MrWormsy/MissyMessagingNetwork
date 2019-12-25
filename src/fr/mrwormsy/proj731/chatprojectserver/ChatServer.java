package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ChatServer {

    public static Server theServer;
    public static Registry registry;

    public static ArrayList<User> users;

    public static void main(String args[]) {
        // Binding the remote object (stub) in the registry
        try {
            registry = LocateRegistry.createRegistry(1888);
            theServer = new Server();
            RemoteServer chat = (RemoteServer) UnicastRemoteObject.exportObject(theServer, 0);
            registry.bind("server", chat);
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }

        System.out.println("Server ready");
    }
}
