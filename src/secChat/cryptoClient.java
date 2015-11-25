package secChat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class cryptoClient {

	// GUI Objects
	private JTextField userInput = new JTextField(100);
	private JTextArea messageDisplay = new JTextArea(8, 40);
	private JFrame frame = new JFrame("Crypto Messaging Client");
	
	// Chat objects
	private BufferedReader in;
	private PrintWriter out;
	private Socket connection;
	private String server;
	
	// Server Port for application
	private static final int serverPort = 9960;
	
	public cryptoClient()
	{
		// Sets both display area and text field to be uneditable until connection secured
		userInput.setEditable(false);
		messageDisplay.setEditable(false);
		
		// Frame Layout
		frame.getContentPane().add(userInput, "SOUTH");
		frame.getContentPane().add(new JScrollPane(messageDisplay), "CENTER");
		frame.pack();
		
		// Event Listener
		userInput.addActionListener(new ActionListener(){

			/**
			 * Action Listener encrypts, prints to output and also clears text for next message
			 */
			public void actionPerformed(ActionEvent e) {
				
				// Add Encryption Here??
				
				out.println(userInput.getText());
				userInput.setText("");	
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
		
		client.run();	
	}

}
