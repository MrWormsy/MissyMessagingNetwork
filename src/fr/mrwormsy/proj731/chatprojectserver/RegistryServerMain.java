package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistryServerMain {

    // The Registry will be always on the port 22222
    public static void main(String args[]) throws RemoteException {
        RegistryServer registryServer = new RegistryServer();


        while (true) {
            
        }
    }

}
