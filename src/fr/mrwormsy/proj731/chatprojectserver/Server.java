package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server implements RemoteServer {

	public ArrayList<RemoteClient> users;
	
	public Server() {
		this.users = new ArrayList<RemoteClient>();
	}

	@Override
	public void log(String message) throws RemoteException {
		System.out.println(message);
		
	}
	
	// Return true if the user exists in the database
	@Override
	public boolean userExists(String username) throws RemoteException {		
		for (RemoteClient user : users) {
			if (user.getUsername().equalsIgnoreCase(username)) {
				return true;
			}
		}
		return false;
	}

	// Return true if a RemoteClient instance is already logged in the server
	@Override
	public boolean isUserAlreadyLoggedIn(String username) throws RemoteException {

		for (RemoteClient remoteClient : this.users) {
			if (remoteClient.getUsername().equalsIgnoreCase(username)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void logInUser(String username) throws RemoteException {
		try {
			RemoteClient remoteClient = (RemoteClient) ChatServer.registry.lookup(username);
			this.users.add(remoteClient);
			this.log("Client " + username + " has been logged in to the server's database");

			// Now we need to update the online users for all online users
			ArrayList<String> onlines = getAllOnlinePlayers();

			for (RemoteClient rc : this.users) {
				rc.updateOnlinePlayers(onlines);
			}

		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean logOut(String username) throws RemoteException {
		try {
			ChatServer.registry.unbind(username);
			ChatServer.theServer.log("User " + username + " has logged out");

			// Now we need to update the online users for all online users
			ArrayList<String> onlines = getAllOnlinePlayers();

			for (RemoteClient rc : this.users) {
				rc.updateOnlinePlayers(onlines);
			}

			return true;
		} catch (NotBoundException e) {
			return false;
		}
	}

	@Override
	public boolean sendMessage(String from, String to, String message) throws RemoteException {
		RemoteClient client = getUserFromUsername(to);

		if (client != null) {
			client.sendMessage(from, message);
			return true;
		}

		return false;
	}

	@Override
	public ArrayList<String> getAllOnlinePlayers() throws RemoteException {
		ArrayList<String> onlines = new ArrayList<String>();

		for (RemoteClient remoteClient : this.users) {
			onlines.add(remoteClient.getUsername());
		}

		return onlines;
	}

	@Override
	public RemoteClient getUserFromUsername(String username) throws RemoteException {
		for(RemoteClient remoteClient : this.users) {
			if (remoteClient.getUsername().equalsIgnoreCase(username)) {
				return remoteClient;
			}
		}

		return null;
	}


}
