package fr.mrwormsy.proj731.chatprojectserver.gui;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.codec.digest.DigestUtils;

import fr.mrwormsy.inf641.epapotage.EPapotage;
import fr.mrwormsy.proj731.chatprojectserver.ChatClient;
import fr.mrwormsy.proj731.chatprojectserver.RemoteClient;

public class LogInGUI {

	private JFrame frame;
	private JTextField usernameInput;
	private JPasswordField passInput;
	private JLabel usernameLabel;
	private JLabel passLabel;

	public LogInGUI() {

		frame = new JFrame("Log in to your account");
		frame.setBounds(100, 100, 300, 185);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		usernameLabel = new JLabel("Username");
		passLabel = new JLabel("Password");

		usernameInput = new JTextField();
		usernameInput.setColumns(10);

		passInput = new JPasswordField();
		passInput.setColumns(10);
		passInput.setEchoChar('•');

		// The group layout

		JButton logInButton = new JButton("Log In");
		logInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(26).addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(usernameLabel).addComponent(passLabel)).addGap(56).addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false).addComponent(logInButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(passInput, Alignment.LEADING).addComponent(usernameInput, Alignment.LEADING)).addContainerGap(81, Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(28).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(usernameLabel).addComponent(usernameInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(18).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(passLabel).addComponent(passInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(18).addComponent(logInButton).addContainerGap(85, Short.MAX_VALUE)));
		frame.getContentPane().setLayout(groupLayout);
		frame.getRootPane().setDefaultButton(logInButton);

		// We add the listener to the log in button
		logInButton.addActionListener(new ActionListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {

				String md5HexPass = DigestUtils.md5Hex(passInput.getText());

				// TODO CHECK IN THE DATABASE IF THE USER HAS THE GOOD CREDENCIALS
				try {
					ChatClient.getTheUser().logIn(usernameInput.getText(), md5HexPass);
					frame.dispose();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}

			}
		});

		frame.setVisible(true);
	}

}
