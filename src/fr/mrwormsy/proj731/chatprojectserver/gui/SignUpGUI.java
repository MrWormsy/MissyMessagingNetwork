package fr.mrwormsy.proj731.chatprojectserver.gui;

import fr.mrwormsy.proj731.chatprojectserver.ClientMain;
import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class SignUpGUI {
    // Variables
    private JFrame frame;
    private JPasswordField confirmInput;
    private JTextField usernameInput;
    private JPasswordField passInput;
    private JLabel usernameLabel;
    private JLabel passLabel;
    private JLabel confirmLabel;

    private ClientGUI clientGUI;

    // This Gui is used to create a new Client with a username and two fields for the password
    public SignUpGUI() {

        frame = new JFrame("Sign In");
        frame.setBounds(100, 100, 300, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        usernameLabel = new JLabel("Username");
        passLabel = new JLabel("Password");
        confirmLabel = new JLabel("Confirm Password");

        confirmInput = new JPasswordField();
        confirmInput.setColumns(10);
        confirmInput.setEchoChar('•');

        usernameInput = new JTextField();
        usernameInput.setColumns(10);

        passInput = new JPasswordField();
        passInput.setColumns(10);
        passInput.setEchoChar('•');

        // The group layout

        JButton registerButton = new JButton("Register Client");
        GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(26).addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(confirmLabel).addComponent(usernameLabel).addComponent(passLabel)).addGap(18).addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false).addComponent(registerButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(confirmInput).addComponent(passInput).addComponent(usernameInput)).addContainerGap(78, Short.MAX_VALUE)));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(28).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(usernameLabel).addComponent(usernameInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(18).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(passLabel).addComponent(passInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(18).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(confirmLabel).addComponent(confirmInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(32).addComponent(registerButton).addContainerGap(33, Short.MAX_VALUE)));

        frame.getContentPane().setLayout(groupLayout);

        frame.setVisible(true);

        // We add an ActionListener when the register button is clicked
        registerButton.addActionListener(new ActionListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void actionPerformed(ActionEvent e) {

                // We check that all the fields are not empty
                if (usernameInput.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Incorect username !");
                    return;
                }

                if (passInput.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Password username !");
                    return;
                }

                // Check if the user already exists
                try {


                    // Check if the two passwords are the same (we are using md5 as the encryption method)
                    String md5HexPass = DigestUtils.md5Hex(passInput.getText());
                    String md5HexConfirm = DigestUtils.md5Hex(confirmInput.getText());

                    // If the passwords are the same
                    if (md5HexConfirm.equals(md5HexPass)) {

                        // TODO OLD ISSUE HERE
                        //ChatClient.getTheUser().createUserAccount(usernameInput.getText(), md5HexPass);
                        ClientMain.getTheClient().createUserAccount(usernameInput.getText(), md5HexPass);

                        JOptionPane.showMessageDialog(frame, "Your account has been added, you just have to login now");
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "The passwords do not match !");
                    }


                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Set the default button
        frame.getRootPane().setDefaultButton(registerButton);
    }
}
