package secChat;


import java.io.*;
import java.net.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class CryptoClient extends JFrame{
	
	// GUI Objects
	private JTextField textField;
	private JTextArea displayArea;
	private JTextField ipField;
	
	// Chat objects
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	private String server = "127.0.0.1";
	private String message = "";
	
	// Server Port for application
	private static final int appPort = 9960;

	
	public CryptoClient()
	{
		super("Encrypted Chat");
		
		// GUI Setup
		textField = new JTextField();
		displayArea = new JTextArea();
		ipField = new JTextField();
		
		// Set initial fields
		textField.setEditable(false);
		displayArea.setEditable(false);
		ipField.setText("No connection");
		ipField.setEditable(false);
		
		// Add ActionListener
		textField.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event)
				{
					sendData(event.getActionCommand());
					textField.setText("");
				}
			}
		);
		
		// Set Layout
		add( ipField, BorderLayout.NORTH);
		add( new JScrollPane(displayArea), BorderLayout.CENTER);
		add( textField, BorderLayout.SOUTH);
		
		// Set window
		setSize(300, 150);
		setVisible(true);
	}
	
	
	public void run()
	{
		// Catch IO Exception on setup
		try{
			connectToServer();
			getStreams();
			processConnection();
			
		} catch(EOFException e){}
		catch(IOException io) {}
		finally{closeConnection();}
		
	}
	
	// Connects to server (Hard-coded right now to be on this computer)
	public void connectToServer() throws IOException
	{
		// Show that a connection is being attempted
		displayMessage("Attempting Connection");
	
		// Attempt to connect to server
		connection = new Socket(InetAddress.getByName(server), appPort);
		
		// Show if connected
		displayMessage("Connected to " + connection.getInetAddress().getHostName());
		ipField.setText("Connected to: " + server);
	}
	
	public void getStreams()throws IOException
	{
		// Set up output stream, flush
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		
		// Set up input stream
		input = new ObjectInputStream(connection.getInputStream());
		
		// Display if successful
		displayMessage("Streams successfully set up");
	}
	
	public void processConnection() throws IOException
	{
		// allow user to type in field
		textField.setEditable(true);
		
		do{
			try{
				message = (String) input.readObject();
				displayMessage(message);
			} catch( ClassNotFoundException cnfException){
				displayMessage("Object of unknown type");
			}
			
			
			
		} while (!message.equals("SERVER: TERMINATE"));
	}
	
	public void closeConnection()
	{
		displayMessage("Closing connection...");
		textField.setEditable(false);
		ipField.setText("No connection");
		
		try{
			output.close();
			input.close();
			connection.close();
		} catch( IOException io)
		{
			io.printStackTrace();
		}
	}
	
	public void sendData( String message)
	{
		try{
			output.writeObject("CLIENT: " + message);
			output.flush();
			displayMessage("CLIENT: " + message);
			
		} catch (IOException io)
		{
			displayArea.append("\nCould not write to Server");
		}
		
	}
	
	public void displayMessage(final String m)
	{
		
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						displayArea.append("\n" + m);
					}
				}
				);
	}
	
	public static void main(String[] args)
	{
		CryptoClient client = new CryptoClient();
		client.run();
		
	}
}
