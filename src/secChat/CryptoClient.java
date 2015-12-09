package secChat;


import java.io.*;
import java.net.*;

import javax.swing.*;

import java.awt.*;



public class CryptoClient extends SecWindow{
	
	// Chat objects
	private BufferedReader in;
	private PrintWriter out;
	private Socket connection;
	private String server = "127.0.0.1";
	private String serverAddress;			// Stores IP Address
	
	// Server Port for application
	private static final int appPort = 9960;

	
	public CryptoClient()
	{
		super("Encrypted Chat");
		mute();
		muteChat();
		
	}
	public void run()
	{
		try{
			connectToServer();
			getStreams();
			processConnection();
			
			
		} catch(EOFException e){}
		catch(IOException io) {}
		finally{closeConnection();}
		
		
		
	}
	
	public void connectToServer() throws IOException
	{
		appendDisplay("Attempting Connection");
	}
	
	public void getStreams()throws IOException
	{
		
	}
	
	public void processConnection() throws IOException
	{
		
	}
	
	public void closeConnection()
	{
		
	}
	
	public static void main(String[] args)
	{
		CryptoClient client = new CryptoClient();
		
	}
}
