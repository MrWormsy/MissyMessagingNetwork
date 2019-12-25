package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/*
 *
 * This is the registry where we keep all the Clients
 *
 * But we can add all the references to the local servers as well where we only
 *
 * But the person invited will have to keep a list of the registry they are connected to with the conversation id
 *
 *   All the users will have the prefix USER_user's name
 *           localServers will have the prefix LSERVER_id
 *
 * */


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
