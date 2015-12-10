/**
 * 
 */
package secChat;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.awt.*;
import java.awt.event.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
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
	private byte[] message;
	
	// Encryption Objects
	private byte[] encodedClientPublicKey;
	private byte[] encodedServerPublicKey;
	private byte[] serverSharedSecret;
	private byte[] clientSharedSecret;
	private int secretLength = 0;
	private KeyPair serverKeyPair;
	private CryptoHelper cryptoHelper;
	private PublicKey clientPublicKey;
	private KeyAgreement keyAgreement;
	private SecretKey serverAESKey;
	
	
	// Port Number
	private static final int appPort = 9991;
	
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
						byte[] enc;
						try {
							enc = encryptMessage(event.getActionCommand());
							sendData(enc);
						} catch (IllegalBlockSizeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (BadPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidKeyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

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
					diffieHellman();
					processConnection();
					
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
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ShortBufferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
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

	}
	
	private void diffieHellman() throws ClassNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalStateException, ShortBufferException
	{
		do{
			encodedClientPublicKey = (byte[])input.readObject();
		}while(encodedClientPublicKey == null);
		
		clientPublicKey = cryptoHelper.getClientPublicKey(encodedClientPublicKey);
		
		encodedClientPublicKey = null;
		
		serverKeyPair = cryptoHelper.getServerKeyPair(clientPublicKey);
		keyAgreement = cryptoHelper.getKeyAgreement(serverKeyPair);
		
		encodedServerPublicKey = cryptoHelper.encodeServerKey(serverKeyPair);
		
		displayMessage("Sending Encoded Key: ");
		sendKey(encodedServerPublicKey);
		
		cryptoHelper.getClientKey(clientPublicKey, keyAgreement); // Do phase 1
		
		do{
			secretLength = input.readInt();
			
		}while(secretLength == 0);
		
		displayMessage("Secret Length: " + secretLength);
		
		serverSharedSecret = new byte[secretLength];
		
		cryptoHelper.getServerSecret(serverSharedSecret, keyAgreement);
		
		secretLength = 0;

		displayMessage("Server Secret: " + toHexString(serverSharedSecret));
		
		do{
			clientSharedSecret = (byte[]) input.readObject();
			System.out.println("In do loop: " + toHexString(clientSharedSecret));
			
		}while(clientSharedSecret == null);
		
		displayMessage("Client Secret: " + toHexString(clientSharedSecret));
		
		
	}
	
	private void sendKey(byte[] encodedKey) throws IOException
	{
		output.writeObject(encodedKey);
		output.flush();
	}

	
	// Process
	private void processConnection() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {	
		
		keyAgreement.doPhase(clientPublicKey, true);
		serverAESKey = keyAgreement.generateSecret("DES");
		
		displayMessage("Secret Key Initialized");
		
		displayMessage("Session started, chat is encrypted");
		textField.setEditable(true);
		
		do{
			try{
				message = (byte[]) input.readObject();
				decryptMessage(message);
			} catch (ClassNotFoundException classNotFoundException)
			{
				displayMessage("Unknown type recieved");
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	public byte[] encryptMessage(String messageToEncrypt) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
	{
		Cipher serverCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		serverCipher.init(Cipher.ENCRYPT_MODE, serverAESKey);
		displayMessage(messageToEncrypt);
		byte[] clearText = messageToEncrypt.getBytes();
		byte[] cipherText = serverCipher.doFinal(clearText);
		return cipherText;
	}
	
	public String decryptMessage(byte[] encMessage) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{
		displayMessage("Decrypting Message");
		Cipher serverCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		serverCipher.init(Cipher.DECRYPT_MODE,  serverAESKey);
		
		byte[] recovered = serverCipher.doFinal(encMessage);
		String clearMessage = recovered.toString();
		displayMessage(clearMessage);
		return clearMessage;
	}
	
	// Display message on screen
	private void displayMessage(final String m)
	{
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
					displayArea.append("\n" + m + "\n");
			}
		});
	}
	
	// Sends message to client
	private void sendData(byte[] message){
		try{
			
			output.writeObject(message);
			output.flush();
		} catch (IOException e){displayMessage("Error sending message! \n");}
		
	}
	
	 /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                            '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }


    /*
     * Converts a byte array to hex string
     */
    private String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();

        int len = block.length;

        for (int i = 0; i < len; i++) {
             byte2hex(block[i], buf);
             if (i < len-1) {
                 buf.append(":");
             }
        }
        return buf.toString();
    }

	

}
