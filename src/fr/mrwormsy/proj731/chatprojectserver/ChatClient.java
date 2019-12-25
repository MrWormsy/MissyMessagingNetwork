package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatClient {

    static RemoteServer theServer;
    static RemoteClient theUser;
    static Registry registry;

    private ChatClient() {
    }

    public static void main(String[] args) {
        try {

            // Getting the registry
            registry = LocateRegistry.getRegistry("localhost", 1888);
            theServer = (RemoteServer) registry.lookup("server");

            // We create the user
            theUser = new User();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RemoteServer getTheServer() {
        return theServer;
    }

    public static void setTheServer(RemoteServer theServer) {
        ChatClient.theServer = theServer;
    }

    public static Registry getRegistry() {
        return registry;
    }

    public static void setRegistry(Registry registry) {
        ChatClient.registry = registry;
    }

    public static RemoteClient getTheUser() {
        return theUser;
    }

    public static void setTheUser(RemoteClient theUser) {
        ChatClient.theUser = theUser;
    }
}
