package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Client {

    public static Registry registryServer;

    static RemoteClient theUser;

    public static void main(String args[]) throws RemoteException, AlreadyBoundException {

        registryServer = LocateRegistry.getRegistry("localhost", 22222);
        theUser = new User();

        RemoteClient remoteUser = (RemoteClient) UnicastRemoteObject.exportObject(theUser, 0);
        registryServer.bind("test", remoteUser);

        // Check if the registry is available
        try {
            registryServer.list();
            System.out.println("Connection to registry server successful");
        } catch (Exception e) {
            System.out.println("Connection to registry server failed");
            return;
        }
    }

}
