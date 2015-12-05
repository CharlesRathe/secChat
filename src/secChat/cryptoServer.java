/**
 * 
 */
package secChat;

import java.awt.BorderLayout;
import java.io.*;
import java.net.*;

import javax.swing.*;

/**
 * @author Charles
 *
 */
public class cryptoServer {

	// GUI Objects
	private JTextField userInput = new JTextField(100);
	private JTextArea messageDisplay = new JTextArea();
	private JFrame frame = new JFrame("Crypto Messaging Client");
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private static final int appPort = 9960;
	private ServerSocket server;
	private Socket connection;
	
	public cryptoServer() throws Exception{

		userInput.setEditable(false);
		messageDisplay.setEditable(false);
		
		frame.getContentPane().add(userInput, BorderLayout.SOUTH);
		frame.getContentPane().add(new JScrollPane(messageDisplay), BorderLayout.CENTER);
		frame.pack();
		
	}
	void run() throws IOException
	{
		server = new ServerSocket(appPort);
		
		while(true)
		{
			try{
				waitForConnections();
				getStreams();
				verifyConnection();
				
			} catch (IOException e){displayMessage("\nConnection terminated\n");}
			finally{
				closeConnection();
			}
	
		}	
	}
	

	private void verifyConnection() {
		
		
	}
	private void getStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		
		input = new ObjectInputStream(connection.getInputStream());
		
		displayMessage("\nIO Streams Open\n");
		
	}
	private void waitForConnections() throws IOException{
		displayMessage("Waiting for connection...\n");
		connection = server.accept();
		displayMessage("Connection recieved from: " + connection.getInetAddress().getHostName());	
	}
	
	private void closeConnection()
	{
		
		displayMessage("\nTerminating Connection...\n");
		SwingUtilities.invokeLater(
				new Runnable(){
					
					public void run()
					{
						
						userInput.setEditable(false);
					}
				}
	);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

	}
	
	private void displayMessage(final String m)
	{
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
				messageDisplay.append(m);
			}
		});
	}
	
	private void sendMessage(String message){
		try{
			
			output.writeObject("Server: " + message);
			output.flush();
			displayMessage("\nServer: " + message);
		} catch (IOException e){messageDisplay.append("Error sending message! \n");}
		
	}
	
	private void sendKey(String key){
		try{
			output.writeObject(key);
			output.flush();
			displayMessage("Key Sent\n");
			
		}catch (IOException e){messageDisplay.append("Error sending message! \n");}
	}

}
