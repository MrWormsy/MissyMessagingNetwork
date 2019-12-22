package fr.mrwormsy.proj731.chatprojectserver.gui;

import fr.mrwormsy.proj731.chatprojectserver.Client;
import fr.mrwormsy.proj731.chatprojectserver.ClientMain;
import fr.mrwormsy.proj731.chatprojectserver.RemoteClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ClientGUI extends JFrame {

	/* * */
	private static final long serialVersionUID = -828370991064425330L;
	
	// All the variables
	private JPanel writeAndSendPanel;
	private JTextArea chatDisplay;
	private JTextField chatWritter;
	private JButton sendMessageButton;
	private JPanel sendPanel;
	private JScrollPane displayScrollPanel;
	private JComboBox<String> onlineUsers;

	private JMenu peopleMenu;
	private ArrayList<JMenu> peoples;

	private JMenu conversationsMenu;
	private ArrayList<JMenuItem> conversations;
	private String currentConversation;

	private ClientGUI clientGUI;

	private RemoteClient client;

	// The password which is hashed with md5
	private String username;
	private String password;

	// The constructor needs a bavard and a name
	public ClientGUI(Client client) {

		this.client = client;

		// This is used to get the instance of the object inside the Listeners
		setClientGUI(this);

		// Basic things
		this.setTitle("Logged Out");
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);
		this.setResizable(false);

		// We don't want the program to finish, just the window to close, this is why we
		// are using DISPOSE_ON_CLOSE instead of EXIT_ON_CLOSE
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// If the person closes the window we inform the Concierges who are listening to
		// the bavard, that his window has been closed
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent windowEvent) {
				try {
					System.out.println(client.getUsername() + " Logged Out");
					client.logOut();

					// TODO EXIT ?
					System.exit(0);

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// Content of the frame, no need to explain
		this.chatDisplay = new JTextArea();
		this.chatDisplay.setEditable(false);

		displayScrollPanel = new JScrollPane(this.chatDisplay, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.displayScrollPanel.setPreferredSize(new Dimension(594, 321));

		this.onlineUsers = new JComboBox<>(new String[]{});
		this.onlineUsers.setBounds(25, 370, 25, 20);

		this.chatWritter = new JTextField("Message");
		this.chatWritter.setBounds(75, 370, 425, 20);

		this.sendMessageButton = new JButton("Send");
		this.sendMessageButton.setBounds(525, 370, 50, 20);

		this.writeAndSendPanel = new JPanel();
		this.writeAndSendPanel.setPreferredSize(new Dimension(10, 10));
		this.sendPanel = new JPanel();


		// Menu menu
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		JMenu mJMenu = new JMenu("Menu");
		menuBar.add(mJMenu);

		JMenuItem menuSignIn = new JMenuItem("Sign In");
		mJMenu.add(menuSignIn);
		
		menuSignIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SignUpGUI();
			}
		});
		
		JMenuItem menuLogIn = new JMenuItem("Log In");
		mJMenu.add(menuLogIn);
		
		menuLogIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new LogInGUI();
			}
		});
		
		JMenuItem menuLogOut = new JMenuItem("Log Out");
		mJMenu.add(menuLogOut);

		menuLogOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println(client.getUsername() + " Logged Out");

					client.logOut();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}

				// TODO EXIT ?
				System.exit(0);
			}
		});


		// Peoples menu

		peopleMenu = new JMenu("Peoples");
		menuBar.add(peopleMenu);

		peoples = new ArrayList<JMenu>();

		// Conversations menu

		conversationsMenu = new JMenu("Conversations");
		menuBar.add(conversationsMenu);

		conversations = new ArrayList<JMenuItem>();


		// We are now using a GroupLayout which is pretty hard to explain and to deal
		// with, but we finally succeed :D
		writeAndSendPanel.setLayout(new BoxLayout(writeAndSendPanel, BoxLayout.Y_AXIS));
		writeAndSendPanel.add(this.displayScrollPanel);
		writeAndSendPanel.add(sendPanel);

		GroupLayout groupLayout = new GroupLayout(sendPanel);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		sendPanel.setLayout(groupLayout);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup().addComponent(this.onlineUsers).addComponent(this.chatWritter).addComponent(this.sendMessageButton));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(this.onlineUsers).addComponent(this.chatWritter).addComponent(this.sendMessageButton));

		this.setContentPane(writeAndSendPanel);

		// We add an ActionListener to the button used to send messages
		this.sendMessageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// If the message contains something
				if (!chatWritter.getText().isEmpty()) {

					// TODO OLD ISSUE
					/*
					try {




						// If it return false that means that the message could not be sent and thus the receiver does not exists
						if (ChatClient.getTheServer().sendMessage(getUsername(), (String) onlineUsers.getSelectedItem(), chatWritter.getText())) {
							writeMessage(getUsername(), chatWritter.getText());
						} else {
							logMessage("User does not exists");
						}



					} catch (RemoteException e1) {
						e1.printStackTrace();
					}

					*/

					// System.out.println("I've removed this part check todos");

					// We want to send a message to the selected conversation
					try {

						// We check id the sever exists
						if (client.getLocalServers().containsKey(currentConversation)) {
							client.getLocalServers().get(currentConversation).sendMessage(username, chatWritter.getText());
						} else {
							System.out.println("This server does not exists...");
						}


					} catch (RemoteException ex) {
						ex.printStackTrace();
					}

					// We reset the text of the chat writter
					chatWritter.setText("");
				}
			}
		});

		// We set a default button, thanks to this we only need to press enter and the message will be sent
		this.writeAndSendPanel.getRootPane().setDefaultButton(this.sendMessageButton);
	}

	// TODO IMPORTANT HERE
	public void createPeopleForMenuByName(String name) {

		// We create a user menu
		JMenu dummyUser = new JMenu(name);

		// And we add it to the list of users
		peoples.add(dummyUser);

		// And we add it to the main menu
		peopleMenu.add(dummyUser);

		JMenuItem startConversation = new JMenuItem("Start Conversation");
		startConversation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

                try {
                    client.startServerWith(name);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }

            }
		});


		dummyUser.add(startConversation);
	}

	// Return true if a person is already in the menu (not to add it twice)
	public boolean isUserAlreadyInPeopleMenu(String name) {

		for(JMenu jMenu : peoples) {
			if (jMenu.getText().equalsIgnoreCase(name)) {
				return true;
			}
		}

		return false;

	}

	public boolean isConversationsAlreadyInConversationsMenu(String conv) {
		for(JMenuItem jMenuItem : conversations) {
			if (jMenuItem.getText().equalsIgnoreCase(conv)) {
				return true;
			}
		}

		return false;
	}

	public void createConversationsForMenuByName(String conv) {

		// We create a conv menu
		JMenuItem dummyConv = new JMenuItem(conv);

		// And we add it to the list of conversations
		conversations.add(dummyConv);

		// And we add it to the main menu
		conversationsMenu.add(dummyConv);

		dummyConv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				System.out.println(conv);

				setCurrentConversation(conv);
			}
		});
	}

	private void logMessage(String message) {
		this.chatDisplay.setText(this.chatDisplay.getText() + "\n log : " + message);
	}

	public void writeMessage(String from, String message) {
		this.chatDisplay.setText(this.chatDisplay.getText() + "\n" + from + " wrote " + message);
	}

	// Getters and Setters...
	public JPanel getWriteAndSendPanel() {
		return writeAndSendPanel;
	}

	public void setWriteAndSendPanel(JPanel writeAndSendPanel) {
		this.writeAndSendPanel = writeAndSendPanel;
	}

	public JTextArea getChatDisplay() {
		return chatDisplay;
	}

	public void setChatDisplay(JTextArea chatDisplay) {
		this.chatDisplay = chatDisplay;
	}

	public JTextField getChatWritter() {
		return chatWritter;
	}

	public void setChatWritter(JTextField chatWritter) {
		this.chatWritter = chatWritter;
	}

	public JButton getSendMessageButton() {
		return sendMessageButton;
	}

	public void setSendMessageButton(JButton sendMessageButton) {
		this.sendMessageButton = sendMessageButton;
	}

	public JPanel getSendPanel() {
		return sendPanel;
	}

	public void setSendPanel(JPanel sendPanel) {
		this.sendPanel = sendPanel;
	}

	public JScrollPane getDisplayScrollPanel() {
		return displayScrollPanel;
	}

	public void setDisplayScrollPanel(JScrollPane displayScrollPanel) {
		this.displayScrollPanel = displayScrollPanel;
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

	public JComboBox<String> getOnlineUsers() {
		return onlineUsers;
	}

	public void setOnlineUsers(JComboBox<String> onlineUsers) {
		this.onlineUsers = onlineUsers;
	}

	public void setClientGUI(ClientGUI clientGUI) {
		this.clientGUI = clientGUI;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCurrentConversation() {
		return currentConversation;
	}

	public void setCurrentConversation(String currentConversation) {
		this.currentConversation = currentConversation;
	}
}
