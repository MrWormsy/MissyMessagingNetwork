package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

public class ClientMain {

    // The client instance
    public static RemoteClient theClient;

    // The address of the regitry server (that will may be rosacorp.net)
    public static String registryAddress = "51.178.46.76";

    // Main method
    public static void main(String[] args) throws RemoteException {

        // We get the registry of the refistry server to know if it is online
        //System.setProperty("java.rmi.server.hostname", registryAddress);
        Registry registryServer = LocateRegistry.getRegistry(registryAddress, 22222);


        // We create a client instance
        theClient = new Client();

        // Check if the registry is available (ie the server is up)
        try {
            registryServer.list();
            System.out.println("Connection to registry server successful");
        } catch (Exception e) {
            System.out.println("Connection to registry server failed, it might be offline");
            return;
        }

        // This is dirty but it is the best way to do it yet sorry :/

        // Here we need to make a thread that is looking for other persons every seconds
        TimerTask lookingForPeopleTask = new TimerTask() {
            public void run() {
                try {
                    if (isLoggedIn()) {
                        theClient.updateOnlinePlayers();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer("lookingForPeopleTimer");
        timer.scheduleAtFixedRate(lookingForPeopleTask, 0, 1000L);

        // Here we need to make a thread that is looking for other conversations every seconds
        TimerTask lookingForConversationsTask = new TimerTask() {
            public void run() {
                try {
                    if (isLoggedIn()) {
                        theClient.updateMyConversations();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer2 = new Timer("lookingForConversationsTimer");
        timer2.scheduleAtFixedRate(lookingForConversationsTask, 0, 1000L);
    }

    // Check if the user is logged in
    public static boolean isLoggedIn() {

        // This is a pretty ugly way to do it but i'm only checking if the title of the GUI is not "Logged Out" and that means that we are not logged in
        try {
            if (!getTheClient().getClientGUI().getTitle().equalsIgnoreCase("Logged Out")) {
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get the client in a static way
    public static RemoteClient getTheClient() {
        return theClient;
    }
}
