package fr.mrwormsy.proj731.chatprojectserver;

import fr.mrwormsy.proj731.chatprojectserver.gui.ClientGUI;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Client implements RemoteClient {
    private ClientGUI clientGUI;

    RemoteClient remoteClient;

    private String username;
    private String password;

    public Client() {
        this.username = "";
        this.password = "";

        clientGUI = new ClientGUI();
        clientGUI.setVisible(true);
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

        /*

        // Check if this user is already logged in
        if (ChatClient.getTheServer().isUserAlreadyLoggedIn(username)) {
            this.log("User already logged");
            return;
        }

        */

        try {
            ClientMain.registryServer.bind(username, this.remoteClient);
            //ChatClient.getTheServer().logInUser(username);

            remoteClient.log("Client logged in");

        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logOut() throws RemoteException {

        //Unbind When logout

        try {
            ClientMain.registryServer.unbind(this.username);
        } catch (NotBoundException e) {
            //Here we do nothing because the person was not logged in
        }

        /*

        if (ChatClient.getTheServer().logOut(this.username)) {
            System.out.println("You has been logged out");
        } else {
            System.out.println("You were not logged in...");
        }

        */
    }

    @Override
    public void log(String log) throws RemoteException {
        System.out.println(log);
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void sendMessage(String from, String message) throws RemoteException {
        clientGUI.writeMessage(from, message);
    }

    @Override
    public void updateOnlinePlayers() throws RemoteException {

        ArrayList<String> onlines = this.getOnlinePersons();

        for (String online : onlines) {
            //if (!online.equalsIgnoreCase(getUsername())) {
                if (!clientGUI.isUserAlreadyInPeopleMenu(online)) {
                    clientGUI.createPeopleForMenuByName(online);
                }
            //}
        }



        /*

        DefaultComboBoxModel model = (DefaultComboBoxModel) this.clientGUI.getOnlineUsers().getModel();
        model.removeAllElements();

        for (String string : onlines) {
            if (!string.equalsIgnoreCase(getUsername())) {
                model.addElement(string);
            }
        }

        this.clientGUI.getOnlineUsers().setModel(model);

        */
    }

    @Override
    public ArrayList<String> getOnlinePersons() throws RemoteException {

        // Here we get the list of people in the registry and then we return it
        ArrayList<String> onlines = new ArrayList<String>();

        for(String online : ClientMain.getRegistryServer().list()) {
            onlines.add(online);
        }

        return onlines;

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ClientGUI getClientGUI() {
        return clientGUI;
    }

    public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

}
