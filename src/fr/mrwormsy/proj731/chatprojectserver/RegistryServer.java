package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistryServer {

    public Registry registry;

    public RegistryServer() {
        try {
            this.registry = LocateRegistry.createRegistry(22222);

            System.out.println("Registry server is ready at port 22222");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
