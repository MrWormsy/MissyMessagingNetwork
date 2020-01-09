package fr.mrwormsy.proj731.chatprojectserver;

import fr.mrwormsy.proj731.chatprojectserver.gui.ClientGUI;

import javax.swing.*;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Client implements RemoteClient {
    // Set of servers that i am the host... (The string is the conversation id : 5W6r8fUsy7rF)
    public HashMap<String, RemoteLocalServer> localServers;
    public RemoteClient remoteClient;
    private ClientGUI clientGUI;
    private String username;

    private RemoteRegistryServer remoteRegistryServer;

    private Registry serverRegistry;

    // To send messages to the server we only need the id of the server with who sent the message
    private String password;

    public Client() {
        this.username = "";
        this.password = "";

        // We gather the registry server
        try {
            this.serverRegistry = LocateRegistry.getRegistry(ClientMain.registryAdress, 22222);

            this.remoteRegistryServer = (RemoteRegistryServer) this.serverRegistry.lookup("clients");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        clientGUI = new ClientGUI(this);
        clientGUI.setVisible(true);

        this.localServers = new HashMap<String, RemoteLocalServer>();
    }

    @Override
    public void createUserAccount(String username, String password) throws RemoteException {

        this.username = username;
        this.password = password;

        remoteClient = (RemoteClient) UnicastRemoteObject.exportObject(this, 0);

        remoteClient.log("Account has been created");
    }

    @Override
    public void logIn(String username, String password) throws RemoteException {

        clientGUI.setUsername(username);


        // If the user is already connected we need to cancel the login
        //if (alreadyBound) {
        if (this.remoteRegistryServer.clientExists(username)) {
            System.out.println("This user is already connected, try with an other one...");

            return;
        }

        /*

        // Check if this user is already logged in
        if (ChatClient.getTheServer().isUserAlreadyLoggedIn(username)) {
            this.log("User already logged");
            return;
        }

        */

        // Change the title of the windows because we want the username as the title
        this.clientGUI.setTitle(username);



        this.remoteRegistryServer.addClient(username, this.remoteClient);
        remoteClient.log("Client logged in");
    }

    // TODO HANDLE LOCAL SERVERS THAT ARE NOT ENDED WE NEED TO MAKE AN OTHER PERSON OF THE TEAM THE LEADER AND ALSO MAYBE THE LEADER CAN GIVE HOS LEADER PRIVILEDGE ON SOMEONE ELSE

    @Override
    public void logOut() throws RemoteException {

        // Here we quit all the conversations I am into...
        Iterator it = localServers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            quitConversation((String) pair.getKey());
        }

        it = localServers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " " + pair.getValue());
        }

        this.remoteRegistryServer.removeClient(this.username);

        // As we log out we set Logged out as the name of the window
        this.clientGUI.setTitle("Logged out");
    }

    @Override
    public void log(String log) throws RemoteException {
        System.out.println(log);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //TODO WE NEED TO REMOVE THE GUYS WHO DECONNECTED AND THE CONVERSATIONS FROM THE MENU

    @Override
    public void sendMessage(String id, String from, String message) throws RemoteException {
        clientGUI.writeMessage(id, from, message);

        Iterator it = localServers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " " + pair.getValue());
        }
    }

    //TODO WE NEED TO REMOVE THE GUYS WHO DECONNECTED AND THE CONVERSATIONS FROM THE MENU

    @Override
    public void updateOnlinePlayers() throws RemoteException {

        ArrayList<String> onlines = this.getOnlinePersons();

        for (String online : onlines) {

            // TODO UNCOMMENT THAT AFTER TESTS
            if (!online.equalsIgnoreCase(getUsername())) {
                if (!clientGUI.isUserAlreadyInPeopleMenu(online)) {
                    clientGUI.createPeopleForMenuByName(online);
                }
            }
        }
    }

    @Override
    public void updateMyConversations() throws RemoteException {

        ArrayList<String> conversations = this.getMyConversations();

        for (String conv : conversations) {
            if (!clientGUI.isConversationsAlreadyInConversationsMenu(conv)) {
                clientGUI.createConversationsForMenuByName(conv);
            }
        }
    }

    @Override
    public ArrayList<String> getOnlinePersons() throws RemoteException {
        // Here we get the list of people in the registry and then we return it
        ArrayList<String> onlines = new ArrayList<String>();

        //for (String online : getServerRegistry().list()) {
        for (RemoteClient theClient : this.remoteRegistryServer.getClients()) {

            onlines.add(theClient.getUsername());
        }
        return onlines;
    }

    @Override
    public ArrayList<String> getMyConversations() throws RemoteException {
        // Here we get the list of people in the registry and then we return it
        ArrayList<String> conversations = new ArrayList<String>();

        Iterator it = localServers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            conversations.add((String) pair.getKey());
        }


        return conversations;
    }

    // Add him and add the friend
    @Override
    public void startServerWith(String... friend) throws RemoteException {
        UUID randomUUID = UUID.randomUUID();

        RemoteLocalServer localServer = new LocalServer(randomUUID.toString(), this);

        // The first user we have in our conversation participants will be the host and then we send the others...
        this.localServers.put(randomUUID.toString(), localServer);
        localServer.getUsers().add(this);

        RemoteClient temp;

        // Here we add all the friends :
        for (String f : friend) {
            try {
                // We need to warn them that we have been added to this conversation with the conversation's id
                temp = this.remoteRegistryServer.getRemoteClient(f);
                temp.sendInvitationToServer(randomUUID.toString(), localServer);

                // And add the user
                localServer.getUsers().add(temp);
            } catch (Exception e) {
                System.out.println("This user does not exists");
            }
        }

        // Send a message to warn them that the conversation is up
        System.out.println("SERVER " + randomUUID.toString() + " is up");

        clientGUI.setCurrentConversation(randomUUID.toString());

        // We update the title
        clientGUI.setTitle(username + " @ " + randomUUID.toString());

        // And we need to create an empty conversationData
        clientGUI.getConversationsData().put(randomUUID.toString(), "You are now connected, have fun chatting");

        // And show this conversation
        clientGUI.showConversation(randomUUID.toString());

    }

    // Warn that this user has been invited to join a conversation and the boolean means whether the user has been successfully invited or not
    @Override
    public boolean sendInvitationToServer(String serverSId, RemoteLocalServer localServer) throws RemoteException {

        // Add the local server to the hashmap of the player
        this.localServers.put(serverSId, localServer);

        this.clientGUI.setCurrentConversation(serverSId);

        // We update the title
        clientGUI.setTitle(username + " @ " + serverSId);

        // And we need to create an empty conversationData
        clientGUI.getConversationsData().put(serverSId, "You are now connected, have fun chatting");

        // And show this conversation
        clientGUI.showConversation(serverSId);

        return true;
    }

    @Override
    public void addUserToTheConversation(String conv, String user) throws RemoteException {

        // Check if this user is the host of the host of Localserver
        if (this.getLocalServers().get(conv).getHost() != this) {
            JOptionPane.showMessageDialog(clientGUI, "You cannot add this user because you are not the host");
            return;
        }

        try {
            // We need to warn them that we have been added to this conversation with the conversation's id
            RemoteClient temp = this.remoteRegistryServer.getRemoteClient(user);
            temp.sendInvitationToServer(conv, this.localServers.get(conv));

            // And add the user
            this.localServers.get(conv).getUsers().add(temp);
        } catch (Exception e) {
            System.out.println("This user does not exists");
        }
    }

    // TODO Quit a conversation. If we quit a conversation and we were only two we destroy this conversation, otherwise we make a random user the new leader if it is the leader that left this conversation
    @Override
    public void quitConversation(String conv) throws RemoteException {

        RemoteLocalServer theServer = getLocalServers().get(conv);

        // First thing first, we need to check how many they were in this conversation...
        // If they were two or less we need to destroy this local server...
        if (theServer.getRegistry().list().length <= 2) {
            theServer.destroy();

            this.localServers.remove(conv);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ClientGUI getClientGUI() throws RemoteException {
        return clientGUI;
    }

    public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    public HashMap<String, RemoteLocalServer> getLocalServers() {
        return localServers;
    }

    public void setLocalServers(HashMap<String, RemoteLocalServer> localServers) {
        this.localServers = localServers;
    }

    /*

    public Registry getServerRegistry() throws RemoteException {
        String registryAdress = "193.48.125.115";

        return LocateRegistry.getRegistry(registryAdress, 22222);
    }

     */
}
