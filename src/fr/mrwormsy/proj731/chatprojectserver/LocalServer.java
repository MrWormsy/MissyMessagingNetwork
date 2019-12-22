package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class LocalServer implements RemoteLocalServer {

    public ArrayList<RemoteClient> users;

    // Has every entities it is connected to
    public Registry registry;

    public String id;
    public RemoteClient host;

    // We create a local server by a new id but also the host
    public LocalServer(String id, RemoteClient host) {
        this.users = new ArrayList<RemoteClient>();

        this.id = id;

        this.host = host;

        //We create the registry for the server here
        try {

            // TODO MAYBE HERE WE WILL GET A PROBLEM BECAUSE WE WILL CREATE SEVERAL REGISTERY ON THE SAME PORT AND I DONT THINK THIS WILL WORK
            // YES WE GOT THAT PROBLEM

            registry = LocateRegistry.createRegistry(1888);
            registry.bind("LSERVER_" + id, UnicastRemoteObject.exportObject(this, 0));
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }

        // Check if the registry is available
        try {
            registry.list();
            System.out.println("Connection to registry server successful");
        } catch (Exception e) {
            System.out.println("Connection to registry server failed");
            return;
        }

    }

    @Override
    public boolean sendMessage(String from, String message) throws RemoteException {

        for(RemoteClient friend : this.getUsers()) {

            if (from.equalsIgnoreCase(friend.getUsername())) {
                friend.sendMessage("You", message);
            }
            else {
                friend.sendMessage(from, message);
            }

            System.out.println("You receive a message from " + from);

        }



        /*

        // We loop through the list of users connected and then we send them messages (if the user is the sender we replace his name by "you")
        for(String friend : this.registry.list()) {

            try {
                temp = (RemoteClient) registry.lookup("USER_" + friend);

                if (from.equalsIgnoreCase(friend)) {
                    temp.sendMessage("You", message);
                }
                else {
                    temp.sendMessage(from, message);
                }

                System.out.println("TEST TEST " + friend);

            } catch (NotBoundException e) {e.printStackTrace();}


            System.out.println("FRIEND " + friend);
        }

        */

        return true;
    }

    public ArrayList<RemoteClient> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<RemoteClient> users) {
        this.users = users;
    }
}
