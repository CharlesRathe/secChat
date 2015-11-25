package secChat;


import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.*;



public class cryptoClient {

	// GUI Objects
	private JTextField userInput = new JTextField(100);
	private JTextArea messageDisplay = new JTextArea();
	private JFrame frame = new JFrame("Crypto Messaging Client");
	
	// Chat objects
	private BufferedReader in;
	private PrintWriter out;
	private Socket connection;
	private String server;
	
	// Server Port for application
	private static final int appPort = 9960;
	
	public cryptoClient()
	{
		// Sets both display area and text field to be uneditable until connection secured
		userInput.setEditable(false);
		messageDisplay.setEditable(false);
		// NOTE: Third Display area may be added here for selecting user? Maybe add this later
		/*
		 * Things we would need:
		 * 
		 * Idea 1) Could have users and an add user button, this would require multiple panels, one of which would have to be layered.
		 * 		   This would also require an add user button.
		 * 		   JObjects which could be selected, each with its own listener in the Event listener section
		 *         Preferably would change color (this might be something we could do if we have time which we probs won't
		 *         
		 * Idea 2) User inputs an IP address which he would like to connect to. Maybe a nickname too.
		 * 		   Could store in a hash and allow the user to pick one in the future if necessary
		 * 		   Simpler, don't even really need to store, but could be used to store the encryption key for future recognition
		 */
		
		// Frame Layout
		frame.getContentPane().add(userInput, BorderLayout.SOUTH);
		frame.getContentPane().add(new JScrollPane(messageDisplay), BorderLayout.CENTER);
		frame.pack();
		
		// Event Listener
		userInput.addActionListener(new ActionListener(){

			/**
			 * Action Listener encrypts, prints to output and also clears text for next message
			 */
			public void actionPerformed(ActionEvent e) {
				
				// Add Encryption Here??
				
				// out.println(userInput.getText());
				// userInput.setText("");	
			}
		});
	}
	
	public String getUserAddress()
	{
		return JOptionPane.showInputDialog(frame,
				"Please Enter IP Address of Desired User:",
				"Welcome to cryptoChat", JOptionPane.QUESTION_MESSAGE);
	}
	
	public void run()
	{
		
		
		
		
	}
	
	
	/**
	 * This driver opens the GUI and connects to the crypto server with the run command
	 * 
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		
		cryptoClient client = new cryptoClient();
		client.frame.setVisible(true);
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setSize(1000, 400);
		
		// client.run();	
	}

}
