/**
 * 
 */
package secChat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.*;

/**
 * @author Charles
 *
 */
public class CryptoServer extends JFrame {
	
	// GUI Objects
	private JTextField textField;
	private JTextArea displayArea;
	private JTextField ipField;
	
	// Chat Objects
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	private String message;
	
	// Port Number
	private static final int appPort = 9960;
	
	public CryptoServer(){
		
		// Initialize window
		super("SecChat Server");
		
		//GUI Setup
		textField = new JTextField();
		displayArea = new JTextArea();
		ipField = new JTextField();
		
		// Add ActionListener to chat field
		textField.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendData(event.getActionCommand());
						textField.setText("");
					}
				}
				);
		
		// Set Layout
		add(ipField, BorderLayout.NORTH);
		add(new JScrollPane(displayArea), BorderLayout.CENTER);
		add(textField, BorderLayout.SOUTH);
		
		//Set window size
		setSize(300, 150);
		setVisible(true);
		
		// Set IP Fields
		ipField.setText("Not Connected");
		ipField.setEditable(false);
	}
	
	public static void main(String[] args) {
		
		CryptoServer server = new CryptoServer();
		server.run();

	}
	
	// Run Server
	void run()
	{
		
		try{
		
			// Create server socket for connection
			server = new ServerSocket(appPort);
			
			// Loop to keep waiting for connection
			while(true)
			{
				// Messaging protocol
				try{
					waitForConnections();
					getStreams();
					processConnection();
					// verifyConnection();
					
				} catch (IOException e){displayMessage("\nConnection terminated\n");}
				finally{
					closeConnection();
				}
			}	
		} catch (IOException io)
		{
			io.printStackTrace();
		}
	}
	
	// Waits for a connection from client
	private void waitForConnections() throws IOException{
		displayMessage("Waiting for connection...\n");
		connection = server.accept();
		displayMessage("Connection recieved from: " + connection.getInetAddress().getHostName());
		ipField.setText("Connected to: " + connection.getInetAddress().getHostName());
	}
	
	// Gets the connection streams
	private void getStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		
		input = new ObjectInputStream(connection.getInputStream());
		
		displayMessage("\nIO Streams Open\n");
	}
	
	
	// Encryption
	private void processConnection() throws IOException {
		
		// Send connection successful message
		message = "Connection Successful, Sending key";
		sendData(message);
		
		
		
		do{
			try{
				message = (String) input.readObject();
				this.displayMessage(message);
			} catch (ClassNotFoundException classNotFoundException)
			{
				displayMessage("Unknown type recieved");
			} 
		} while (!message.equals("CLIENT>>> TERMINATE"));
		
	}
	
	// Closes the open connection between client and server
	private void closeConnection()
	{
		
		displayMessage("\nTerminating Connection...\n");
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run()
				{textField.setEditable(false);}
			}
		);
	}
	
	//////////////////////
	/* Helper Functions */
	//////////////////////

	// Display message on screen
	private void displayMessage(final String m)
	{
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
					displayArea.append("\n" + m);
			}
		});
	}
	
	// Sends message to client
	private void sendData(String message){
		try{
			
			output.writeObject("SERVER: " + message);
			output.flush();
			displayMessage("Server: " + message);
		} catch (IOException e){displayMessage("Error sending message! \n");}
		
	}
	

}
