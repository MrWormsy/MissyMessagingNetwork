package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

public class ClientMain {

    public static RemoteClient theClient;

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        // The web adress of the registry server (the only must known adress)

        String registryAdress = "193.48.125.115";

        Registry registryServer = LocateRegistry.getRegistry(registryAdress, 22222);

        System.out.println("THE REGISTRY SERVER " + registryServer);

        // We set the registry as a parameter because we will need him in the future (we thus have removed all the occurrences of ClientMain.registryServer...)
        theClient = new Client();


        // Check if the registry is available
        try {
            registryServer.list();
            System.out.println("Connection to registry server successful");
        } catch (Exception e) {
            System.out.println("Connection to registry server failed");
            return;
        }

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

    public static RemoteClient getTheClient() {
        return theClient;
    }

}
