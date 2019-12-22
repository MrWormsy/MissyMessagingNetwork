package fr.mrwormsy.proj731.chatprojectserver;

import fr.mrwormsy.proj731.chatprojectserver.gui.ClientGUI;

import javax.swing.*;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class User implements RemoteClient {
	private ClientGUI clientGUI;

	RemoteClient stub;

	private String username;
	private String password;
	
	public User() {
		this.username = "";
		this.password = "";

		//clientGUI = new ClientGUI(this);
		clientGUI.setVisible(true);
	}



	@Override
	public void createUserAccount(String username, String password) throws RemoteException {

		this.username = username;
		this.password = password;

		stub = (RemoteClient) UnicastRemoteObject.exportObject(this, 0);

		stub.log("Account has been created");
	}

	@Override
	public void logIn(String username, String password) throws RemoteException {

		clientGUI.setUsername(username);

		// Check if this user is already logged in
		if (ChatClient.getTheServer().isUserAlreadyLoggedIn(username)) {
			this.log("User already logged");
			return;
		}

		try {
			ChatClient.registry.bind(username, this.stub);
			ChatClient.getTheServer().logInUser(username);

			stub.log("Client logged in");

		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void logOut() throws RemoteException {
		if (ChatClient.getTheServer().logOut(this.username)) {
			System.out.println("You has been logged out");
		} else {
			System.out.println("You were not logged in...");
		}
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

		ArrayList<String> onlines = null;
		DefaultComboBoxModel model = (DefaultComboBoxModel) this.clientGUI.getOnlineUsers().getModel();
		model.removeAllElements();

		for (String string : onlines) {
			if (!string.equalsIgnoreCase(getUsername())) {
				model.addElement(string);
			}
		}

		this.clientGUI.getOnlineUsers().setModel(model);
	}

	@Override
	public void updateMyConversations() throws RemoteException {

	}

	@Override
	public ArrayList<String> getOnlinePersons() throws RemoteException {
		return null;
	}

	@Override
	public ArrayList<String> getMyConversations() throws RemoteException {
		return null;
	}

	@Override
	public void startServerWith(String... friend) throws RemoteException {

	}

	@Override
	public boolean sendInvitationToServer(String serverSId, RemoteLocalServer localServer) throws RemoteException {
		return false;
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
