package fr.mrwormsy.proj731.chatprojectserver.gui;

import fr.mrwormsy.proj731.chatprojectserver.Client;
import fr.mrwormsy.proj731.chatprojectserver.RemoteClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientGUI extends JFrame {

    /* * */
    private static final long serialVersionUID = -828370991064425330L;

    // All the variables
    private JPanel writeAndSendPanel;

    // Here we have to change that because we will no longer have a single text area  but a text area for every conversations.
    // We will continue to use the JTextArea to diplay things but it will be stored inside the hashmap
    private JTextArea chatDisplay;
    private HashMap<String, String> conversationsData;


    private JTextField chatWritter;
    private JButton sendMessageButton;
    private JPanel sendPanel;
    private JScrollPane displayScrollPanel;
    private JComboBox<String> onlineUsers;

    private JMenuBar menuBar;

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
        this.conversationsData = new HashMap<String, String>();

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
        menuBar = new JMenuBar();
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

                    // We want to send a message to the selected conversation
                    try {

                        // We check id the sever exists
                        if (client.getLocalServers().containsKey(currentConversation)) {
                            client.getLocalServers().get(currentConversation).sendMessage(username, chatWritter.getText());
                        } else {
                            System.out.println("This server does not exist...");
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

        for (JMenu jMenu : peoples) {
            if (jMenu.getText().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;

    }

    public boolean isConversationsAlreadyInConversationsMenu(String conv) {
        for (JMenuItem jMenuItem : conversations) {
            if (jMenuItem.getText().equalsIgnoreCase(conv)) {
                return true;
            }
        }

        return false;
    }

    public void createConversationsForMenuByName(String conv) {
        // We create a conv menu
        JMenu dummyConv = new JMenu(conv);

        // And we add it to the list of conversations
        conversations.add(dummyConv);

        // And we add it to the main menu
        conversationsMenu.add(dummyConv);

        JMenuItem selectConversation = new JMenuItem("Select conversation");

        selectConversation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // When we choose a conversation we need to set the current one to this one, change the title and show the conversation
                setCurrentConversation(conv);
                setTitle(username + " @ " + conv);
                showConversation(conv);
            }
        });

        JMenuItem addUserToConversation = new JMenuItem("Add user to conversation");

        // Here we add a user to the given conversation (ONLY IF THE PERSON IS THE OWNER OF THE CONVERSATION)
        addUserToConversation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // We need to gather all the users except the ones who are already in the conversation...
                try {

                    ArrayList<String> temp = new ArrayList<>();

                    for (String theUser : client.getOnlinePersons()) {

                        if (!client.getLocalServers().get(conv).containsUser(theUser) && !theUser.equalsIgnoreCase(username)) {
                            temp.add(theUser);
                        }
                    }

                    String user = (String) JOptionPane.showInputDialog(null, "Add a user", "Add someone to this conversation",
                            JOptionPane.QUESTION_MESSAGE, null, temp.toArray(), "");

                    try {
                        client.addUserToTheConversation(conv, user);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }

            }
        });

        JMenuItem quitConversation = new JMenuItem("Quit conversation");

        quitConversation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.quitConversation(conv);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        dummyConv.add(selectConversation);

        dummyConv.add(addUserToConversation);

        dummyConv.add(quitConversation);

    }

    // Show the given conversation
    public void showConversation(String conv) {

        // If the conversation exists we can show it
        if (conversationsData.containsKey(conv)) {
            getChatDisplay().setText(conversationsData.get(conv));
            setTitle(username + " @ " + conv);
        } else {
            System.out.println("The given conversation does not exist");
        }
    }

    private void logMessage(String message) {
        this.chatDisplay.setText(this.chatDisplay.getText() + "\n log : " + message);
    }

    public void writeMessage(String conversationId, String from, String message) {
        //this.chatDisplay.setText(this.chatDisplay.getText() + "\n" + from + " wrote " + message);

        // Here we add the text to the conversation
        conversationsData.replace(conversationId, conversationsData.get(conversationId) + "\n" + from + " wrote " + message);

        // And we show the last conversation
        this.chatDisplay.setText(conversationsData.get(conversationId));
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

    public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    public JComboBox<String> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(JComboBox<String> onlineUsers) {
        this.onlineUsers = onlineUsers;
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

    public HashMap<String, String> getConversationsData() {
        return conversationsData;
    }

    public void setConversationsData(HashMap<String, String> conversationsData) {
        this.conversationsData = conversationsData;
    }
}
