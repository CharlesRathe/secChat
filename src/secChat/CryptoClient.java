package secChat;


import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
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
	private byte[] message;
	
	//Encryption Objects
	private CryptoHelper cryptoHelper;
	private KeyPair clientKeyPair;
	private KeyAgreement keyAgreement;
	private byte[] encodedClientPublicKey;
	private byte[] encodedServerPublicKey;
	private byte[] clientSharedSecret;
	private int secretLength;
	private PublicKey serverPublicKey;
	private SecretKey clientAESKey;
	

	// Server Port for application
	private static final int appPort = 9991;

	
	public CryptoClient()
	{
		super("Encrypted Chat");
		
		//Encryption Initialization
		cryptoHelper = new CryptoHelper();
		
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
					byte[] enc;
					try {
						enc = encryptMessage(event.getActionCommand());
						sendData(enc);
					} catch (InvalidKeyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalBlockSizeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BadPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

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
			diffieHellman();
			processConnection();
			
		} catch(EOFException e){}
		catch(IOException io) {} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidParameterSpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{closeConnection();}
		
	}
	
	private void diffieHellman() throws NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, IOException, ClassNotFoundException, IllegalStateException, InvalidKeySpecException {
		 DHParameterSpec dh = cryptoHelper.getAlgorithmParameters();
		 clientKeyPair = cryptoHelper.genKeyPair(dh);
		 keyAgreement = cryptoHelper.getKeyAgreement(clientKeyPair);
		 encodedClientPublicKey = cryptoHelper.encodeKey(clientKeyPair);
		 
		 displayMessage("Sending client public key: " + toHexString(encodedClientPublicKey));
		 sendPublicKey(encodedClientPublicKey);
		 
		 do{
			 encodedServerPublicKey = (byte[])input.readObject();
			 displayMessage("Got server public key: ");
			 
		 }while(encodedServerPublicKey == null);
		 
		 serverPublicKey = cryptoHelper.getServerKey(encodedServerPublicKey, keyAgreement);
		 
		 clientSharedSecret = cryptoHelper.getSharedSecret(keyAgreement);
		 secretLength = cryptoHelper.getSecretLength(clientSharedSecret);
		 
		 displayMessage("Sending Length: "+ Integer.toString(secretLength));
		 sendLength(secretLength);
		 displayMessage("Sending shared secret: "+ toHexString(clientSharedSecret));
		 sendPublicKey(clientSharedSecret);
		 
		 displayMessage("Diffie Hellman Complete");
		 
	}
	
	public String decryptMessage(byte[] encMessage) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		displayMessage("Decrypting Message ...");
		Cipher clientCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		clientCipher.init(Cipher.DECRYPT_MODE,  clientAESKey);
		
		
		byte[] recovered = clientCipher.doFinal(encMessage);
		String clearMessage = recovered.toString();
		displayMessage(clearMessage);
		return clearMessage;
	}
	
	public byte[] encryptMessage(String messageToEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException
	{
		Cipher clientCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		clientCipher.init(Cipher.ENCRYPT_MODE, clientAESKey);
		displayMessage(messageToEncrypt);
		byte[] clearText = messageToEncrypt.getBytes();
		byte[] cipherText = clientCipher.doFinal(clearText);
		return cipherText;
	}
	
	private void sendPublicKey(byte[] publicKey) throws IOException
	{
		output.writeObject(publicKey);
		output.flush();
	}
	
	private void sendLength(int secretLength) throws IOException
	{
		output.writeInt(secretLength);
		output.flush();
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
	
	
	public void processConnection() throws IOException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, NoSuchPaddingException
	{
		keyAgreement.doPhase(serverPublicKey, true);
		clientAESKey = keyAgreement.generateSecret("DES");
		
		displayMessage("Secret Key initialized");
		
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
		} while (!message.equals("SERVER>>> TERMINATE"));
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
	
	// Sends message to client
	private void sendData(byte[] message){
		try{
			
			output.writeObject(message);
			output.flush();
		} catch (IOException e){displayMessage("Error sending message! \n");}
		
	}
	
	public void displayMessage(final String m)
	{
		
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						displayArea.append("\n" + m + "\n");
					}
				}
				);
	}
	
	public static void main(String[] args)
	{
		CryptoClient client = new CryptoClient();
		client.run();
		
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
