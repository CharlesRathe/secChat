package secChat;


import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import java.awt.*;

import net.java.otr4j.OtrEngineHost;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.session.Session;
import net.java.otr4j.session.SessionID;


public class cryptoClient implements OtrEngineHost{

	// Logger for logging errors/warnings etc.
	private final static Logger logger = Logger.getLogger(cryptoClient.class.getName());
	
	// GUI Objects
	private secWindow window;
	
	// OTR Objects
	private Session session;
	private OtrPolicy policy;
	public String lastInjectedMessage;
	
	// Chat objects
	private BufferedReader in;
	private PrintWriter out;
	private Socket connection;
	private String server;
	private String serverAddress;			// Stores IP Address
	
	// Server Port for application
	private static final int appPort = 9960;
	
	public cryptoClient(OtrPolicy p)
	{
		
		this.policy = p;
		window = new secWindow();
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(400,400);
	}
	public void run()
	{
		
		
		
		
	}
	
	
	// Generates Key Pair
	public KeyPair getKeyPair(SessionID sess) {
        KeyPairGenerator kg;
        try {
                kg = KeyPairGenerator.getInstance("DSA");

        } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                logger.log(Level.WARNING, "No such algorithm");
                return null;
        }
        
        logger.log(Level.INFO, "Generated Key Pair");
        return kg.genKeyPair();
	}
	
	public OtrPolicy getSessionPolicy(SessionID sess) {
		return this.policy;
	}
	
	
	public void injectMessage(SessionID sess, String message) {
		// Send message over connection
		
	}
	
	
	//Error Handling
	
	public void showError(SessionID sess, String error) {
		logger.severe("Error: " + error);
	}
	
	public void showWarning(SessionID sess, String warning) {
		logger.warning("Warning: " + warning);	
	}

}
