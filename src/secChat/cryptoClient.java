package secChat;


import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.*;



public class cryptoClient {

	// GUI Objects
	private secWindow window = new secWindow();
	
	// Chat objects
	private BufferedReader in;
	private PrintWriter out;
	private Socket connection;
	private String server;
	private String serverAddress;			// Stores IP Address
	
	// Server Port for application
	private static final int appPort = 9960;
	
	public cryptoClient()
	{

		
		// Event Listener
		window.addActionListener(new ActionListener(){

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
