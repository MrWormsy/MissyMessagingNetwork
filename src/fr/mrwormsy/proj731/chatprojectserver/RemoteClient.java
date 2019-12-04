package fr.mrwormsy.proj731.chatprojectserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

//Creating Remote interface for our application 
public interface RemoteClient extends Remote {
	void createUserAccount(String username, String password) throws RemoteException;

	void logIn(String username, String password) throws RemoteException;
	void logOut() throws RemoteException;

	void log(String log) throws RemoteException;
	
	String getUsername() throws RemoteException;

	void sendMessage(String from, String message) throws RemoteException;

	void updateOnlinePlayers() throws RemoteException;

	ArrayList<String> getOnlinePersons() throws RemoteException;
}