package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

public class ClientMain {

    public static Registry registryServer;

    public static RemoteClient theClient;

    public static void main(String args[]) throws RemoteException, AlreadyBoundException {

        registryServer = LocateRegistry.getRegistry("localhost", 22222);

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
                    theClient.updateOnlinePlayers();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer("lookingForPeopleTimer");

        long delay  = 0L;
        long period = 1000L;
        timer.scheduleAtFixedRate(lookingForPeopleTask, delay, period);
    }

    public static Registry getRegistryServer() {
        return registryServer;
    }

    public static RemoteClient getTheClient() {
        return theClient;
    }

}
