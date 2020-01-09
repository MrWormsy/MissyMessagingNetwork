package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.RemoteException;

public class RegistryServerMain {

    // The Registry will be always on the port 22222
    public static void main(String[] args) throws RemoteException {

        // We create a registry server
        RegistryServer registryServer = new RegistryServer();

        // We loop indefinitely to have a server running
        while (true) {
        }
    }

}
