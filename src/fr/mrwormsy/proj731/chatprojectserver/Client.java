package fr.mrwormsy.proj731.chatprojectserver;

import fr.mrwormsy.proj731.chatprojectserver.gui.ClientGUI;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

// TODO HANDLE LOCAL SERVERS THAT ARE NOT ENDED WE NEED TO MAKE AN OTHER PERSON OF THE TEAM THE LEADER AND ALSO MAYBE THE LEADER CAN GIVE HOS LEADER PRIVILEDGE ON SOMEONE ELSE
// TODO WE NEED TO REMOVE THE GUYS WHO DECONNECTED AND THE CONVERSATIONS FROM THE MENU
// TODO Quit a conversation. If we quit a conversation and we were only two we destroy this conversation, otherwise we make a random user the new leader if it is the leader that left this conversation


public class Client implements RemoteClient {

    // Set of local servers with a unique id
    public HashMap<String, RemoteLocalServer> localServers;

    // The RemoteClient instance
    public RemoteClient remoteClient;

    // The GUI
    private ClientGUI clientGUI;

    // The username used to identity me on the network
    private String username;

    // The remote registry server
    private RemoteRegistryServer remoteRegistryServer;

    // The password but it is pretty useless now
    private String password;

    // --- Constructor ---

    public Client() {
        this.username = "";
        this.password = "";

        // We gather the remote registry server (Which only contains the clients)
        try {
            Registry serverRegistry = LocateRegistry.getRegistry(ClientMain.registryAddress, 22222);
            this.remoteRegistryServer = (RemoteRegistryServer) serverRegistry.lookup("clients");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        // Create a GUI and set it visible
        clientGUI = new ClientGUI(this);
        clientGUI.setVisible(true);

        // Init local servers to empty
        this.localServers = new HashMap<String, RemoteLocalServer>();
    }

    // --- Ovveriden methods ---

    // Create an account for the user (useless for now) and export the client
    @Override
    public void createUserAccount(String username, String password) throws RemoteException {
        this.username = username;
        this.password = password;

        remoteClient = (RemoteClient) UnicastRemoteObject.exportObject(this, 0);
        remoteClient.log("Account has been created");
    }

    // Log the user in with its brand new account and send it to the registry server clients' database
    @Override
    public void logIn(String username, String password) throws RemoteException {
        // If the user is already connected (into the database) we need to cancel the login...
        if (this.remoteRegistryServer.clientExists(username)) {
            System.out.println("This user is already connected, try with an other one...");
            return;
        }

        // Set the username of the GUI
        clientGUI.setUsername(username);

        // Change the title of the windows because we want the username as the title
        this.clientGUI.setTitle(username);

        // Add the client to the database
        this.remoteRegistryServer.addClient(username, this.remoteClient);
        remoteClient.log("Client logged in");
    }

    // Log the user out in a safe way
    @Override
    public void logOut() throws RemoteException {

        // Here the user quits all the conversations he is into...
        Iterator it = localServers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            quitConversation((String) pair.getKey());
        }

        // Remove this user from the database
        this.remoteRegistryServer.removeClient(this.username);

        // As we log out we set "Logged out" as the name of the window
        this.clientGUI.setTitle("Logged out");
    }

    // Write log for debugging
    @Override
    public void log(String log) throws RemoteException {
        System.out.println(log);
    }

    // Send a message to all the users of the conversation
    @Override
    public void sendMessage(String conversation, String from, String message) throws RemoteException {
        clientGUI.writeMessage(conversation, from, message);
    }

    // Update the online players
    @Override
    public void updateOnlinePlayers() throws RemoteException {

        // Get the list of online players (the ones who are the the database)
        ArrayList<String> onlines = this.getOnlinePersons();

        // We loop through this file
        for (String online : onlines) {

            // Here we need to create a submenu for the current online user to be able to do some interactions with him (everybody except the client)
            if (!online.equalsIgnoreCase(getUsername())) {
                if (!clientGUI.isUserAlreadyInPeopleMenu(online)) {
                    clientGUI.createPeopleForMenuByName(online);
                }
            }
        }
    }

    // Update the list of the conversations I am in
    @Override
    public void updateMyConversations() throws RemoteException {

        // We get the conversations the user is in
        ArrayList<String> conversations = this.getMyConversations();

        // We create a submenu for the client to do interactions with this conversation
        for (String conv : conversations) {
            if (!clientGUI.isConversationsAlreadyInConversationsMenu(conv)) {
                clientGUI.createConversationsForMenuByName(conv);
            }
        }
    }

    // Get the online persons (The ones who are in the database)
    @Override
    public ArrayList<String> getOnlinePersons() throws RemoteException {

        // We create the return list
        ArrayList<String> onlines = new ArrayList<String>();

        // We loop through the people that are in the database and we add them to the list to return
        for (RemoteClient theClient : this.remoteRegistryServer.getClients()) {
            onlines.add(theClient.getUsername());
        }

        return onlines;
    }

    // Get the conversations I am part of
    @Override
    public ArrayList<String> getMyConversations() throws RemoteException {

        // We create the return list
        ArrayList<String> conversations = new ArrayList<String>();

        // We loop through all the conversations that I belong and we will return them
        Iterator it = localServers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            conversations.add((String) pair.getKey());
        }

        return conversations;
    }

    // Start a conversation (server) with one or more friends
    @Override
    public void startServerWith(String... friend) throws RemoteException {
        // We get a random UUID for the conversation
        UUID randomUUID = UUID.randomUUID();

        // Create a new Local Server where the user is the host
        RemoteLocalServer localServer = new LocalServer(randomUUID.toString(), this);

        // We add this conversations to the ones the users is into
        this.localServers.put(randomUUID.toString(), localServer);

        // And we add the user to this conversation
        localServer.getUsers().add(this);

        // Temp value
        RemoteClient temp;

        // Here we add all the friends :
        for (String f : friend) {
            try {

                // We need to warn them that we have been added to this conversation with the conversation's id
                temp = this.remoteRegistryServer.getRemoteClient(f);
                temp.sendInvitationToServer(randomUUID.toString(), localServer);

                // And add the friend
                localServer.getUsers().add(temp);
            } catch (Exception e) {
                System.out.println("This user does not exists");
            }
        }

        // We set that to notify that this conversation is up
        clientGUI.setCurrentConversation(randomUUID.toString());

        // We update the title (To know on which conversation we are talking)
        clientGUI.setTitle(username + " @ " + randomUUID.toString());

        // And we need to create an empty conversationData
        clientGUI.getConversationsData().put(randomUUID.toString(), "You are now connected, have fun chatting");

        // And show this conversation
        clientGUI.showConversation(randomUUID.toString());
    }

    // Warn that the user has been invited to join a conversation and the boolean means whether the user has been successfully invited or not
    @Override
    public boolean sendInvitationToServer(String serverId, RemoteLocalServer localServer) throws RemoteException {

        // Add the local server to the server's list of the player
        this.localServers.put(serverId, localServer);

        // We set the current conversation to this one
        this.clientGUI.setCurrentConversation(serverId);

        // We update the title
        clientGUI.setTitle(username + " @ " + serverId);

        // And we need to create an empty conversationData
        clientGUI.getConversationsData().put(serverId, "You are now connected, have fun chatting");

        // And show this conversation
        clientGUI.showConversation(serverId);

        return true;
    }

    // This method is to add user the an already created conversation
    @Override
    public void addUserToTheConversation(String conv, String user) throws RemoteException {

        // Check if this user is the host of the Local Server
        if (this.getLocalServers().get(conv).getHost() != this) {
            JOptionPane.showMessageDialog(clientGUI, "You cannot add this user because you are not the host");
            return;
        }

        // We need to warn them that we have been added to this conversation with the conversation's id
        RemoteClient temp = this.remoteRegistryServer.getRemoteClient(user);
        temp.sendInvitationToServer(conv, this.localServers.get(conv));

        // And add the user to the server
        this.localServers.get(conv).getUsers().add(temp);
    }

    // Quit a given conversation (/!\ This method may lead to errors /!\)
    @Override
    public void quitConversation(String conv) throws RemoteException {

        // Get the conversation the user wants to leave
        RemoteLocalServer theServer = getLocalServers().get(conv);

        // First thing first, we need to check how many they were in this conversation...
        // If they were two or less we need to destroy this local server...
        if (theServer.getRegistry().list().length <= 2) {
            theServer.destroy();
            this.localServers.remove(conv);
        }

        // Else I did not do it yet :/
        else {

        }
    }

    // --- Getters and Setters ---

    @Override
    public HashMap<String, RemoteLocalServer> getLocalServers() {
        return localServers;
    }

    public void setLocalServers(HashMap<String, RemoteLocalServer> localServers) {
        this.localServers = localServers;
    }

    public RemoteClient getRemoteClient() {
        return remoteClient;
    }

    public void setRemoteClient(RemoteClient remoteClient) {
        this.remoteClient = remoteClient;
    }

    @Override
    public ClientGUI getClientGUI() {
        return clientGUI;
    }

    public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RemoteRegistryServer getRemoteRegistryServer() {
        return remoteRegistryServer;
    }

    public void setRemoteRegistryServer(RemoteRegistryServer remoteRegistryServer) {
        this.remoteRegistryServer = remoteRegistryServer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
