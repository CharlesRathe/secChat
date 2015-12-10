/**
 * 
 */
package secChat;

import java.io.*;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.awt.*;
import java.awt.event.*;

import javax.crypto.KeyAgreement;
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
	
	// Encryption Objects
	private byte[] encodedPublicKey;
	private byte[] serverPublicKey;
	private byte[] sharedSecret;
	private int secretLength;
	private KeyPair serverKeyPair;
	private CryptoHelper cryptoHelper;
	private PublicKey clientPublicKey;
	private KeyAgreement keyAgreement;
	
	
	// Port Number
	private static final int appPort = 9998;
	
	public CryptoServer(){
		
		// Initialize window
		super("SecChat Server");
		
		// Encryption initialization
		cryptoHelper = new CryptoHelper();
		
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
					if (diffieHellman())
					{processConnection();}
					
				} catch (IOException e){displayMessage("\nConnection terminated\n");} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidAlgorithmParameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
		message = "Connection Successful, Sending key";
		sendData(message);
	}
	
	private boolean diffieHellman() throws ClassNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException
	{
		do{
			encodedPublicKey = (byte[])input.readObject();
		}while(encodedPublicKey == null);
		
		clientPublicKey = cryptoHelper.getClientPublicKey(encodedPublicKey);
		
		serverKeyPair = cryptoHelper.getServerKeyPair(clientPublicKey);
		keyAgreement = cryptoHelper.getKeyAgreement(serverKeyPair);
		
		serverPublicKey = cryptoHelper.encodeServerKey(serverKeyPair);
		
		sendKey(serverPublicKey);
		
		cryptoHelper.getClientKey(clientPublicKey, keyAgreement); // Do phase 1
		
		
		
	}
	
	private void sendKey(byte[] encodedKey) throws IOException
	{
		output.writeObject(encodedKey);
		output.flush();
	}
	
	// Process
	private void processConnection() throws IOException {	
		
		displayMessage("Session started, chat is encrypted");
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
	
	///////////////////
	// Private Class //
	///////////////////
	
	class OtrEngineHostImpl implements OtrEngineHost
	{
		private OtrPolicy policy;
		public String lastInjectedMessage;
		
		public OtrEngineHostImpl(OtrPolicy policy)
		{
			this.policy = policy;
		}

	
		public KeyPair getKeyPair(SessionID arg0) {
            KeyPairGenerator kg;
            try {
                    kg = KeyPairGenerator.getInstance("RSA");

            } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return null;
            }

            return kg.genKeyPair();
		}
	
		
		public OtrPolicy getSessionPolicy(SessionID arg0) {
			return this.policy;
		}
	
		
		public void injectMessage(SessionID id, String message) {
			sendData(message);
			
		}
	
		
		public void showError(SessionID arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}
	
		
		public void showWarning(SessionID arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

	}

	

}
