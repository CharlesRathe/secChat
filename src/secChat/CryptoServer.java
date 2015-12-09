/**
 * 
 */
package secChat;

import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 * @author Charles
 *
 */
public class CryptoServer extends SecWindow {
	
	// Chat Objects
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	// Port Number
	private static final int appPort = 9960;
	
	public CryptoServer(){
		
		// Initialize window
		super("SecChat Client");
		
		// Set IP Fields
		this.setIP("No user connected");
		this.muteIP();
	}
	
	public static void main(String[] args) {
		

	}
	
	// Run Server
	void run() throws IOException
	{
		// Create server socket for connection
		server = new ServerSocket(appPort);
		
		// Loop to keep waiting for connection
		while(true)
		{
			// Messaging protocol
			try{
				waitForConnections();
				getStreams();
				// verifyConnection();
				
			} catch (IOException e){displayMessage("\nConnection terminated\n");}
			finally{
				closeConnection();
			}
		}	
	}
	
	// Waits for a connection from client
	private void waitForConnections() throws IOException{
		displayMessage("Waiting for connection...\n");
		connection = server.accept();
		displayMessage("Connection recieved from: " + connection.getInetAddress().getHostName());	
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
		String message = "Connection Successful, Sending key";
		sendMessage(message);
		
		this.unmute();
		
		do{
			try{
				message = (String) input.readObject();
				this.displayMessage(message);
			} catch (ClassNotFoundException classNotFoundException)
			{
				displayMessage("Unknown type recieved");
			} 
		} while (!message.equals( "CLIENT>>> TERMINATE"));
		
	}
	
	// Closes the open connection between client and server
	private void closeConnection()
	{
		
		displayMessage("\nTerminating Connection...\n");
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run()
				{mute();}
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
					appendDisplay(m);
			}
		});
	}
	
	// Sends message to client
	private void sendMessage(String message){
		try{
			
			output.writeObject("Server: " + message);
			output.flush();
			displayMessage("\nServer: " + message);
		} catch (IOException e){appendDisplay("Error sending message! \n");}
		
	}
	
	/*private void sendKey(String key){
		try{
			output.writeObject(key);
			output.flush();
			displayMessage("Key Sent\n");
			
		}catch (IOException e){window.appendDisplay("Error sending message! \n");}
	}
	*/
}
